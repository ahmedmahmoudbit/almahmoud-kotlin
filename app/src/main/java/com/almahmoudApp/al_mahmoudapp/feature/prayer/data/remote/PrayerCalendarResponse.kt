package com.almahmoudApp.al_mahmoudapp.feature.prayer.data.remote

import com.google.gson.annotations.SerializedName

data class PrayerCalendarResponse(
    @SerializedName("data")
    val data: List<PrayerDayResponse> = emptyList(),
)

data class PrayerDayResponse(
    @SerializedName("date")
    val date: PrayerDateResponse? = null,
    @SerializedName("timings")
    val timings: PrayerTimingsResponse? = null,
)

data class PrayerDateResponse(
    @SerializedName("readable")
    val readable: String? = null,
)

data class PrayerTimingsResponse(
    @SerializedName("Fajr")
    val fajr: String? = null,
    @SerializedName("Sunrise")
    val sunrise: String? = null,
    @SerializedName("Dhuhr")
    val dhuhr: String? = null,
    @SerializedName("Asr")
    val asr: String? = null,
    @SerializedName("Maghrib")
    val maghrib: String? = null,
    @SerializedName("Isha")
    val isha: String? = null,
)
