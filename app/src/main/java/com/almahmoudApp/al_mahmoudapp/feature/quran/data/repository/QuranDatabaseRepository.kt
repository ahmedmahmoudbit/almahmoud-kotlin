package com.almahmoudApp.al_mahmoudapp.feature.quran.data.repository

import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.AyahDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.AyahWordDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.MushafDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.NavigationDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.SurahDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.AyahEntity
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.AyahWordEntity
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.MushafLineType
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.MushafMapEntity
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.NavigationRangeEntity
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.NavigationType
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.SurahWithLocalizations
import com.almahmoudApp.al_mahmoudapp.feature.quran.util.QuranConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranDatabaseRepository @Inject constructor(
    private val surahDao: SurahDao,
    private val ayahDao: AyahDao,
    private val ayahWordDao: AyahWordDao,
    private val mushafDao: MushafDao,
    private val navigationDao: NavigationDao,
) {
    companion object {
        private const val DEFAULT_SCRIPT_CODE = "uthmani"
        private const val DEFAULT_MUSHAF_ID = 1
    }

    fun getAllSurahsWithLocalizations(): Flow<List<SurahWithLocalizations>> {
        return surahDao.getAllSurahsWithLocalizations()
    }

    suspend fun getSurahWithLocalizations(chapterNo: Int): SurahWithLocalizations? {
        return surahDao.getSurahWithLocalization(chapterNo)
    }

    suspend fun getSurahsByNos(chapterNos: List<Int>): Map<Int, SurahWithLocalizations> {
        if (chapterNos.isEmpty()) return emptyMap()
        return surahDao.getSurahsWithLocalizationsByNos(chapterNos)
            .associateBy { it.surah.surahNo }
    }

    suspend fun getNumberOfPages(mushafId: Int = DEFAULT_MUSHAF_ID): Int {
        if (mushafId <= 0) return 0
        return mushafDao.getPageCount(mushafId) ?: 0
    }

    suspend fun getPageLines(mushafId: Int = DEFAULT_MUSHAF_ID, pageNo: Int): List<MushafMapEntity> {
        if (mushafId <= 0 || pageNo <= 0) return emptyList()
        return mushafDao.getPageLines(mushafId, pageNo)
    }

    suspend fun getFirstPageOfChapter(
        chapterNo: Int,
        mushafId: Int = DEFAULT_MUSHAF_ID
    ): Int? {
        if (mushafId <= 0 || chapterNo <= 0) return null
        return mushafDao.getFirstPageOfChapter(mushafId, chapterNo)
    }

    suspend fun getPageForVerse(
        surahNo: Int,
        ayahNo: Int,
        mushafId: Int = DEFAULT_MUSHAF_ID
    ): Int? {
        if (mushafId <= 0 || surahNo <= 0 || ayahNo <= 0) return null
        return mushafDao.getPageForVerse(mushafId, QuranConstants.getAyahId(surahNo, ayahNo))
    }

    suspend fun getFirstPageOfJuz(
        juzNo: Int,
        mushafId: Int = DEFAULT_MUSHAF_ID
    ): Int? {
        if (mushafId <= 0 || juzNo <= 0) return null
        return mushafDao.getFirstPageOfJuz(mushafId, juzNo)
    }

    suspend fun getFirstPageOfHizb(
        hizbNo: Int,
        mushafId: Int = DEFAULT_MUSHAF_ID
    ): Int? {
        if (mushafId <= 0 || hizbNo <= 0) return null
        return mushafDao.getFirstPageOfHizb(mushafId, hizbNo)
    }

    suspend fun getAyahsBySurah(surahNo: Int): List<AyahEntity> {
        return ayahDao.getAyahsInRange(surahNo, 1, 999)
    }

    suspend fun getAyah(surahNo: Int, ayahNo: Int): AyahEntity? {
        return ayahDao.getAyah(surahNo, ayahNo)
    }

    suspend fun getWordsForAyah(
        chapterNo: Int,
        verseNo: Int,
        scriptCode: String = DEFAULT_SCRIPT_CODE
    ): List<AyahWordEntity> {
        val words = ayahWordDao.getWordsForAyah(chapterNo, verseNo, scriptCode)
            .sortedBy { it.wordIndex }
        val lastWordIndex = words.lastOrNull()?.wordIndex
        return words.map {
            it.apply { isLastWordOfAyah = it.wordIndex == lastWordIndex }
        }
    }

    suspend fun getWordsForAyahById(
        ayahId: Int,
        scriptCode: String = DEFAULT_SCRIPT_CODE
    ): List<AyahWordEntity> {
        val words = ayahWordDao.getWordsForAyahById(ayahId, scriptCode)
            .sortedBy { it.wordIndex }
        val lastWordIndex = words.lastOrNull()?.wordIndex
        return words.map {
            it.apply { isLastWordOfAyah = it.wordIndex == lastWordIndex }
        }
    }

    suspend fun resolveMushafLineWords(
        row: MushafMapEntity,
        scriptCode: String = DEFAULT_SCRIPT_CODE,
    ): List<AyahWordEntity> {
        if (row.lineType != MushafLineType.ayah) return emptyList()

        val startAyah = row.startAyahId ?: return emptyList()
        val endAyah = row.endAyahId ?: return emptyList()
        val startWi = row.startWordIndex ?: return emptyList()
        val endWi = row.endWordIndex ?: return emptyList()

        if (startAyah > endAyah) return emptyList()

        if (startAyah == endAyah) {
            val words = ayahWordDao.getWordsForAyahByIndexRange(
                startAyah, scriptCode, startWi, endWi
            )
            val lastWordIndex = ayahWordDao.getLastWordIndexForAyah(startAyah, scriptCode)
            return words.map {
                it.apply { isLastWordOfAyah = lastWordIndex != null && it.wordIndex == lastWordIndex }
            }
        }

        val ayahIds = (startAyah..endAyah).toList()
        val allWords = ayahWordDao.getWordsForAyahs(ayahIds, scriptCode)
        val lastWordIndexes = mutableMapOf<Int, Int?>()
        for (id in ayahIds) {
            lastWordIndexes[id] = allWords.lastOrNull { it.ayahId == id }?.wordIndex
        }

        return allWords
            .filter { word ->
                when (word.ayahId) {
                    startAyah -> word.wordIndex >= startWi
                    endAyah -> word.wordIndex <= endWi
                    else -> true
                }
            }
            .map { word ->
                val maxIndex = lastWordIndexes[word.ayahId]
                word.apply { isLastWordOfAyah = maxIndex != null && word.wordIndex == maxIndex }
            }
    }

    suspend fun getChapterVerseRangesInJuz(juzNo: Int): List<Pair<Int, IntRange>> {
        if (juzNo <= 0) return emptyList()
        val ayahs = ayahDao.getAyahsByJuz(juzNo)
        if (ayahs.isEmpty()) return emptyList()

        return ayahs.groupBy { it.surahNo }
            .entries
            .sortedBy { it.key }
            .map { (surahNo, list) ->
                val minAyah = list.minOf { it.ayahNo }
                val maxAyah = list.maxOf { it.ayahNo }
                surahNo to (minAyah..maxAyah)
            }
    }

    suspend fun getChapterVerseRangesInHizb(hizbNo: Int): List<Pair<Int, IntRange>> {
        if (hizbNo <= 0) return emptyList()
        val ayahs = ayahDao.getAyahsByHizb(hizbNo)
        if (ayahs.isEmpty()) return emptyList()

        return ayahs.groupBy { it.surahNo }
            .entries
            .sortedBy { it.key }
            .map { (surahNo, list) ->
                val minAyah = list.minOf { it.ayahNo }
                val maxAyah = list.maxOf { it.ayahNo }
                surahNo to (minAyah..maxAyah)
            }
    }

    suspend fun getJuzForMushafPages(
        mushafId: Int = DEFAULT_MUSHAF_ID,
        pageNumbers: List<Int>,
    ): Map<Int, Int> {
        if (mushafId <= 0 || pageNumbers.isEmpty()) return emptyMap()
        return mushafDao.getJuzForPages(mushafId, pageNumbers)
            .associate { it.pageNumber to it.juzNo }
    }

    suspend fun getHizbForMushafPages(
        mushafId: Int = DEFAULT_MUSHAF_ID,
        pageNumbers: List<Int>,
    ): Map<Int, List<Int>> {
        if (mushafId <= 0 || pageNumbers.isEmpty()) return emptyMap()
        return mushafDao.getHizbForPages(mushafId, pageNumbers)
            .groupBy { it.pageNumber }
            .mapValues { (_, rows) -> rows.map { it.hizbNo }.distinct().sorted() }
    }

    suspend fun getSurahNosWithSajdah(): Set<Int> {
        return ayahDao.getDistinctSurahNosWithSajdah().toSet()
    }

    suspend fun getJuzNosForChapter(chapterNo: Int): List<Int> {
        return ayahDao.getDistinctJuzNosForSurah(chapterNo)
    }

    fun getJuzRanges(): Flow<List<NavigationRangeEntity>> {
        return navigationDao.getRanges(NavigationType.juz)
    }

    fun getHizbRanges(): Flow<List<NavigationRangeEntity>> {
        return navigationDao.getRanges(NavigationType.hizb)
    }

    suspend fun getChapterName(chapterNo: Int): String {
        if (chapterNo <= 0) return ""
        val surah = surahDao.getSurahWithLocalization(chapterNo) ?: return ""
        return surah.getBestName()
    }

    suspend fun getChapterNames(chapterNos: List<Int>): Map<Int, String> {
        if (chapterNos.isEmpty()) return emptyMap()
        val surahs = surahDao.getSurahsWithLocalizationsByNos(chapterNos)
        return surahs.associate { it.surah.surahNo to it.getBestName() }
    }

    suspend fun getChapterVerseCount(chapterNo: Int): Int {
        if (!QuranConstants.isChapterValid(chapterNo)) return 0
        return surahDao.getSurah(chapterNo)?.ayahCount ?: 0
    }
}
