package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.repository.QuranRepository
import javax.inject.Inject

class GetQuranVersesUseCase @Inject constructor(
    private val repository: QuranRepository,
) {
    suspend operator fun invoke(surahNumber: Int): Result<List<QuranVerse>> {
        return repository.loadVersesBySurah(surahNumber)
    }
}
