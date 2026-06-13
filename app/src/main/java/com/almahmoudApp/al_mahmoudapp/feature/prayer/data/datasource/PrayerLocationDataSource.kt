package com.almahmoudApp.al_mahmoudapp.feature.prayer.data.datasource

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerLocation
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

class PrayerLocationDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): PrayerLocation? {
        return withContext(Dispatchers.IO) {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val location = suspendCancellableCoroutine<android.location.Location?> { continuation ->
                client.lastLocation
                    .addOnSuccessListener { continuation.resume(it) }
                    .addOnFailureListener { continuation.resume(null) }
            } ?: return@withContext null

            resolveLocation(location.latitude, location.longitude)
        }
    }

    fun resolveLocationFromAddress(city: String, country: String): PrayerLocation {
        return PrayerLocation(
            city = city.trim(),
            country = country.trim(),
        )
    }

    private fun resolveLocation(latitude: Double, longitude: Double): PrayerLocation? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1) ?: emptyList()
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(latitude, longitude, 1) ?: emptyList()
            }
            val address = addresses.firstOrNull() ?: return null
            val city = address.locality ?: address.subAdminArea ?: address.adminArea ?: ""
            val country = address.countryName ?: ""
            if (city.isBlank() || country.isBlank()) return null
            PrayerLocation(city = city, country = country)
        } catch (_: Exception) {
            null
        }
    }
}
