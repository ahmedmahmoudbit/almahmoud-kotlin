package com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.state

import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.model.StoryItem

data class StoryDetailsUiState(
    val isLoading: Boolean = true,
    val story: StoryItem? = null,
    val errorMessage: String? = null,
    val isFavorite: Boolean = false,
    val autoScrollSpeed: Float = 0f,
)
