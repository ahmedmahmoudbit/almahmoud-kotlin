package com.almahmoudApp.al_mahmoudapp.feature.onboarding.data.datasource

import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.model.OnboardingPage
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.model.OnboardingPageKey
import javax.inject.Inject

class OnboardingLocalDataSource @Inject constructor() {
    fun loadPages(): List<OnboardingPage> {
        return OnboardingPageKey.entries.mapIndexed { index, key ->
            OnboardingPage(key = key, sortOrder = index)
        }
    }
}
