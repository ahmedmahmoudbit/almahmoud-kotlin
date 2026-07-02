package com.almahmoudApp.al_mahmoudapp.feature.settings.domain.usecase

import com.almahmoudApp.al_mahmoudapp.core.theme.ThemeMode
import com.almahmoudApp.al_mahmoudapp.feature.settings.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveThemeModeUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<ThemeMode> {
        return repository.observeThemeMode()
    }
}
