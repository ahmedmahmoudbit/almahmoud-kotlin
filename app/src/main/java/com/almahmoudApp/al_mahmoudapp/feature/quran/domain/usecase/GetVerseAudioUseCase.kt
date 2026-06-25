package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.quran.data.api.VerseAudioResponse
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.repository.VerseAudioRepository
import javax.inject.Inject

class GetVerseAudioUseCase @Inject constructor(
    private val repository: VerseAudioRepository,
) {
    suspend operator fun invoke(surahNumber: Int, ayahNumber: Int): Result<VerseAudioResponse> {
        return repository.getVerseAudio(surahNumber, ayahNumber)
    }
}
