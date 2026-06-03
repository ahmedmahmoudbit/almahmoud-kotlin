package com.almahmoudApp.al_mahmoudapp.feature.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.usecase.CompleteOnboardingUseCase
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.usecase.GetOnboardingPagesUseCase
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.presentation.state.OnboardingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getOnboardingPagesUseCase: GetOnboardingPagesUseCase,
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    init {
        loadPages()
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            completeOnboardingUseCase()
                .onSuccess {
                    _state.update { state ->
                        state.copy(isCompleted = true, errorMessage = null)
                    }
                }
                .onFailure { error ->
                    _state.update { state ->
                        state.copy(errorMessage = error.localizedMessage)
                    }
                }
        }
    }

    private fun loadPages() {
        viewModelScope.launch {
            getOnboardingPagesUseCase()
                .onSuccess { pages ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            pages = pages,
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
