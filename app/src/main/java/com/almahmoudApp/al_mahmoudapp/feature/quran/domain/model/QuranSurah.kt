package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class QuranSurah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val versesCount: Int,
    val revelationType: String,
    val pageNumber: Int,
)
