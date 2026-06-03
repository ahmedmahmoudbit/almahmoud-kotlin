package com.almahmoudApp.al_mahmoudapp.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.usecase.ObserveOnboardingCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor(
    observeOnboardingCompletedUseCase: ObserveOnboardingCompletedUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(AppStartupState())
    val state: StateFlow<AppStartupState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeOnboardingCompletedUseCase().collect { isCompleted ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isOnboardingCompleted = isCompleted,
                    )
                }
            }
        }
    }
}
