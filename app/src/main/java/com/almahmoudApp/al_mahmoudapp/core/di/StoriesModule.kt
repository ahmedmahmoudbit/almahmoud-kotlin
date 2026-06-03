package com.almahmoudApp.al_mahmoudapp.core.di

import com.almahmoudApp.al_mahmoudapp.feature.stories.data.repository.StoriesRepositoryImpl
import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.repository.StoriesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StoriesModule {
    @Binds
    abstract fun bindStoriesRepository(repository: StoriesRepositoryImpl): StoriesRepository
}
