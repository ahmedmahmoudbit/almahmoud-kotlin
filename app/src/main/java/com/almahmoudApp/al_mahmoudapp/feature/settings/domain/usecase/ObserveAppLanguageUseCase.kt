package com.almahmoudApp.al_mahmoudapp.feature.settings.domain.usecase

import com.almahmoudApp.al_mahmoudapp.core.theme.AppLanguage
import com.almahmoudApp.al_mahmoudapp.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAppLanguageUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    operator fun invoke(): Flow<AppLanguage> {
        return repository.observeAppLanguage()
    }
}
