package com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {
    private val _state = MutableStateFlow(StoriesUiState())
    val state: StateFlow<StoriesUiState> = _state.asStateFlow()

    init {
        loadStories()
    }

    private fun loadStories() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = StoriesUiState(isLoading = true)
            getStoriesUseCase()
                .onSuccess { stories ->
                    _state.value = StoriesUiState(isLoading = false, stories = stories)
                }
                .onFailure { throwable ->
                    _state.value = StoriesUiState(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load stories",
                    )
                }
        }
    }
}
