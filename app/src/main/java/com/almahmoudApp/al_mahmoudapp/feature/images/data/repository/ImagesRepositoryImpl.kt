package com.almahmoudApp.al_mahmoudapp.feature.images.data.repository

import com.almahmoudApp.al_mahmoudapp.feature.images.data.datasource.ImagesLocalDataSource
import com.almahmoudApp.al_mahmoudapp.feature.images.domain.model.IslamicImage
import com.almahmoudApp.al_mahmoudapp.feature.images.domain.repository.ImagesRepository
import javax.inject.Inject

class ImagesRepositoryImpl @Inject constructor(
    private val localDataSource: ImagesLocalDataSource
) : ImagesRepository {

    override suspend fun getWallpaperBackgrounds(): Result<List<IslamicImage>> {
        return runCatching {
            localDataSource.getWallpaperUrls().map { url ->
                IslamicImage(url = url, isWallpaper = true)
            }
        }
    }
}
