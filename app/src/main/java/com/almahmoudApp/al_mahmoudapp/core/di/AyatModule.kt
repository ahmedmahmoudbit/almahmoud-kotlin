package com.almahmoudApp.al_mahmoudapp.core.di

import com.almahmoudApp.al_mahmoudapp.feature.ayat.data.repository.AyatRepositoryImpl
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.repository.AyatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AyatModule {
    @Binds
    abstract fun bindAyatRepository(repository: AyatRepositoryImpl): AyatRepository
}
