package com.almahmoudApp.al_mahmoudapp.feature.images.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.images.domain.usecase.GetWallpaperBackgroundsUseCase
import com.almahmoudApp.al_mahmoudapp.feature.images.presentation.state.ImagesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val getWallpaperBackgroundsUseCase: GetWallpaperBackgroundsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ImagesUiState())
    val state: StateFlow<ImagesUiState> = _state.asStateFlow()

    init {
        loadWallpapers()
    }

    fun loadWallpapers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = getWallpaperBackgroundsUseCase()

            result.onSuccess { wallpapers ->
                _state.update {
                    it.copy(
                        wallpaperImages = wallpapers.shuffled(Random),
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        errorMessage = error.localizedMessage ?: "Failed to load wallpapers",
                        isLoading = false
                    )
                }
            }
        }
    }
}
