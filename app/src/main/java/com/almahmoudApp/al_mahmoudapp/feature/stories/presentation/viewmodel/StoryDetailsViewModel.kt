package com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.core.data.preferences.AppPreferencesDataSource
import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.usecase.GetStoryUseCase
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.state.StoryDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryDetailsViewModel @Inject constructor(
    private val getStoryUseCase: GetStoryUseCase,
    private val preferencesDataSource: AppPreferencesDataSource,
) : ViewModel() {
    private val _state = MutableStateFlow(StoryDetailsUiState())
    val state: StateFlow<StoryDetailsUiState> = _state.asStateFlow()

    fun load(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true)
            getStoryUseCase(index)
                .onSuccess { story ->
                    // Launch a collector for this story's favorite status
                    viewModelScope.launch(Dispatchers.IO) {
                        preferencesDataSource.favoriteStories.collect { favorites ->
                            if (_state.value.story?.title == story.title || _state.value.story == null) {
                                val isFav = favorites.contains(story.title)
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    story = story,
                                    isFavorite = isFav
                                )
                            }
                        }
                    }
                }
                .onFailure { throwable ->
                    _state.value = StoryDetailsUiState(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load story",
                    )
                }
        }
    }

    fun toggleFavorite() {
        val story = _state.value.story ?: return
        viewModelScope.launch(Dispatchers.IO) {
            preferencesDataSource.toggleFavoriteStory(story.title)
        }
    }

    fun setAutoScrollSpeed(speed: Float) {
        _state.value = _state.value.copy(autoScrollSpeed = speed)
    }
}
