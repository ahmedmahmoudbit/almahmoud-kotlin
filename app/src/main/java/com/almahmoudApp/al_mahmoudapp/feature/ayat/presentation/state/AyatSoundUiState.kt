package com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.state

import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatAudioItem
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatTopic

data class AyatSoundUiState(
    val isLoading: Boolean = true,
    val topic: AyatTopic? = null,
    val items: List<AyatAudioItem> = emptyList(),
    val cachedUrls: Set<String> = emptySet(),
    val errorMessage: String? = null,
)
