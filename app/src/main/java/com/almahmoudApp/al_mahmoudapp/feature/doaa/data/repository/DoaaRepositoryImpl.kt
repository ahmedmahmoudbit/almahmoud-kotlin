package com.almahmoudApp.al_mahmoudapp.feature.doaa.data.repository

import com.almahmoudApp.al_mahmoudapp.feature.doaa.data.datasource.DoaaLocalDataSource
import com.almahmoudApp.al_mahmoudapp.feature.doaa.domain.repository.DoaaRepository
import javax.inject.Inject

class DoaaRepositoryImpl @Inject constructor(
    private val localDataSource: DoaaLocalDataSource,
) : DoaaRepository {
    override suspend fun getDoaa(): List<String> = localDataSource.getDoaa()
}
