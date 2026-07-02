package com.almahmoudApp.al_mahmoudapp.feature.settings.domain.usecase

import com.almahmoudApp.al_mahmoudapp.core.theme.AppLanguage
import com.almahmoudApp.al_mahmoudapp.feature.settings.domain.repository.SettingsRepository
import javax.inject.Inject

class SetAppLanguageUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(language: AppLanguage) {
        repository.setAppLanguage(language)
    }
}
