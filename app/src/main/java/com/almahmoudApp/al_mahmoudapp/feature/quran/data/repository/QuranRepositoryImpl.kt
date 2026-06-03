package com.almahmoudApp.al_mahmoudapp.feature.quran.data.repository

import android.util.Log
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.datasource.QuranLocalDataSource
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranContent
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerseDetails
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.repository.QuranRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranRepositoryImpl @Inject constructor(
    private val localDataSource: QuranLocalDataSource,
) : QuranRepository {
    override suspend fun loadQuranContent(): Result<QuranContent> {
        return runCatching {
            localDataSource.loadQuranContent()
        }.onFailure { error ->
            Log.e(TAG, "Failed to load Quran content", error)
        }
    }

    override suspend fun loadVersesBySurah(surahNumber: Int): Result<List<QuranVerse>> {
        return runCatching {
            localDataSource.loadVersesBySurah(surahNumber)
        }.onFailure { error ->
            Log.e(TAG, "Failed to load Quran verses for surah=$surahNumber", error)
        }
    }

    override suspend fun loadVerseDetails(
        surahNumber: Int,
        verseNumber: Int,
    ): Result<QuranVerseDetails> {
        return runCatching {
            localDataSource.loadVerseDetails(surahNumber, verseNumber)
        }.onFailure { error ->
            Log.e(TAG, "Failed to load verse details for $surahNumber:$verseNumber", error)
        }
    }

    private companion object {
        const val TAG = "QuranRepository"
    }
}
