package com.almahmoudApp.al_mahmoudapp.feature.settings.domain.usecase

import com.almahmoudApp.al_mahmoudapp.core.theme.ThemeMode
import com.almahmoudApp.al_mahmoudapp.feature.settings.domain.repository.SettingsRepository
import javax.inject.Inject

class SetThemeModeUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(themeMode: ThemeMode) {
        repository.setThemeMode(themeMode)
    }
}
