package com.almahmoudApp.al_mahmoudapp.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.usecase.GetHomeFeaturesUseCase
import com.almahmoudApp.al_mahmoudapp.feature.home.presentation.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeFeaturesUseCase: GetHomeFeaturesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadHomeFeatures()
    }

    private fun loadHomeFeatures() {
        viewModelScope.launch {
            getHomeFeaturesUseCase()
                .onSuccess { content ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            features = content.features,
                            quotes = content.quotes,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage,
                        )
                    }
                }
        }
    }
}
