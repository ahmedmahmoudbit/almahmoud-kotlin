package com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.repository

import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerDashboard
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerLocation

interface PrayerRepository {
    suspend fun getPrayerDashboard(forceRefresh: Boolean = false): Result<PrayerDashboard>
    suspend fun setManualLocation(location: PrayerLocation): Result<Unit>
    suspend fun useCurrentLocation(): Result<Unit>
}
