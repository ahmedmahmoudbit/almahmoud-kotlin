package com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AnasheedItem
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.repository.AyatRepository
import javax.inject.Inject

class GetAnasheedItemsUseCase @Inject constructor(
    private val repository: AyatRepository,
) {
    suspend operator fun invoke(): Result<List<AnasheedItem>> = runCatching {
        repository.getAnasheedItems()
    }
}
