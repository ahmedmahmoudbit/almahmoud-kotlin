package com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model

data class AnasheedItem(
    val title: String,
    val soundId: Int, // raw resource id (e.g. R.raw.almahmoud1)
    val duration: String
)
