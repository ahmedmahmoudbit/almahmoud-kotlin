package com.almahmoudApp.al_mahmoudapp.feature.home.presentation.state

import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeFeature

data class HomeUiState(
    val isLoading: Boolean = true,
    val features: List<HomeFeature> = emptyList(),
    val quotes: List<String> = emptyList(),
    val errorMessage: String? = null,
)
