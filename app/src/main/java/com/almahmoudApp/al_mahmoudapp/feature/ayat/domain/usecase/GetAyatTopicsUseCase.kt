package com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatTopic
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.repository.AyatRepository
import javax.inject.Inject

class GetAyatTopicsUseCase @Inject constructor(
    private val repository: AyatRepository,
) {
    suspend operator fun invoke(): Result<List<AyatTopic>> = runCatching {
        repository.getTopics()
    }
}
