package com.almahmoudApp.al_mahmoudapp.feature.qotof.data.datasource

import android.content.Context
import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.model.QotofContent
import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.model.QotofItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QotofLocalDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val cachedContent: QotofContent by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        QotofContent(items = loadItems())
    }

    fun loadQotofContent(): QotofContent = cachedContent

    private fun loadItems(): List<QotofItem> {
        return context.assets.open(ASSET_NAME).bufferedReader().useLines { lines ->
            lines
                .map(String::trim)
                .filter(String::isNotBlank)
                .mapNotNull { line ->
                    val separatorIndex = line.indexOf(',')
                    if (separatorIndex <= 0) return@mapNotNull null

                    val title = line.substring(0, separatorIndex).trim()
                    val body = line.substring(separatorIndex + 1).trim()

                    if (title.isBlank() || body.isBlank()) {
                        null
                    } else {
                        QotofItem(title = title, body = body)
                    }
                }
                .toList()
        }
    }

    private companion object {
        const val ASSET_NAME = "question.txt"
    }
}
