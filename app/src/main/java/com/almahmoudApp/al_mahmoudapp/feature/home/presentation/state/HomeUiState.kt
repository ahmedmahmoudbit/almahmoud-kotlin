package com.almahmoudApp.al_mahmoudapp.feature.home.presentation.state

import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeFeature
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerDashboard

data class HomeUiState(
    val isLoading: Boolean = true,
    val features: List<HomeFeature> = emptyList(),
    val quotes: List<String> = emptyList(),
    val prayerDashboard: PrayerDashboard? = null,
    val prayerCountdownText: String = "",
    val isPrayerLoading: Boolean = true,
    val errorMessage: String? = null,
    val prayerErrorMessage: String? = null,
)
