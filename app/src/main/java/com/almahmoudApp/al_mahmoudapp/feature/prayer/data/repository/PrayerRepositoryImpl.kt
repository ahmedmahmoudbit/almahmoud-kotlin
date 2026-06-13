package com.almahmoudApp.al_mahmoudapp.feature.prayer.data.repository

import android.util.Log
import com.almahmoudApp.al_mahmoudapp.feature.prayer.data.datasource.PrayerLocationDataSource
import com.almahmoudApp.al_mahmoudapp.feature.prayer.data.datasource.PrayerPreferencesDataSource
import com.almahmoudApp.al_mahmoudapp.feature.prayer.data.datasource.PrayerRemoteDataSource
import com.almahmoudApp.al_mahmoudapp.feature.prayer.data.remote.PrayerDayResponse
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerDashboard
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerDay
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerLocation
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerTimeItem
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.repository.PrayerRepository
import com.google.gson.Gson
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PrayerRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PrayerPreferencesDataSource,
    private val remoteDataSource: PrayerRemoteDataSource,
    private val locationDataSource: PrayerLocationDataSource,
    private val gson: Gson,
) : PrayerRepository {
    override suspend fun getPrayerDashboard(forceRefresh: Boolean): Result<PrayerDashboard> {
        return runCatching {
            withContext(Dispatchers.IO) {
                val location = resolveLocation()
                val monthYear = currentMonthYear()
                val cachedMonthYear = preferencesDataSource.cachedMonthYear.first()
                val cachedJson = preferencesDataSource.cachedCalendarJson.first()
                val cachedCity = preferencesDataSource.cachedCity.first()
                val cachedCountry = preferencesDataSource.cachedCountry.first()
                val cacheMatchesLocation = cachedCity.equals(location.city, ignoreCase = true) &&
                    cachedCountry.equals(location.country, ignoreCase = true)

                val calendarJson = if (forceRefresh || cachedJson.isBlank() || cachedMonthYear != monthYear || !cacheMatchesLocation) {
                    val fetched = remoteDataSource.fetchMonthlyPrayerTimes(
                        city = location.city,
                        country = location.country,
                        method = chooseMethod(location.country),
                        month = currentMonth(),
                        year = currentYear(),
                    )
                    val json = gson.toJson(fetched.data.mapNotNull { it.toDomainDay() })
                    preferencesDataSource.saveCalendar(
                        json = json,
                        monthYear = monthYear,
                        city = location.city,
                        country = location.country,
                    )
                    json
                } else {
                    cachedJson
                }

                val days = parseCalendar(calendarJson)
                buildDashboard(location, days)
            }
        }.onFailure { error ->
            Log.e(TAG, "Failed to load prayer dashboard", error)
        }
    }

    override suspend fun setManualLocation(location: PrayerLocation): Result<Unit> {
        return runCatching {
            preferencesDataSource.saveManualLocation(location.city, location.country)
            Unit
        }
    }

    override suspend fun useCurrentLocation(): Result<Unit> {
        return runCatching {
            val location = locationDataSource.getCurrentLocation()
                ?: throw IllegalStateException("Unable to determine current location")
            preferencesDataSource.saveManualLocation(location.city, location.country)
            preferencesDataSource.saveAutoLocation()
            Unit
        }
    }

    private suspend fun resolveLocation(): PrayerLocation {
        val locationMode = preferencesDataSource.locationMode.first()
        val city = preferencesDataSource.manualCity.first()
        val country = preferencesDataSource.manualCountry.first()

        return when {
            locationMode == LOCATION_MODE_MANUAL && city.isNotBlank() && country.isNotBlank() ->
                PrayerLocation(city = city, country = country)
            else -> locationDataSource.getCurrentLocation() ?: PrayerLocation(
                city = city.ifBlank { DEFAULT_CITY },
                country = country.ifBlank { DEFAULT_COUNTRY },
            )
        }
    }

    private fun buildDashboard(location: PrayerLocation, days: List<PrayerDay>): PrayerDashboard {
        val today = currentReadableDate()
        val todayDay = days.firstOrNull { it.readableDate == today } ?: days.firstOrNull()
            ?: throw IllegalStateException("No prayer data available")
        val next = calculateNextPrayer(days, todayDay)
        return PrayerDashboard(
            location = location,
            today = todayDay,
            nextPrayerName = next.name,
            nextPrayerTime = next.time,
            nextPrayerAtMillis = next.atMillis,
            remainingText = next.remaining,
        )
    }

    private fun calculateNextPrayer(
        days: List<PrayerDay>,
        today: PrayerDay,
    ): NextPrayerSelection {
        val order = today.prayerTimes.filter { it.time.isNotBlank() }
        val now = Calendar.getInstance()
        val candidates = order.mapNotNull { prayer ->
            parsePrayerTime(prayer.time)?.takeIf { it.after(now) }?.let { prayer to it }
        }

        val selected = candidates.minByOrNull { it.second.timeInMillis }
            ?: nextDayFajr(days, today)

        val millis = selected.second.timeInMillis - now.timeInMillis
        val hours = (millis / (1000 * 60 * 60)).coerceAtLeast(0)
        val minutes = ((millis / (1000 * 60)) % 60).coerceAtLeast(0)
        val remaining = buildString {
            append(hours)
            append("h ")
            append(minutes)
            append("m")
        }
        return NextPrayerSelection(
            name = selected.first.name,
            time = selected.first.time,
            atMillis = selected.second.timeInMillis,
            remaining = remaining,
        )
    }

    private fun nextDayFajr(days: List<PrayerDay>, today: PrayerDay): Pair<PrayerTimeItem, Calendar> {
        val nextDay = days.dropWhile { it.readableDate != today.readableDate }.drop(1).firstOrNull()
        val fajr = nextDay?.prayerTimes?.firstOrNull { it.name == PRAYER_FAJR } ?: today.prayerTimes.first()
        val nextTime = parsePrayerTime(fajr.time) ?: Calendar.getInstance()
        nextTime.add(Calendar.DAY_OF_MONTH, 1)
        return fajr to nextTime
    }

    private fun parseCalendar(json: String): List<PrayerDay> {
        return runCatching {
            val type = com.google.gson.reflect.TypeToken.getParameterized(List::class.java, PrayerDay::class.java).type
            gson.fromJson<List<PrayerDay>>(json, type) ?: emptyList()
        }.getOrDefault(emptyList())
    }

    private fun PrayerDayResponse.toDomainDay(): PrayerDay? {
        val readable = date?.readable.orEmpty()
        val timings = timings ?: return null
        if (readable.isBlank()) return null
        return PrayerDay(
            readableDate = readable,
            prayerTimes = listOf(
                PrayerTimeItem(PRAYER_FAJR, timings.fajr.orEmpty()),
                PrayerTimeItem(PRAYER_DHUHR, timings.dhuhr.orEmpty()),
                PrayerTimeItem(PRAYER_ASR, timings.asr.orEmpty()),
                PrayerTimeItem(PRAYER_MAGHRIB, timings.maghrib.orEmpty()),
                PrayerTimeItem(PRAYER_ISHA, timings.isha.orEmpty()),
            ),
        )
    }

    private fun chooseMethod(country: String): Int {
        return when {
            country.contains("Saudi", ignoreCase = true) ||
                country.contains("Qatar", ignoreCase = true) ||
                country.contains("Kuwait", ignoreCase = true) ||
                country.contains("Emirates", ignoreCase = true) ||
                country.contains("Bahrain", ignoreCase = true) ||
                country.contains("Oman", ignoreCase = true) -> 4
            country.contains("Egypt", ignoreCase = true) ||
                country.contains("Sudan", ignoreCase = true) ||
                country.contains("Morocco", ignoreCase = true) ||
                country.contains("Libya", ignoreCase = true) ||
                country.contains("Algeria", ignoreCase = true) ||
                country.contains("Tunisia", ignoreCase = true) -> 5
            else -> 3
        }
    }

    private fun currentMonthYear(): String = "${currentMonth()}-${currentYear()}"
    private fun currentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    private fun currentReadableDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        return sdf.format(Calendar.getInstance().time)
    }

    private fun parsePrayerTime(timeString: String): Calendar? {
        return try {
            val cleanTime = timeString.split(" ").firstOrNull().orEmpty()
            val parts = cleanTime.split(":")
            if (parts.size != 2) return null
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar
        } catch (_: Exception) {
            null
        }
    }

    private companion object {
        const val TAG = "PrayerRepository"
        const val LOCATION_MODE_MANUAL = "manual"
        const val PRAYER_FAJR = "Fajr"
        const val PRAYER_DHUHR = "Dhuhr"
        const val PRAYER_ASR = "Asr"
        const val PRAYER_MAGHRIB = "Maghrib"
        const val PRAYER_ISHA = "Isha"
        const val DEFAULT_CITY = "Cairo"
        const val DEFAULT_COUNTRY = "Egypt"
    }

    private data class NextPrayerSelection(
        val name: String,
        val time: String,
        val atMillis: Long,
        val remaining: String,
    )
}
