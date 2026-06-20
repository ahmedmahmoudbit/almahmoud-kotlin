package com.almahmoudApp.al_mahmoudapp.feature.tamilat.presentation.state

import com.almahmoudApp.al_mahmoudapp.feature.tamilat.domain.model.TamilatItem

data class TamilatUiState(
    val reflections: List<TamilatItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
