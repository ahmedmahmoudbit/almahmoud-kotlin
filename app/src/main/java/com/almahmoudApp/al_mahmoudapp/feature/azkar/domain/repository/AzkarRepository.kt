package com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.repository

import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.AzkarCategory
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.ZikrItem

interface AzkarRepository {
    suspend fun getAzkar(category: AzkarCategory): List<ZikrItem>
}
