package com.almahmoudApp.al_mahmoudapp.feature.ayat.data.datasource

import android.content.Context
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatAudioItem
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatTopic
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AnasheedItem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class AyatLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val topicList by lazy {
        listOf(
            AyatTopic(
                id = 0,
                title = context.getString(R.string.ayat_topic_dolm_title),
                description = context.getString(R.string.ayat_topic_dolm_desc),
                backgroundUrl = "https://drive.google.com/uc?id=1MJsVejGPThecpWqS-1sT79idgB4qe74j",
                durationAsset = "duration_dolm.txt",
                lottieRawRes = R.raw.love1,
            ),
            AyatTopic(
                id = 1,
                title = context.getString(R.string.ayat_topic_hozn_title),
                description = context.getString(R.string.ayat_topic_hozn_desc),
                backgroundUrl = "https://drive.google.com/uc?id=1fViRkEWEcb1TqsmstSuxGL8P4oaNtTxN",
                durationAsset = "duration_hozn.txt",
                lottieRawRes = R.raw.love2,
            ),
            AyatTopic(
                id = 2,
                title = context.getString(R.string.ayat_topic_rezk_title),
                description = context.getString(R.string.ayat_topic_rezk_desc),
                backgroundUrl = "https://drive.google.com/uc?id=19WvmxHyMb1TtmJ4cr4_hlUivwiblGMp5",
                durationAsset = "duration_rezik.txt",
                lottieRawRes = R.raw.love3,
            ),
            AyatTopic(
                id = 3,
                title = context.getString(R.string.ayat_topic_sabr_title),
                description = context.getString(R.string.ayat_topic_sabr_desc),
                backgroundUrl = "https://drive.google.com/uc?id=1s2KIMwwNvZWwkMBUennMFnqipDlOZYvE",
                durationAsset = "duration_sabr.txt",
                lottieRawRes = R.raw.moon,
            ),
            AyatTopic(
                id = 4,
                title = context.getString(R.string.ayat_topic_adma_title),
                description = context.getString(R.string.ayat_topic_adma_desc),
                backgroundUrl = "https://drive.google.com/uc?id=1s2KIMwwNvZWwkMBUennMFnqipDlOZYvE",
                durationAsset = "duration_adama.txt",
                lottieRawRes = R.raw.love5,
            ),
            AyatTopic(
                id = 5,
                title = context.getString(R.string.ayat_topic_jana_title),
                description = context.getString(R.string.ayat_topic_jana_desc),
                backgroundUrl = "https://drive.google.com/uc?id=1Ik7OsU0GFovf4H1zoTQLiuUNgxT17BFm",
                durationAsset = "duration_jana.txt",
                lottieRawRes = R.raw.love6,
            ),
            AyatTopic(
                id = 6,
                title = context.getString(R.string.ayat_topic_sakina_title),
                description = context.getString(R.string.ayat_topic_sakina_desc),
                backgroundUrl = "https://drive.google.com/uc?id=1NWxsk0cCIBPUpFM2zbEljsf0DoYvABA1",
                durationAsset = "duration_sakina.txt",
                lottieRawRes = R.raw.love7,
            ),
            AyatTopic(
                id = 7,
                title = context.getString(R.string.ayat_topic_tawba_title),
                description = context.getString(R.string.ayat_topic_tawba_desc),
                backgroundUrl = "https://drive.google.com/uc?id=1_4gBzvtAvZ7hok8gZqnzfNnlQgasotCh",
                durationAsset = "duration_tawba.txt",
                lottieRawRes = R.raw.sun,
            ),
            AyatTopic(
                id = 8,
                title = context.getString(R.string.ayat_topic_alakera_title),
                description = context.getString(R.string.ayat_topic_alakera_desc),
                backgroundUrl = "https://drive.google.com/uc?id=1wAk90eauAXZJw0KUFSRQEjKkn3vk7-TN",
                durationAsset = "duration_akera.txt",
                lottieRawRes = R.raw.flower,
            ),
        )
    }

    fun getTopics(): List<AyatTopic> = topicList

    fun getTopic(topicId: Int): AyatTopic? = topicList.firstOrNull { it.id == topicId }

    fun getAudioItems(topicId: Int): List<AyatAudioItem> {
        val topic = getTopic(topicId) ?: return emptyList()
        return readAudioFile(topic.durationAsset)
    }

    fun getAnasheedItems(): List<AnasheedItem> {
        return runCatching {
            val dataLines = context.assets.open("data_anashed.txt").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readLines()
            }
            val durationLines = context.assets.open("duration_anasheed.txt").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readLines()
            }

            dataLines.mapIndexedNotNull { index, soundName ->
                val durationLine = durationLines.getOrNull(index) ?: return@mapIndexedNotNull null
                val parts = durationLine.split(",", limit = 2).map { it.trim() }
                val title = parts.getOrNull(0).orEmpty()
                val duration = parts.getOrNull(1).orEmpty()
                val soundId = context.resources.getIdentifier(soundName.trim(), "raw", context.packageName)
                if (soundId != 0 && title.isNotBlank()) {
                    AnasheedItem(title = title, soundId = soundId, duration = duration)
                } else {
                    null
                }
            }
        }.getOrDefault(emptyList())
    }

    fun getBenefitsItems(): List<AyatAudioItem> {
        return readAudioFile("benefits_islamic.txt")
    }

    private fun readAudioFile(assetName: String): List<AyatAudioItem> {
        return runCatching {
            context.assets.open(assetName).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
                    lines
                        .filter { it.isNotBlank() }
                        .mapNotNull { line ->
                            val parts = line.split(",", limit = 3).map { it.trim() }
                            val title = parts.getOrNull(0).orEmpty()
                            val duration = parts.getOrNull(1).orEmpty()
                            val url = parts.getOrNull(2).orEmpty()
                            if (title.isBlank() || url.isBlank()) {
                                null
                            } else {
                                AyatAudioItem(
                                    title = title,
                                    duration = duration,
                                    url = url,
                                )
                            }
                        }
                        .toList()
                }
            }
        }.getOrDefault(emptyList())
    }
}
