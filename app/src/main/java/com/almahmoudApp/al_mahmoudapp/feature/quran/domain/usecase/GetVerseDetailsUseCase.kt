package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerseDetails
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.repository.QuranRepository
import javax.inject.Inject

class GetVerseDetailsUseCase @Inject constructor(
    private val repository: QuranRepository,
) {
    suspend operator fun invoke(surahNumber: Int, verseNumber: Int): Result<QuranVerseDetails> {
        return repository.loadVerseDetails(surahNumber, verseNumber)
    }
}
