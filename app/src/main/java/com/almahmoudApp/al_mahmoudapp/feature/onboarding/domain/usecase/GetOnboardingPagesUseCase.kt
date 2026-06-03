package com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.model.OnboardingPage
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject

class GetOnboardingPagesUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(): Result<List<OnboardingPage>> {
        return repository.loadPages()
    }
}
