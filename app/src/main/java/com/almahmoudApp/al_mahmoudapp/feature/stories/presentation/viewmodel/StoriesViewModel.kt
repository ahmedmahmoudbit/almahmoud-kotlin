package com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.core.data.preferences.AppPreferencesDataSource
import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.model.StoryItem
import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.usecase.GetStoriesUseCase
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.state.StoriesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoriesViewModel @Inject constructor(
    private val getStoriesUseCase: GetStoriesUseCase,
    private val preferencesDataSource: AppPreferencesDataSource,
) : ViewModel() {
    private val _state = MutableStateFlow(StoriesUiState())
    val state: StateFlow<StoriesUiState> = _state.asStateFlow()

    private var rawShuffledStories: List<StoryItem> = emptyList()
    private var originalStoriesList: List<StoryItem> = emptyList()

    init {
        loadStories()
        observeFavorites()
    }

    private fun loadStories() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
            getStoriesUseCase()
                .onSuccess { stories ->
                    originalStoriesList = stories
                    if (stories.isNotEmpty()) {
                        // Shuffle stories on entry
                        val shuffled = stories.shuffled()
                        val featured = shuffled.first()
                        rawShuffledStories = shuffled.drop(1)

                        _state.value = _state.value.copy(
                            isLoading = false,
                            featuredStory = featured,
                            allStories = rawShuffledStories,
                            errorMessage = null
                        )
                        applyFilters()
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            stories = emptyList(),
                            allStories = emptyList(),
                            featuredStory = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load stories",
                    )
                }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataSource.favoriteStories.collect { favorites ->
                _state.value = _state.value.copy(favoriteStories = favorites)
                applyFilters()
            }
        }
    }

    fun toggleFavoriteFilter() {
        _state.value = _state.value.copy(showOnlyFavorites = !_state.value.showOnlyFavorites)
        applyFilters()
    }

    fun toggleSearchActive() {
        val nextSearchActive = !_state.value.isSearchActive
        val query = if (nextSearchActive) _state.value.searchQuery else ""
        _state.value = _state.value.copy(
            isSearchActive = nextSearchActive,
            searchQuery = query
        )
        applyFilters()
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        applyFilters()
    }

    fun toggleFavoriteStory(story: StoryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataSource.toggleFavoriteStory(story.title)
        }
    }

    private fun applyFilters() {
        val currentState = _state.value
        var filteredList = rawShuffledStories

        // Apply favorites filter
        if (currentState.showOnlyFavorites) {
            filteredList = filteredList.filter { story ->
                currentState.favoriteStories.contains(story.title)
            }
        }

        // Apply search query filter
        if (currentState.searchQuery.isNotBlank()) {
            filteredList = filteredList.filter { story ->
                story.title.contains(currentState.searchQuery, ignoreCase = true) ||
                        story.body.contains(currentState.searchQuery, ignoreCase = true)
            }
        }

        _state.value = _state.value.copy(stories = filteredList)
    }

    fun getOriginalIndex(story: StoryItem): Int {
        val index = originalStoriesList.indexOf(story)
        return if (index != -1) index else 0
    }
}
