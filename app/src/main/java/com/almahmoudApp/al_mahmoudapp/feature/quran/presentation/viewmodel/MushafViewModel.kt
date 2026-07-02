package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.core.data.preferences.AppPreferencesDataSource
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.api.AudioReciter
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase.GetMushafPageCountUseCase
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase.GetMushafPageUseCase
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase.GetPageForSurahUseCase
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase.GetQuranVersesUseCase
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase.GetVerseAudioUseCase
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase.GetVerseDetailsUseCase
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.MushafUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class MushafViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMushafPageUseCase: GetMushafPageUseCase,
    private val getMushafPageCountUseCase: GetMushafPageCountUseCase,
    private val getPageForSurahUseCase: GetPageForSurahUseCase,
    private val getQuranVersesUseCase: GetQuranVersesUseCase,
    private val getVerseDetailsUseCase: GetVerseDetailsUseCase,
    private val getVerseAudioUseCase: GetVerseAudioUseCase,
    private val appPreferencesDataSource: AppPreferencesDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(MushafUiState())
    val state: StateFlow<MushafUiState> = _state.asStateFlow()

    /**
     * Cache of fully-loaded pages keyed by page number.
     * Replacing the old Set so we can restore page data when the user
     * swipes back to an already-visited page — fixing the blank-page bug.
     */
    private val pageCache = mutableMapOf<Int, com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.MushafPage>()
    private var loadingPageNumber = -1

    init {
        loadPageCount()
    }

    fun loadPage(pageNumber: Int) {
        val cached = pageCache[pageNumber]
        if (cached != null) {
            _state.update {
                it.copy(
                    isLoading = false,
                    page = cached,
                    currentPage = pageNumber,
                    errorMessage = null,
                    loadedPages = pageCache.toMap(),
                )
            }
            saveLastReadPosition(cached)
            return
        }

        if (loadingPageNumber == pageNumber) return

        loadingPageNumber = pageNumber
        viewModelScope.launch {
            _state.update { it.copy(isLoading = pageCache.isEmpty(), errorMessage = null) }
            getMushafPageUseCase(pageNumber)
                .onSuccess { page ->
                    pageCache[pageNumber] = page
                    loadingPageNumber = -1
                    _state.update {
                        it.copy(
                            isLoading = false,
                            page = page,
                            currentPage = pageNumber,
                            errorMessage = null,
                            loadedPages = pageCache.toMap(),
                        )
                    }
                    saveLastReadPosition(page)
                    preloadAdjacentPages(pageNumber)
                }
                .onFailure { error ->
                    loadingPageNumber = -1
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage,
                        )
                    }
                }
        }
    }

    private fun saveLastReadPosition(page: com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.MushafPage) {
        val surahNo = page.lines.firstNotNullOfOrNull { it.surahNo } ?: 1
        val surahName = page.surahNames.firstOrNull().orEmpty()
        viewModelScope.launch(Dispatchers.IO + NonCancellable) {
            appPreferencesDataSource.setLastReadPage(page.pageNumber)
            appPreferencesDataSource.setLastReadSurah(surahNo)
            if (surahName.isNotEmpty()) {
                appPreferencesDataSource.setLastReadSurahName(surahName)
            }
        }
    }

    private fun preloadAdjacentPages(currentPage: Int) {
        val total = _state.value.totalPages
        val pagesToLoad = listOfNotNull(
            (currentPage - 1).takeIf { it >= 1 && it !in pageCache },
            (currentPage + 1).takeIf { it <= total && it !in pageCache },
            (currentPage - 2).takeIf { it >= 1 && it !in pageCache },
            (currentPage + 2).takeIf { it <= total && it !in pageCache },
        )
        for (page in pagesToLoad) {
            viewModelScope.launch {
                getMushafPageUseCase(page)
                    .onSuccess { p -> pageCache[page] = p }
                    .onFailure { /* silent */ }
            }
        }
    }

    fun navigateToSurah(surahNumber: Int) {
        viewModelScope.launch {
            getPageForSurahUseCase(surahNumber)
                .onSuccess { page ->
                    if (page != null && page > 0) {
                        loadPage(page)
                    } else {
                        loadPage(1)
                    }
                }
                .onFailure {
                    loadPage(1)
                }
        }
    }

    fun loadVerseContent(surahNumber: Int, verseNumber: Int) {
        _state.update { it.copy(isVerseContentLoading = true) }
        viewModelScope.launch {
            getQuranVersesUseCase(surahNumber)
                .onSuccess { verses ->
                    val content = verses.firstOrNull { it.verseNumber == verseNumber }?.content
                    _state.update {
                        it.copy(
                            selectedVerseContent = content,
                            isVerseContentLoading = false,
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isVerseContentLoading = false) }
                }
        }
    }

    fun loadVerseDetails(surahNumber: Int, verseNumber: Int) {
        _state.update { it.copy(isVerseDetailsLoading = true, verseDetailsErrorMessage = null) }
        viewModelScope.launch {
            getVerseDetailsUseCase(surahNumber, verseNumber)
                .onSuccess { details ->
                    _state.update {
                        it.copy(
                            selectedVerseDetails = details,
                            isVerseDetailsLoading = false,
                            verseDetailsErrorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isVerseDetailsLoading = false,
                            verseDetailsErrorMessage = error.localizedMessage,
                        )
                    }
                }
        }
    }

    fun loadVerseAudio(surahNumber: Int, ayahNumber: Int) {
        _state.update { it.copy(isAudioLoading = true, audioError = null) }
        viewModelScope.launch {
            getVerseAudioUseCase(surahNumber, ayahNumber)
                .onSuccess { response ->
                    val reciters = response.audio.map { (id, audio) ->
                        AudioReciterItem(
                            id = id,
                            name = audio.reciter,
                            url = audio.url,
                        )
                    }
                    _state.update {
                        it.copy(
                            availableReciters = reciters,
                            isAudioLoading = false,
                            audioError = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isAudioLoading = false,
                            audioError = error.localizedMessage,
                        )
                    }
                }
        }
    }

    fun playAudio(url: String, reciterName: String) {
        _state.update {
            it.copy(
                isAudioPlaying = true,
                currentPlayingReciter = reciterName,
                currentAudioUrl = url,
            )
        }
    }

    fun stopAudio() {
        _state.update {
            it.copy(
                isAudioPlaying = false,
                currentPlayingReciter = null,
                currentAudioUrl = null,
                isAudioLoading = false,
                availableReciters = emptyList(),
                audioError = null,
            )
        }
    }

    fun onAudioCompleted() {
        _state.update { it.copy(isAudioPlaying = false, currentPlayingReciter = null) }
    }

    fun nextPage() {
        val current = _state.value.currentPage
        if (current < _state.value.totalPages) {
            loadPage(current + 1)
        }
    }

    fun previousPage() {
        val current = _state.value.currentPage
        if (current > 1) {
            loadPage(current - 1)
        }
    }

    fun goToPage(pageNumber: Int) {
        val total = _state.value.totalPages
        if (pageNumber in 1..total) {
            loadPage(pageNumber)
        }
    }

    fun retry() {
        // Evict from cache so loadPage triggers a fresh network request.
        pageCache.remove(_state.value.currentPage)
        _state.update { it.copy(loadedPages = pageCache.toMap()) }
        loadPage(_state.value.currentPage)
    }

    private fun loadPageCount() {
        viewModelScope.launch {
            getMushafPageCountUseCase()
                .onSuccess { count ->
                    _state.update { it.copy(totalPages = count) }
                }
        }
    }
}
