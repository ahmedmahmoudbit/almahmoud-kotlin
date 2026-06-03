package com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatTopic
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.repository.AyatRepository
import javax.inject.Inject

class GetAyatTopicUseCase @Inject constructor(
    private val repository: AyatRepository,
) {
    suspend operator fun invoke(topicId: Int): Result<AyatTopic> = runCatching {
        repository.getTopic(topicId) ?: error("Topic not found")
    }
}
