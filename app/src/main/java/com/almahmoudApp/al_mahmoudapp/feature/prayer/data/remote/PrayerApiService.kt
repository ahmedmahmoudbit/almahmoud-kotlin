package com.almahmoudApp.al_mahmoudapp.feature.prayer.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface PrayerApiService {
    @GET("v1/calendarByCity")
    suspend fun getCalendarByCity(
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int,
        @Query("month") month: Int,
        @Query("year") year: Int,
    ): PrayerCalendarResponse
}
