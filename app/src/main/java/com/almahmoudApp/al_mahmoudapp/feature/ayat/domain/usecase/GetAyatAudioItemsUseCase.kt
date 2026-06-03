package com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatAudioItem
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.repository.AyatRepository
import javax.inject.Inject

class GetAyatAudioItemsUseCase @Inject constructor(
    private val repository: AyatRepository,
) {
    suspend operator fun invoke(topicId: Int): Result<List<AyatAudioItem>> = runCatching {
        repository.getAudioItems(topicId)
    }
}
