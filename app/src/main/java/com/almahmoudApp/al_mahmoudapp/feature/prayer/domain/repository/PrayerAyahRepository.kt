package com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.repository

interface PrayerAyahRepository {
    /** Returns a random cached verse. Empty if none available. */
    fun randomAyah(): String

    /** Returns all cached verses. */
    fun allAyat(): List<String>
}
