package com.almahmoudApp.al_mahmoudapp.feature.prayer.data.datasource

import com.almahmoudApp.al_mahmoudapp.feature.prayer.data.remote.PrayerApiService
import com.almahmoudApp.al_mahmoudapp.feature.prayer.data.remote.PrayerCalendarResponse
import javax.inject.Inject

class PrayerRemoteDataSource @Inject constructor(
    private val apiService: PrayerApiService,
) {
    suspend fun fetchMonthlyPrayerTimes(
        city: String,
        country: String,
        method: Int,
        month: Int,
        year: Int,
    ): PrayerCalendarResponse {
        return apiService.getCalendarByCity(
            city = city,
            country = country,
            method = method,
            month = month,
            year = year,
        )
    }
}
