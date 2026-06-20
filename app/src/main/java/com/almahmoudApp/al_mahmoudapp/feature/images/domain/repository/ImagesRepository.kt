package com.almahmoudApp.al_mahmoudapp.feature.images.domain.repository

import com.almahmoudApp.al_mahmoudapp.feature.images.domain.model.IslamicImage

interface ImagesRepository {
    suspend fun getIslamicImages(): Result<List<IslamicImage>>
    suspend fun getWallpaperBackgrounds(): Result<List<IslamicImage>>
}
