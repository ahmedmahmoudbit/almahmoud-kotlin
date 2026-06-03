package com.almahmoudApp.al_mahmoudapp.core.di

import com.almahmoudApp.al_mahmoudapp.feature.qotof.data.repository.QotofRepositoryImpl
import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.repository.QotofRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class QotofModule {
    @Binds
    abstract fun bindQotofRepository(repository: QotofRepositoryImpl): QotofRepository
}
