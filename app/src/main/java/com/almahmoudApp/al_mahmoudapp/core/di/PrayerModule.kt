package com.almahmoudApp.al_mahmoudapp.core.di

import com.almahmoudApp.al_mahmoudapp.feature.prayer.data.repository.PrayerRepositoryImpl
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.repository.PrayerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PrayerModule {
    @Binds
    abstract fun bindPrayerRepository(repository: PrayerRepositoryImpl): PrayerRepository
}
