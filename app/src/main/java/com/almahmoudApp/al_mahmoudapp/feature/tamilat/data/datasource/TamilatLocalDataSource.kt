package com.almahmoudApp.al_mahmoudapp.feature.tamilat.data.datasource

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TamilatLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun loadTamilat(): List<String> = withContext(Dispatchers.IO) {
        context.assets.open("quotes.txt").bufferedReader().useLines { lines ->
            lines.map { it.trim() }.filter { it.isNotEmpty() }.toList()
        }
    }
}
