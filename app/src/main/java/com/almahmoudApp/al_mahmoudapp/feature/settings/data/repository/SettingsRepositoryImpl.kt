package com.almahmoudApp.al_mahmoudapp.feature.settings.data.repository

import com.almahmoudApp.al_mahmoudapp.core.data.preferences.AppPreferencesDataSource
import com.almahmoudApp.al_mahmoudapp.core.theme.AppLanguage
import com.almahmoudApp.al_mahmoudapp.core.theme.ThemeMode
import com.almahmoudApp.al_mahmoudapp.feature.settings.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl @Inject constructor(
    private val preferencesDataSource: AppPreferencesDataSource,
) : SettingsRepository {

    override fun observeThemeMode(): Flow<ThemeMode> {
        return preferencesDataSource.themeMode.map { mode ->
            try {
                ThemeMode.valueOf(mode)
            } catch (_: IllegalArgumentException) {
                ThemeMode.SYSTEM
            }
        }
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        preferencesDataSource.setThemeMode(themeMode.name)
    }

    override fun observeAppLanguage(): Flow<AppLanguage> {
        return preferencesDataSource.appLanguage.map { lang ->
            try {
                AppLanguage.valueOf(lang)
            } catch (_: IllegalArgumentException) {
                AppLanguage.ARABIC
            }
        }
    }

    override suspend fun setAppLanguage(language: AppLanguage) {
        preferencesDataSource.setAppLanguage(language.name)
    }
}
