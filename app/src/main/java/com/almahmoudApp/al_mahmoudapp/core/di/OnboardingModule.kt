package com.almahmoudApp.al_mahmoudapp.core.di

import com.almahmoudApp.al_mahmoudapp.feature.onboarding.data.repository.OnboardingRepositoryImpl
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.repository.OnboardingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingModule {
    @Binds
    abstract fun bindOnboardingRepository(
        repository: OnboardingRepositoryImpl,
    ): OnboardingRepository
}
