package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranContent
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranMode
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranReader
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranSurah
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase.GetQuranContentUseCase
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.QuranUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val getQuranContentUseCase: GetQuranContentUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(QuranUiState())
    val state: StateFlow<QuranUiState> = _state.asStateFlow()

    init {
        loadQuranContent()
    }

    fun onQueryChange(query: String) {
        _state.update { current ->
            current.copy(
                query = query,
                filteredSurahs = filterSurahs(current.content, query),
                filteredReaders = filterReaders(current.content, query),
                errorMessage = null,
            )
        }
    }

    fun onModeChange(mode: QuranMode) {
        _state.update { current ->
            current.copy(selectedMode = mode)
        }
    }

    fun retry() {
        loadQuranContent()
    }

    private fun loadQuranContent() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getQuranContentUseCase()
                .onSuccess { content ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            content = content,
                            filteredSurahs = filterSurahs(content, it.query),
                            filteredReaders = filterReaders(content, it.query),
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

    private fun filterSurahs(content: QuranContent?, query: String): List<QuranSurah> {
        val surahs = content?.surahs.orEmpty()
        if (query.isBlank()) {
            return surahs
        }

        val normalizedQuery = query.trim()
        return surahs.filter { surah ->
            surah.nameArabic.contains(normalizedQuery, ignoreCase = true) ||
                surah.nameEnglish.contains(normalizedQuery, ignoreCase = true) ||
                surah.revelationType.contains(normalizedQuery, ignoreCase = true) ||
                surah.versesCount.toString().contains(normalizedQuery) ||
                surah.pageNumber.toString().contains(normalizedQuery)
        }
    }

    private fun filterReaders(content: QuranContent?, query: String): List<QuranReader> {
        val readers = content?.readers.orEmpty()
        if (query.isBlank()) {
            return readers
        }

        val normalizedQuery = query.trim()
        return readers.filter { reader ->
            reader.name.contains(normalizedQuery, ignoreCase = true)
        }
    }
}
