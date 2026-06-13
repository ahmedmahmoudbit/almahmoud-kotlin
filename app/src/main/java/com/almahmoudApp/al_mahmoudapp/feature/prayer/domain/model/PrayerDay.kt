package com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model

data class PrayerDay(
    val readableDate: String,
    val prayerTimes: List<PrayerTimeItem>,
)
