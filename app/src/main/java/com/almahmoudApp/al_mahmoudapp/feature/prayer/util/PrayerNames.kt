package com.almahmoudApp.al_mahmoudapp.feature.prayer.util

/**
 * Maps the English prayer names returned by the Aladhan API to their Arabic equivalents.
 * Shared across screens to avoid duplicated mapping logic.
 *
 * On Friday, the Dhuhr prayer is referred to as the Friday congregational prayer (الجمعة).
 */
object PrayerNames {

    fun arabicName(englishName: String, isFriday: Boolean = false): String = when (englishName.lowercase().trim()) {
        "fajr" -> "الفجر"
        "sunrise", "shorouq", "shoorooq" -> "الشروق"
        "dhuhr" -> if (isFriday) "الجمعة" else "الظهر"
        "asr" -> "العصر"
        "maghrib" -> "المغرب"
        "isha" -> "العشاء"
        else -> englishName
    }
}
