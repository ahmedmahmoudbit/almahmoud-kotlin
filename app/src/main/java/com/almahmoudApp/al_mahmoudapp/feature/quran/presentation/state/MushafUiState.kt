package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state

import androidx.compose.runtime.Immutable
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.MushafPage
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerseDetails
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.AudioReciterItem

@Immutable
data class MushafUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 604,
    val page: MushafPage? = null,
    val showPageNavigator: Boolean = false,
    val selectedVerseDetails: QuranVerseDetails? = null,
    val isVerseDetailsLoading: Boolean = false,
    val verseDetailsErrorMessage: String? = null,
    val selectedVerseContent: String? = null,
    val isVerseContentLoading: Boolean = false,
    val availableReciters: List<AudioReciterItem> = emptyList(),
    val isAudioLoading: Boolean = false,
    val isAudioPlaying: Boolean = false,
    val currentPlayingReciter: String? = null,
    val currentAudioUrl: String? = null,
    val audioError: String? = null,
)
