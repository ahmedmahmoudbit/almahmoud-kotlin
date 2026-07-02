package com.almahmoudApp.al_mahmoudapp.feature.settings.domain.repository

import com.almahmoudApp.al_mahmoudapp.core.theme.AppLanguage
import com.almahmoudApp.al_mahmoudapp.core.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeThemeMode(): Flow<ThemeMode>
    suspend fun setThemeMode(themeMode: ThemeMode)
    fun observeAppLanguage(): Flow<AppLanguage>
    suspend fun setAppLanguage(language: AppLanguage)
}
