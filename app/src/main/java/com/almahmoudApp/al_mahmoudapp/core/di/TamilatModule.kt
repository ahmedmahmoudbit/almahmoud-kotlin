package com.almahmoudApp.al_mahmoudapp.core.di

import com.almahmoudApp.al_mahmoudapp.feature.tamilat.data.repository.TamilatRepositoryImpl
import com.almahmoudApp.al_mahmoudapp.feature.tamilat.domain.repository.TamilatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TamilatModule {

    @Binds
    @Singleton
    abstract fun bindTamilatRepository(
        impl: TamilatRepositoryImpl
    ): TamilatRepository
}
