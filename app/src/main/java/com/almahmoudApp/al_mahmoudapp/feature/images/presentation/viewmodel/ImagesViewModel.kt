package com.almahmoudApp.al_mahmoudapp.feature.images.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.images.domain.usecase.GetIslamicImagesUseCase
import com.almahmoudApp.al_mahmoudapp.feature.images.domain.usecase.GetWallpaperBackgroundsUseCase
import com.almahmoudApp.al_mahmoudapp.feature.images.presentation.state.ImagesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val getIslamicImagesUseCase: GetIslamicImagesUseCase,
    private val getWallpaperBackgroundsUseCase: GetWallpaperBackgroundsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ImagesUiState())
    val state: StateFlow<ImagesUiState> = _state.asStateFlow()

    init {
        loadImages()
    }

    fun loadImages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            val imagesResult = getIslamicImagesUseCase()
            val wallpapersResult = getWallpaperBackgroundsUseCase()

            if (imagesResult.isSuccess && wallpapersResult.isSuccess) {
                _state.update { 
                    it.copy(
                        islamicImages = imagesResult.getOrDefault(emptyList()),
                        wallpaperImages = wallpapersResult.getOrDefault(emptyList()),
                        isLoading = false
                    )
                }
            } else {
                val error = imagesResult.exceptionOrNull() ?: wallpapersResult.exceptionOrNull()
                _state.update { 
                    it.copy(
                        errorMessage = error?.localizedMessage ?: "Failed to load images",
                        isLoading = false
                    )
                }
            }
        }
    }
}
