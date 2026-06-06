package com.almahmoudApp.al_mahmoudapp.feature.azkar.presentation.state

import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.AzkarCategory
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.ZikrItem

data class AzkarListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

data class AzkarDetailsUiState(
    val isLoading: Boolean = true,
    val category: AzkarCategory? = null,
    val items: List<ZikrItem> = emptyList(),
    val errorMessage: String? = null,
)
