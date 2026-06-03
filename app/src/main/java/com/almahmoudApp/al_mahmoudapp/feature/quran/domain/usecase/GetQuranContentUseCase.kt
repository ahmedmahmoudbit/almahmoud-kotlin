package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranContent
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.repository.QuranRepository
import javax.inject.Inject

class GetQuranContentUseCase @Inject constructor(
    private val repository: QuranRepository,
) {
    suspend operator fun invoke(): Result<QuranContent> {
        return repository.loadQuranContent()
    }
}
