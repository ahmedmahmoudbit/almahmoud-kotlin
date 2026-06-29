package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class JuzInfo(
    val juzNumber: Int,
    val surahRanges: List<SurahVerseRange>,
)

@Immutable
data class HizbInfo(
    val hizbNumber: Int,
    val surahRanges: List<SurahVerseRange>,
)

@Immutable
data class SurahVerseRange(
    val surahNumber: Int,
    val surahName: String,
    val startVerse: Int,
    val endVerse: Int,
)
