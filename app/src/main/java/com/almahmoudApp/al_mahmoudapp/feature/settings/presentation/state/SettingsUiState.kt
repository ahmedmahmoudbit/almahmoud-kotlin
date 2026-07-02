package com.almahmoudApp.al_mahmoudapp.feature.settings.presentation.state

import com.almahmoudApp.al_mahmoudapp.core.theme.AppLanguage
import com.almahmoudApp.al_mahmoudapp.core.theme.ThemeMode

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val appLanguage: AppLanguage = AppLanguage.ARABIC,
)
