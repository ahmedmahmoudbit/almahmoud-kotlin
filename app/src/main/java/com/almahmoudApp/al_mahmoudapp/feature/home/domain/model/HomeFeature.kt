package com.almahmoudApp.al_mahmoudapp.feature.home.domain.model

enum class HomeFeatureKey {
    SOUND,
    AYAT,
    IMAGES,
    TAMILAT,
    QOTOF,
    STORIES,
    PRAYER,
    DOAA,
    AZKAR,
    TASBEEH,
    STATUS,
    APPS,
    SUGGESTIONS,
    CARDS,
    RECORDINGS,
}

data class HomeFeature(
    val key: HomeFeatureKey,
    val sortOrder: Int,
)
