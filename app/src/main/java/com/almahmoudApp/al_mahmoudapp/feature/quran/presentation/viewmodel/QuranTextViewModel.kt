package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase.GetQuranVersesUseCase
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
) : ViewModel() {
    private val _state = MutableStateFlow(QuranTextUiState())
    val state: StateFlow<QuranTextUiState> = _state.asStateFlow()

    init {
        loadVerses()
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
            )
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
    }
}
