package com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject

class CompleteOnboardingUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.completeOnboarding()
    }
}
