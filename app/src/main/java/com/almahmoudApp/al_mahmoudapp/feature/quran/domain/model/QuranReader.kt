package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class QuranReader(
    val name: String,
    val audioBaseUrl: String,
    val imageUrl: String,
)
