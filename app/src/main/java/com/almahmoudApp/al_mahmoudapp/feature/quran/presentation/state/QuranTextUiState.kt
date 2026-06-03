package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state

import androidx.compose.runtime.Immutable
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerseDetails

@Immutable
data class QuranTextUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val verses: List<QuranVerse> = emptyList(),
    val selectedVerse: QuranVerse? = null,
    val selectedVerseDetails: QuranVerseDetails? = null,
    val isVerseDetailsLoading: Boolean = false,
    val verseDetailsErrorMessage: String? = null,
)
