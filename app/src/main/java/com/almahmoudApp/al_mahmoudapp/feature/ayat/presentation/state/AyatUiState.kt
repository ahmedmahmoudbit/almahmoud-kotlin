package com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.state

import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatTopic

data class AyatUiState(
    val isLoading: Boolean = true,
    val topics: List<AyatTopic> = emptyList(),
    val errorMessage: String? = null,
)
