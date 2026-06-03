package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class QuranContent(
    val surahs: List<QuranSurah>,
    val readers: List<QuranReader>,
)
