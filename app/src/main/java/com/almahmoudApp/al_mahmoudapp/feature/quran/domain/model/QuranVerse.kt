package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class QuranVerse(
    val surahNumber: Int,
    val verseNumber: Int,
    val qcfData: String,
    val content: String,
)
