package com.almahmoudApp.al_mahmoudapp.feature.prayer.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PrayerPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val locationMode: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.LOCATION_MODE] ?: LocationMode.AUTO }

    val manualCity: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.MANUAL_CITY] ?: "" }

    val manualCountry: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.MANUAL_COUNTRY] ?: "" }

    val cachedCalendarJson: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.CACHED_CALENDAR_JSON] ?: "" }

    val cachedMonthYear: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.CACHED_MONTH_YEAR] ?: "" }

    val cachedCity: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.CACHED_CITY] ?: "" }

    val cachedCountry: Flow<String> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[Keys.CACHED_COUNTRY] ?: "" }

    suspend fun saveManualLocation(city: String, country: String) {
        dataStore.edit { prefs ->
            prefs[Keys.LOCATION_MODE] = LocationMode.MANUAL
            prefs[Keys.MANUAL_CITY] = city
            prefs[Keys.MANUAL_COUNTRY] = country
        }
    }

    suspend fun saveAutoLocation() {
        dataStore.edit { prefs ->
            prefs[Keys.LOCATION_MODE] = LocationMode.AUTO
        }
    }

    suspend fun saveCalendar(json: String, monthYear: String, city: String, country: String) {
        dataStore.edit { prefs ->
            prefs[Keys.CACHED_CALENDAR_JSON] = json
            prefs[Keys.CACHED_MONTH_YEAR] = monthYear
            prefs[Keys.CACHED_CITY] = city
            prefs[Keys.CACHED_COUNTRY] = country
        }
    }

    private object Keys {
        val LOCATION_MODE = stringPreferencesKey("prayer_location_mode")
        val MANUAL_CITY = stringPreferencesKey("prayer_manual_city")
        val MANUAL_COUNTRY = stringPreferencesKey("prayer_manual_country")
        val CACHED_CALENDAR_JSON = stringPreferencesKey("prayer_cached_calendar_json")
        val CACHED_MONTH_YEAR = stringPreferencesKey("prayer_cached_month_year")
        val CACHED_CITY = stringPreferencesKey("prayer_cached_city")
        val CACHED_COUNTRY = stringPreferencesKey("prayer_cached_country")
    }

    private object LocationMode {
        const val AUTO = "auto"
        const val MANUAL = "manual"
    }
}
