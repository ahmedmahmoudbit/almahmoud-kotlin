package com.almahmoudApp.al_mahmoudapp.feature.settings.di

import com.almahmoudApp.al_mahmoudapp.feature.settings.data.repository.SettingsRepositoryImpl
import com.almahmoudApp.al_mahmoudapp.feature.settings.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {
    @Binds
    abstract fun bindSettingsRepository(
        repository: SettingsRepositoryImpl,
    ): SettingsRepository
}
