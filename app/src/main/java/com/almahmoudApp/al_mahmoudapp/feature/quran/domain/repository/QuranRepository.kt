package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.repository

import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranContent
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerseDetails
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse

interface QuranRepository {
    suspend fun loadQuranContent(): Result<QuranContent>
    suspend fun loadVersesBySurah(surahNumber: Int): Result<List<QuranVerse>>
    suspend fun loadVerseDetails(surahNumber: Int, verseNumber: Int): Result<QuranVerseDetails>
}
