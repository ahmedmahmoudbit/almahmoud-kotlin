package com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.state

import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.model.StoryItem

data class StoriesUiState(
    val isLoading: Boolean = true,
    val stories: List<StoryItem> = emptyList(),
    val allStories: List<StoryItem> = emptyList(),
    val featuredStory: StoryItem? = null,
    val favoriteStories: Set<String> = emptySet(),
    val searchQuery: String = "",
    val showOnlyFavorites: Boolean = false,
    val isSearchActive: Boolean = false,
    val errorMessage: String? = null,
)
