package com.almahmoudApp.al_mahmoudapp.feature.images.data.datasource

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImagesLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getWallpaperUrls(): List<String> {
        val jsonString = context.assets.open("wallpaper.json")
            .bufferedReader().use { it.readText() }
        val type = object : TypeToken<Map<String, List<String>>>() {}.type
        val map: Map<String, List<String>> = Gson().fromJson(jsonString, type)
        return map["wallpaper"] ?: emptyList()
    }
}
