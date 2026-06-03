package com.almahmoudApp.al_mahmoudapp.feature.home.data.repository

import android.util.Log
import com.almahmoudApp.al_mahmoudapp.feature.home.data.datasource.HomeLocalDataSource
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeContent
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val localDataSource: HomeLocalDataSource,
) : HomeRepository {
    override suspend fun loadHomeContent(): Result<HomeContent> {
        return runCatching {
            HomeContent(
                features = localDataSource.loadHomeFeatures().sortedBy { it.sortOrder },
                quotes = localDataSource.loadQuotes(),
            )
        }.onFailure { error ->
            Log.e(TAG, "Failed to load home content", error)
        }
    }

    private companion object {
        const val TAG = "HomeRepository"
    }
}
