package com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.repository

import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.model.OnboardingPage
import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    fun observeOnboardingCompleted(): Flow<Boolean>

    suspend fun loadPages(): Result<List<OnboardingPage>>

    suspend fun completeOnboarding(): Result<Unit>
}
