package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state

import androidx.compose.runtime.Immutable
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.api.VerseAudioResponse
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerseDetails
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.AudioReciterItem

@Immutable
data class QuranTextUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val verses: List<QuranVerse> = emptyList(),
    val selectedVerse: QuranVerse? = null,
    val selectedVerseDetails: QuranVerseDetails? = null,
    val isVerseDetailsLoading: Boolean = false,
    val verseDetailsErrorMessage: String? = null,
    val fontSize: Int = 28,
    val showFontSizeSheet: Boolean = false,
    val isAutoScrolling: Boolean = false,
    val scrollSpeed: Float = 1.0f,
    val showScrollSpeedSheet: Boolean = false,
    // Audio related state
    val verseAudioData: VerseAudioResponse? = null,
    val availableReciters: List<AudioReciterItem> = emptyList(),
    val isAudioLoading: Boolean = false,
    val audioError: String? = null,
    val isAudioPlaying: Boolean = false,
    val currentPlayingReciter: String? = null,
    val currentAudioUrl: String? = null,
)
