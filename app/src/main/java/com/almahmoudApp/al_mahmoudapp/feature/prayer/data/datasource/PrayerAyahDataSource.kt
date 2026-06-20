package com.almahmoudApp.al_mahmoudapp.feature.prayer.data.datasource

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reads and caches the list of Quranic verses from `assets/ayat.txt`.
 * Each non-empty line is treated as a separate verse.
 */
@Singleton
class PrayerAyahDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val cachedAyat: List<String> by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { loadAyat() }

    /** Returns the full cached list of verses. */
    fun ayat(): List<String> = cachedAyat

    private fun loadAyat(): List<String> {
        return runCatching {
            context.assets.open(ASSET_NAME).bufferedReader().useLines { lines ->
                lines.map(String::trim).filter(String::isNotBlank).toList()
            }
        }.getOrDefault(emptyList())
    }

    private companion object {
        const val ASSET_NAME = "ayat.txt"
    }
}
