package com.almahmoudApp.al_mahmoudapp.core.di

import com.almahmoudApp.al_mahmoudapp.feature.images.data.repository.ImagesRepositoryImpl
import com.almahmoudApp.al_mahmoudapp.feature.images.domain.repository.ImagesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ImagesModule {

    @Binds
    @Singleton
    abstract fun bindImagesRepository(
        impl: ImagesRepositoryImpl
    ): ImagesRepository
}
