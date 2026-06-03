package com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {
    private val _state = MutableStateFlow(StoryDetailsUiState())
    val state: StateFlow<StoryDetailsUiState> = _state.asStateFlow()

    fun load(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = StoryDetailsUiState(isLoading = true)
            getStoryUseCase(index)
                .onSuccess { story ->
                    _state.value = StoryDetailsUiState(isLoading = false, story = story)
                }
                .onFailure { throwable ->
                    _state.value = StoryDetailsUiState(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load story",
                    )
                }
        }
    }
}
