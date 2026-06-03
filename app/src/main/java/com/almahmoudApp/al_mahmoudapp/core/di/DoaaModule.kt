package com.almahmoudApp.al_mahmoudapp.core.di

import com.almahmoudApp.al_mahmoudapp.feature.doaa.data.repository.DoaaRepositoryImpl
import com.almahmoudApp.al_mahmoudapp.feature.doaa.domain.repository.DoaaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DoaaModule {
    @Binds
    abstract fun bindDoaaRepository(repository: DoaaRepositoryImpl): DoaaRepository
}
