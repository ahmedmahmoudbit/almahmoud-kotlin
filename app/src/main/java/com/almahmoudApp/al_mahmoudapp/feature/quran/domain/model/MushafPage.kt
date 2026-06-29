package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class MushafPage(
    val pageNumber: Int,
    val lines: List<MushafLine>,
    val surahNames: List<String>,
)
