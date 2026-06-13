package com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model

data class PrayerDashboard(
    val location: PrayerLocation,
    val today: PrayerDay,
    val nextPrayerName: String,
    val nextPrayerTime: String,
    val nextPrayerAtMillis: Long,
    val remainingText: String,
)
