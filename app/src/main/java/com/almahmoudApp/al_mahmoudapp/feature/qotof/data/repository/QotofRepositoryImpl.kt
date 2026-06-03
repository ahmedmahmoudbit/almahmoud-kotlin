package com.almahmoudApp.al_mahmoudapp.feature.qotof.data.repository

import android.util.Log
import com.almahmoudApp.al_mahmoudapp.feature.qotof.data.datasource.QotofLocalDataSource
import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.model.QotofContent
import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.repository.QotofRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QotofRepositoryImpl @Inject constructor(
    private val localDataSource: QotofLocalDataSource,
) : QotofRepository {
    override suspend fun loadQotofContent(): Result<QotofContent> {
        return runCatching {
            localDataSource.loadQotofContent()
        }.onFailure { error ->
            Log.e(TAG, "Failed to load Qotof content", error)
        }
    }

    private companion object {
        const val TAG = "QotofRepository"
    }
}
