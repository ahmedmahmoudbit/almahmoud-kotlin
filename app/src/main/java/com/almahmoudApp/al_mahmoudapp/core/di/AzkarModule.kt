package com.almahmoudApp.al_mahmoudapp.core.di

import com.almahmoudApp.al_mahmoudapp.feature.azkar.data.repository.AzkarRepositoryImpl
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.repository.AzkarRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AzkarModule {
    @Binds
    abstract fun bindAzkarRepository(repository: AzkarRepositoryImpl): AzkarRepository
}
