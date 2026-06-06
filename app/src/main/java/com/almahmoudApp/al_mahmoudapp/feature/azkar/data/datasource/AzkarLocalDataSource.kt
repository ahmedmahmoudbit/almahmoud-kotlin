package com.almahmoudApp.al_mahmoudapp.feature.azkar.data.datasource

import android.content.Context
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.AzkarCategory
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.ZikrItem
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class AzkarLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getAzkar(category: AzkarCategory): List<ZikrItem> {
        return runCatching {
            context.assets.open(category.assetPath).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
                    lines.filter { it.isNotBlank() }.mapNotNull { line ->
                        val parts = line.split(",")
                        if (parts.size >= 2) {
                            ZikrItem(text = parts[0].trim(), count = parts[1].trim())
                        } else {
                            null
                        }
                    }.toList()
                }
            }
        }.getOrDefault(emptyList())
    }
}
