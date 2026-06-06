package com.almahmoudApp.al_mahmoudapp.feature.azkar.data.repository

import com.almahmoudApp.al_mahmoudapp.feature.azkar.data.datasource.AzkarLocalDataSource
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.AzkarCategory
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.ZikrItem
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.repository.AzkarRepository
import javax.inject.Inject

class AzkarRepositoryImpl @Inject constructor(
    private val localDataSource: AzkarLocalDataSource,
) : AzkarRepository {
    override suspend fun getAzkar(category: AzkarCategory): List<ZikrItem> =
        localDataSource.getAzkar(category)
}
