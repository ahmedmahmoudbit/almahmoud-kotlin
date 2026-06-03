package com.almahmoudApp.al_mahmoudapp.feature.quran.data.datasource

import android.content.Context
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranContent
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranReader
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranSurah
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerseDetails
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import org.json.JSONArray
import org.json.JSONObject

@Singleton
class QuranLocalDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val cachedContent: QuranContent by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        QuranContent(
            surahs = loadSurahs(),
            readers = loadReaders(),
        )
    }
    private val cachedVerses: List<QuranVerse> by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        loadVerses()
    }
    private val cachedTafseer: List<QuranVerseDetails> by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        loadVerseDetails()
    }

    fun loadQuranContent(): QuranContent = cachedContent

    fun loadVersesBySurah(surahNumber: Int): List<QuranVerse> {
        return cachedVerses.filter { verse -> verse.surahNumber == surahNumber }
    }

    fun loadVerseDetails(surahNumber: Int, verseNumber: Int): QuranVerseDetails {
        return cachedTafseer.firstOrNull { details ->
            details.surahNumber == surahNumber && details.verseNumber == verseNumber
        } ?: QuranVerseDetails(
            surahNumber = surahNumber,
            verseNumber = verseNumber,
            tafseerText = "لا يوجد تفسير متاح لهذه الآية",
            maanyText = "لا توجد معاني متاحة لهذه الآية",
        )
    }

    private fun loadSurahs(): List<QuranSurah> {
        val json = context.assets.open(QURAN_SURAHS_JSON_ASSET).bufferedReader().use { reader ->
            reader.readText()
        }
        val surahs = JSONArray(json)

        return buildList {
            for (index in 0 until surahs.length()) {
                val item = surahs.getJSONObject(index)
                add(
                    QuranSurah(
                        number = item.getInt("number"),
                        nameArabic = item.getString("nameArabic"),
                        nameEnglish = item.getString("nameEnglish"),
                        versesCount = item.getInt("versesCount"),
                        revelationType = item.getString("revelationType"),
                        pageNumber = item.getInt("pageNumber"),
                    )
                )
            }
        }
    }

    private fun loadReaders(): List<QuranReader> {
        return readAssetLines(READERS_ASSET).mapNotNull { line ->
            val parts = line.split(',').map(String::trim)
            val name = parts.getOrNull(0).orEmpty()
            val audioBaseUrl = parts.getOrNull(1).orEmpty()
            val imageUrl = parts.getOrNull(2).orEmpty()

            if (name.isBlank() || audioBaseUrl.isBlank()) {
                null
            } else {
                QuranReader(
                    name = name,
                    audioBaseUrl = audioBaseUrl,
                    imageUrl = imageUrl,
                )
            }
        }
    }

    private fun loadVerses(): List<QuranVerse> {
        val json = context.assets.open(QURAN_VERSES_ASSET).bufferedReader().use { reader ->
            reader.readText()
        }
        val verses = JSONArray(json)

        return buildList {
            for (index in 0 until verses.length()) {
                val item: JSONObject = verses.getJSONObject(index)
                add(
                    QuranVerse(
                        surahNumber = item.getInt("surah_number"),
                        verseNumber = item.getInt("verse_number"),
                        qcfData = item.optString("qcfData"),
                        content = item.getString("content"),
                    )
                )
            }
        }
    }

    private fun loadVerseDetails(): List<QuranVerseDetails> {
        val tafseer = readDetailsAsset(TAFSEER_ASSET)
        val maany = readDetailsAsset(MAANY_ASSET)
        val maanyMap = maany.associateBy(
            keySelector = { "${it.first}:${it.second}" },
            valueTransform = { it.third },
        )

        return tafseer.map { tafseerItem ->
            val key = "${tafseerItem.first}:${tafseerItem.second}"
            QuranVerseDetails(
                surahNumber = tafseerItem.first,
                verseNumber = tafseerItem.second,
                tafseerText = tafseerItem.third,
                maanyText = maanyMap[key] ?: "لا توجد معاني متاحة لهذه الآية",
            )
        }
    }

    private fun readDetailsAsset(fileName: String): List<Triple<Int, Int, String>> {
        val json = context.assets.open(fileName).bufferedReader().use { reader ->
            reader.readText()
        }
        val items = JSONArray(json)

        return buildList {
            for (index in 0 until items.length()) {
                val item = items.getJSONObject(index)
                add(
                    Triple(
                        first = item.getInt("sura"),
                        second = item.getInt("aya"),
                        third = item.getString("text"),
                    )
                )
            }
        }
    }

    private fun readAssetLines(fileName: String): List<String> {
        return context.assets.open(fileName).bufferedReader().useLines { lines ->
            lines.map(String::trim).filter(String::isNotBlank).toList()
        }
    }

    private companion object {
        const val READERS_ASSET = "quran_reader.txt"
        const val QURAN_SURAHS_JSON_ASSET = "quran_surahs.json"
        const val QURAN_VERSES_ASSET = "quran.json"
        const val TAFSEER_ASSET = "tafser.json"
        const val MAANY_ASSET = "maany.json"
    }
}
