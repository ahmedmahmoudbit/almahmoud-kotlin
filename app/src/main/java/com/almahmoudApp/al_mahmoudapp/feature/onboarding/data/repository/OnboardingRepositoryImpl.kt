package com.almahmoudApp.al_mahmoudapp.feature.onboarding.data.repository

import android.util.Log
import com.almahmoudApp.al_mahmoudapp.core.data.preferences.AppPreferencesDataSource
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.data.datasource.OnboardingLocalDataSource
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.model.OnboardingPage
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class OnboardingRepositoryImpl @Inject constructor(
    private val localDataSource: OnboardingLocalDataSource,
    private val preferencesDataSource: AppPreferencesDataSource,
) : OnboardingRepository {
    override fun observeOnboardingCompleted(): Flow<Boolean> {
        return preferencesDataSource.isOnboardingCompleted
    }

    override suspend fun loadPages(): Result<List<OnboardingPage>> {
        return runCatching {
            localDataSource.loadPages().sortedBy(OnboardingPage::sortOrder)
        }.onFailure { error ->
            Log.e(TAG, "Failed to load onboarding pages", error)
        }
    }

    override suspend fun completeOnboarding(): Result<Unit> {
        return runCatching {
            preferencesDataSource.setOnboardingCompleted(true)
        }.onFailure { error ->
            Log.e(TAG, "Failed to complete onboarding", error)
        }
    }

    private companion object {
        const val TAG = "OnboardingRepository"
    }
}
