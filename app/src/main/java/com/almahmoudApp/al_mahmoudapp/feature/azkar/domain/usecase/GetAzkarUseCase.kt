package com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.AzkarCategory
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.ZikrItem
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.repository.AzkarRepository
import javax.inject.Inject

class GetAzkarUseCase @Inject constructor(
    private val repository: AzkarRepository,
) {
    suspend operator fun invoke(category: AzkarCategory): Result<List<ZikrItem>> = runCatching {
        repository.getAzkar(category)
    }
}
