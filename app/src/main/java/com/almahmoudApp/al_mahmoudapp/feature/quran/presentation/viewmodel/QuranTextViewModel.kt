package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.core.data.preferences.AppPreferencesDataSource
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.api.AudioReciter
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase.GetQuranVersesUseCase
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase.GetVerseAudioUseCase
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase.GetVerseDetailsUseCase
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.QuranTextUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class QuranTextViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getQuranVersesUseCase: GetQuranVersesUseCase,
    private val getVerseDetailsUseCase: GetVerseDetailsUseCase,
    private val getVerseAudioUseCase: GetVerseAudioUseCase,
    private val appPreferencesDataSource: AppPreferencesDataSource,
) : ViewModel() {
    private val _state = MutableStateFlow(QuranTextUiState())
    val state: StateFlow<QuranTextUiState> = _state.asStateFlow()

    init {
        loadVerses()
        loadFontSize()
    }

    fun retry() {
        loadVerses()
    }

    fun onVerseSelected(verse: QuranVerse) {
        val surahNumber = verse.surahNumber
        val verseNumber = verse.verseNumber

        _state.update {
            it.copy(
                selectedVerse = verse,
                selectedVerseDetails = null,
                verseDetailsErrorMessage = null,
                isVerseDetailsLoading = true,
            )
        }

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

    fun dismissVerseDetails() {
        _state.update {
            it.copy(
                selectedVerse = null,
                selectedVerseDetails = null,
                isVerseDetailsLoading = false,
                verseDetailsErrorMessage = null,
                verseAudioData = null,
                availableReciters = emptyList(),
                isAudioLoading = false,
                isAudioPlaying = false,
                currentPlayingReciter = null,
            )
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
                            verseAudioData = response,
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
            )
        }
    }

    fun onAudioCompleted() {
        _state.update {
            it.copy(
                isAudioPlaying = false,
                currentPlayingReciter = null,
                currentAudioUrl = null,
            )
        }
    }

    fun showFontSizeSheet() {
        _state.update { it.copy(showFontSizeSheet = true) }
    }

    fun hideFontSizeSheet() {
        _state.update { it.copy(showFontSizeSheet = false) }
    }

    fun showScrollSpeedSheet() {
        _state.update { it.copy(showScrollSpeedSheet = true) }
    }

    fun hideScrollSpeedSheet() {
        _state.update { it.copy(showScrollSpeedSheet = false) }
    }

    fun startAutoScroll() {
        _state.update { it.copy(isAutoScrolling = true) }
    }

    fun stopAutoScroll() {
        _state.update { it.copy(isAutoScrolling = false) }
    }

    fun setScrollSpeed(speed: Float) {
        _state.update { it.copy(scrollSpeed = speed) }
    }

    fun increaseFontSize() {
        val current = _state.value.fontSize
        val next = (current + FONT_STEP).coerceAtMost(MAX_FONT_SIZE)
        updateFontSize(next)
    }

    fun decreaseFontSize() {
        val current = _state.value.fontSize
        val next = (current - FONT_STEP).coerceAtLeast(MIN_FONT_SIZE)
        updateFontSize(next)
    }

    private fun updateFontSize(size: Int) {
        _state.update { it.copy(fontSize = size) }
        viewModelScope.launch {
            appPreferencesDataSource.setQuranFontSize(size)
        }
    }

    private fun loadFontSize() {
        viewModelScope.launch {
            appPreferencesDataSource.quranFontSize.collect { size ->
                _state.update { it.copy(fontSize = size) }
            }
        }
    }

    private fun loadVerses() {
        val surahNumber = savedStateHandle.get<Int>(SURAH_NUMBER_KEY) ?: 1

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getQuranVersesUseCase(surahNumber)
                .onSuccess { verses ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            verses = verses,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage,
                        )
                    }
                }
        }
    }

    private companion object {
        const val SURAH_NUMBER_KEY = "surahNumber"
        const val MIN_FONT_SIZE = 22
        const val MAX_FONT_SIZE = 42
        const val FONT_STEP = 2
    }
}

data class AudioReciterItem(
    val id: String,
    val name: String,
    val url: String,
)
