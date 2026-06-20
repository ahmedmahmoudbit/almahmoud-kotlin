package com.almahmoudApp.al_mahmoudapp.feature.images.presentation.state

import com.almahmoudApp.al_mahmoudapp.feature.images.domain.model.IslamicImage

data class ImagesUiState(
    val islamicImages: List<IslamicImage> = emptyList(),
    val wallpaperImages: List<IslamicImage> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
