package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state

import androidx.compose.runtime.Immutable
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranContent
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranMode
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranReader
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranSurah

@Immutable
data class QuranUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedMode: QuranMode = QuranMode.TEXT,
    val query: String = "",
    val content: QuranContent? = null,
    val filteredSurahs: List<QuranSurah> = emptyList(),
    val filteredReaders: List<QuranReader> = emptyList(),
)
