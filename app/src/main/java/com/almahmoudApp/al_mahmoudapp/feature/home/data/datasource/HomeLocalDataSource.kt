package com.almahmoudApp.al_mahmoudapp.feature.home.data.datasource

import android.content.Context
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeFeature
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeFeatureKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class HomeLocalDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    fun loadHomeFeatures(): List<HomeFeature> {
        return HomeFeatureKey.entries
            .filter { it != HomeFeatureKey.STATUS }
            .mapIndexed { index, key ->
                HomeFeature(key = key, sortOrder = index)
            }
    }

    fun loadQuotes(): List<String> {
        return context.assets.open(QUOTES_ASSET).bufferedReader().useLines { lines ->
            lines
                .map(String::trim)
                .filter(String::isNotBlank)
                .take(MAX_QUOTES)
                .toList()
        }
    }

    private companion object {
        const val QUOTES_ASSET = "quotes.txt"
        const val MAX_QUOTES = 40
    }
}
