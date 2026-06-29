package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class MushafLine(
    val lineNumber: Int,
    val lineType: MushafLineType,
    val isCentered: Boolean,
    val words: List<MushafWord>,
    val surahNo: Int? = null,
)

enum class MushafLineType {
    SURAH_NAME,
    AYAH,
    BASMALLAH
}

@Immutable
data class MushafWord(
    val ayahId: Int,
    val wordIndex: Int,
    val text: String,
    val isLastWordOfAyah: Boolean,
)
