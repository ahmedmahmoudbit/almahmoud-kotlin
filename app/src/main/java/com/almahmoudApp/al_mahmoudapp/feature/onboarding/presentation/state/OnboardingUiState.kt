package com.almahmoudApp.al_mahmoudapp.feature.onboarding.presentation.state

import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.model.OnboardingPage

data class OnboardingUiState(
    val isLoading: Boolean = true,
    val pages: List<OnboardingPage> = emptyList(),
    val errorMessage: String? = null,
    val isCompleted: Boolean = false,
)
