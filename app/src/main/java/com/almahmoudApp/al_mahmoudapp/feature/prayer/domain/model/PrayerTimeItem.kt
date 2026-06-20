package com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model

data class PrayerTimeItem(
    val name: String,
    val time: String,
    val iqamahTime: String = "",
)
