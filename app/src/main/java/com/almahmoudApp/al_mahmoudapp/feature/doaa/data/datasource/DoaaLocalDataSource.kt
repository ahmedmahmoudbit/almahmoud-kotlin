package com.almahmoudApp.al_mahmoudapp.feature.doaa.data.datasource

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class DoaaLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getDoaa(): List<String> {
        return runCatching {
            context.assets.open("doaa.txt").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
                    lines.filter { it.isNotBlank() }.map { it.trim() }.toList()
                }
            }
        }.getOrDefault(emptyList())
    }
}
