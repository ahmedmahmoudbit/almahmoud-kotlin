package com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.state

import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerDashboard

data class PrayerUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val dashboard: PrayerDashboard? = null,
    val prayerCountdownText: String = "",
    val isLocationSheetVisible: Boolean = false,
    val manualCity: String = "",
    val manualCountry: String = "",
    val errorMessage: String? = null,
)
