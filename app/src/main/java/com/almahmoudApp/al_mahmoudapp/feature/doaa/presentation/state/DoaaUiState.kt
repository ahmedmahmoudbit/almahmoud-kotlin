package com.almahmoudApp.al_mahmoudapp.feature.doaa.presentation.state

data class DoaaUiState(
    val isLoading: Boolean = true,
    val items: List<String> = emptyList(),
    val errorMessage: String? = null,
)
