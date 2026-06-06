package com.almahmoudApp.al_mahmoudapp.core.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.edit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class AppPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
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

    private object Keys {
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
        val FAVORITE_ANASHEED = stringSetPreferencesKey("favorite_anasheed")
        val FAVORITE_STORIES = stringSetPreferencesKey("favorite_stories")
    }
}
