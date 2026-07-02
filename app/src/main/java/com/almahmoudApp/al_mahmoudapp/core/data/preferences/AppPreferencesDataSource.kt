package com.almahmoudApp.al_mahmoudapp.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.edit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class AppPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val themeMode: Flow<String> = dataStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { preferences ->
            preferences[Keys.THEME_MODE] ?: "SYSTEM"
        }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = mode
        }
    }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { preferences ->
            preferences[Keys.IS_ONBOARDING_COMPLETED] ?: false
        }

    suspend fun setOnboardingCompleted(isCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_ONBOARDING_COMPLETED] = isCompleted
        }
    }

    val favoriteAnasheed: Flow<Set<String>> = dataStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { preferences ->
            preferences[Keys.FAVORITE_ANASHEED] ?: emptySet()
        }

    suspend fun toggleFavoriteAnasheed(title: String) {
        dataStore.edit { preferences ->
            val current = preferences[Keys.FAVORITE_ANASHEED] ?: emptySet()
            if (current.contains(title)) {
                preferences[Keys.FAVORITE_ANASHEED] = current - title
            } else {
                preferences[Keys.FAVORITE_ANASHEED] = current + title
            }
        }
    }

    val favoriteStories: Flow<Set<String>> = dataStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { preferences ->
            preferences[Keys.FAVORITE_STORIES] ?: emptySet()
        }

    suspend fun toggleFavoriteStory(title: String) {
        dataStore.edit { preferences ->
            val current = preferences[Keys.FAVORITE_STORIES] ?: emptySet()
            if (current.contains(title)) {
                preferences[Keys.FAVORITE_STORIES] = current - title
            } else {
                preferences[Keys.FAVORITE_STORIES] = current + title
            }
        }
    }

    val favoriteSurahs: Flow<Set<String>> = dataStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { preferences ->
            preferences[Keys.FAVORITE_SURAHS] ?: emptySet()
        }

    suspend fun toggleFavoriteSurah(surahNumber: Int) {
        dataStore.edit { preferences ->
            val surahStr = surahNumber.toString()
            val current = preferences[Keys.FAVORITE_SURAHS] ?: emptySet()
            if (current.contains(surahStr)) {
                preferences[Keys.FAVORITE_SURAHS] = current - surahStr
            } else {
                preferences[Keys.FAVORITE_SURAHS] = current + surahStr
            }
        }
    }

    val quranFontSize: Flow<Int> = dataStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { preferences ->
            preferences[Keys.QURAN_FONT_SIZE] ?: DEFAULT_FONT_SIZE
        }

    suspend fun setQuranFontSize(size: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.QURAN_FONT_SIZE] = size
        }
    }

    val lastReadPage: Flow<Int> = dataStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { preferences ->
            preferences[Keys.LAST_READ_PAGE] ?: 1
        }

    suspend fun setLastReadPage(page: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.LAST_READ_PAGE] = page
        }
    }

    val lastReadSurah: Flow<Int> = dataStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { preferences ->
            preferences[Keys.LAST_READ_SURAH] ?: 1
        }

    suspend fun setLastReadSurah(surah: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.LAST_READ_SURAH] = surah
        }
    }

    val lastReadSurahName: Flow<String> = dataStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { preferences ->
            preferences[Keys.LAST_READ_SURAH_NAME] ?: ""
        }

    suspend fun setLastReadSurahName(name: String) {
        dataStore.edit { preferences ->
            preferences[Keys.LAST_READ_SURAH_NAME] = name
        }
    }

    val appLanguage: Flow<String> = dataStore.data
        .catch { emit(androidx.datastore.preferences.core.emptyPreferences()) }
        .map { preferences ->
            preferences[Keys.APP_LANGUAGE] ?: "ARABIC"
        }

    suspend fun setAppLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[Keys.APP_LANGUAGE] = language
        }
    }

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val FAVORITE_ANASHEED = stringSetPreferencesKey("favorite_anasheed")
        val FAVORITE_STORIES = stringSetPreferencesKey("favorite_stories")
        val FAVORITE_SURAHS = stringSetPreferencesKey("favorite_surahs")
        val QURAN_FONT_SIZE = intPreferencesKey("quran_font_size")
        val LAST_READ_PAGE = intPreferencesKey("last_read_page")
        val LAST_READ_SURAH = intPreferencesKey("last_read_surah")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val LAST_READ_SURAH_NAME = stringPreferencesKey("last_read_surah_name")
    }

    companion object {
        const val DEFAULT_FONT_SIZE = 28
    }
}
