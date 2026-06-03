package com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.state

import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.model.StoryItem

data class StoriesUiState(
    val isLoading: Boolean = true,
    val stories: List<StoryItem> = emptyList(),
    val errorMessage: String? = null,
)
