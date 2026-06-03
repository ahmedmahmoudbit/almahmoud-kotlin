package com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.model

enum class OnboardingPageKey {
    AD_FREE,
    DAILY_QURAN,
    SIMPLE_ACCESS,
}

data class OnboardingPage(
    val key: OnboardingPageKey,
    val sortOrder: Int,
)
