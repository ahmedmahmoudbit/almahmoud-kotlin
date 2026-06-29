package com.almahmoudApp.al_mahmoudapp.feature.quran.util

object QuranConstants {
    val chapterRange get() = 1..114
    val juzRange get() = 1..30
    val hizbRange get() = 1..60

    fun isChapterValid(chapterNo: Int?) = chapterNo in chapterRange
    fun isJuzValid(juzNo: Int?) = juzNo in juzRange
    fun isHizbValid(hizbNo: Int?) = hizbNo in hizbRange

    fun getAyahId(chapterNo: Int, verseNo: Int): Int = chapterNo * 1000 + verseNo

    fun getChapterAndVerseFromAyahId(ayahId: Int): Pair<Int, Int> {
        val chapterNo = ayahId / 1000
        val ayahNo = ayahId % 1000
        return chapterNo to ayahNo
    }
}
