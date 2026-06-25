package com.almahmoudApp.al_mahmoudapp.feature.quran.data.repository

import com.almahmoudApp.al_mahmoudapp.feature.quran.data.api.QuranVerseApiService
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.api.VerseAudioResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VerseAudioRepository @Inject constructor(
    private val apiService: QuranVerseApiService,
) {
    suspend fun getVerseAudio(surahNumber: Int, ayahNumber: Int): Result<VerseAudioResponse> {
        return try {
            val response = apiService.getVerseAudio(surahNumber, ayahNumber)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
