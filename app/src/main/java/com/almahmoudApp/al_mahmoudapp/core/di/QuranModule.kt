package com.almahmoudApp.al_mahmoudapp.core.di

import com.almahmoudApp.al_mahmoudapp.feature.quran.data.repository.QuranRepositoryImpl
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.repository.QuranRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class QuranModule {
    @Binds
    abstract fun bindQuranRepository(repository: QuranRepositoryImpl): QuranRepository
}
