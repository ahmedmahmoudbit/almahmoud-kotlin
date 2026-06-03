package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class QuranVerseDetails(
    val surahNumber: Int,
    val verseNumber: Int,
    val tafseerText: String,
    val maanyText: String,
)
