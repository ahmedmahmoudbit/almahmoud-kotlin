package com.almahmoudApp.al_mahmoudapp.feature.tamilat.data.repository

import com.almahmoudApp.al_mahmoudapp.feature.tamilat.data.datasource.TamilatLocalDataSource
import com.almahmoudApp.al_mahmoudapp.feature.tamilat.domain.model.TamilatItem
import com.almahmoudApp.al_mahmoudapp.feature.tamilat.domain.repository.TamilatRepository
import javax.inject.Inject

class TamilatRepositoryImpl @Inject constructor(
    private val localDataSource: TamilatLocalDataSource
) : TamilatRepository {
    override suspend fun getReflections(): Result<List<TamilatItem>> {
        return runCatching {
            localDataSource.loadTamilat().mapIndexed { index, text ->
                TamilatItem(text = text, id = index)
            }
        }
    }
}
