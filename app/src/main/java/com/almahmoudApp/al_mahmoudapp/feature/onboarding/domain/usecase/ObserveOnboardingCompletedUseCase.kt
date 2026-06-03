package com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveOnboardingCompletedUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.observeOnboardingCompleted()
    }
}
