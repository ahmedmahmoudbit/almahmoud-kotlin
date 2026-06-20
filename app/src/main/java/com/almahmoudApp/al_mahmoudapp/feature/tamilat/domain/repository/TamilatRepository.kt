package com.almahmoudApp.al_mahmoudapp.feature.tamilat.domain.repository

import com.almahmoudApp.al_mahmoudapp.feature.tamilat.domain.model.TamilatItem

interface TamilatRepository {
    suspend fun getReflections(): Result<List<TamilatItem>>
}
