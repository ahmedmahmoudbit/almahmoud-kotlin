package com.almahmoudApp.al_mahmoudapp.feature.stories.data.datasource

import android.content.Context
import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.model.StoryItem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class StoriesLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val storyList by lazy { readStories() }

    fun getStories(): List<StoryItem> = storyList

    fun getStory(index: Int): StoryItem? = storyList.getOrNull(index)

    private fun readStories(): List<StoryItem> {
        return runCatching {
            context.assets.open("stories.txt").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
                    lines
                        .filter { it.isNotBlank() }
                        .mapNotNull { line ->
                            parseStoryLine(line)
                        }
                        .toList()
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun parseStoryLine(line: String): StoryItem? {
        val firstComma = line.indexOf(',')
        val lastComma = line.lastIndexOf(',')
        if (firstComma <= 0 || lastComma <= firstComma) return null

        val title = line.substring(0, firstComma).trim()
        val body = line.substring(firstComma + 1, lastComma).trim()
        val imageUrl = line.substring(lastComma + 1).trim()

        if (title.isBlank() || body.isBlank() || imageUrl.isBlank()) return null
        return StoryItem(title = title, body = body, imageUrl = imageUrl)
    }
}
