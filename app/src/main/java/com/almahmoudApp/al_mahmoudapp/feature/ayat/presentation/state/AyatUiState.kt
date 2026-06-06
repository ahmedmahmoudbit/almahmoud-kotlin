package com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.state

import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatTopic
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatAudioItem
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AnasheedItem

data class AyatUiState(
    val isLoading: Boolean = true,
    val topics: List<AyatTopic> = emptyList(),
    val anasheed: List<AnasheedItem> = emptyList(),
    val benefits: List<AyatAudioItem> = emptyList(),
    val selectedTab: Int = 0, // default is Anasheed (0)
    val favoriteAnasheed: Set<String> = emptySet(),
    val filterType: Int = 0, // 0 = All, 1 = Favorites
    val errorMessage: String? = null,
)
