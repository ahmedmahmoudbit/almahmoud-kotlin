package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranContent
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranMode
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranReader
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranSurah
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase.GetQuranContentUseCase
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.screen.FilterType
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.QuranUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.almahmoudApp.al_mahmoudapp.core.data.preferences.AppPreferencesDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class QuranViewModel @Inject constructor(
    private val getQuranContentUseCase: GetQuranContentUseCase,
    private val appPreferencesDataSource: AppPreferencesDataSource,
) : ViewModel() {
    private val _state = MutableStateFlow(QuranUiState())
    val state: StateFlow<QuranUiState> = _state.asStateFlow()

    init {
        loadQuranContent()
        observeFavorites()
    }

    fun onQueryChange(query: String) {
        _state.update { current ->
            current.copy(
                query = query,
                filteredSurahs = filterSurahsByType(current.content, current.selectedFilter, query),
                filteredReaders = filterReaders(current.content, query),
                errorMessage = null,
            )
        }
    }

    fun onModeChange(mode: QuranMode) {
        _state.update { current ->
            current.copy(selectedMode = mode)
        }
    }

    fun onFilterChange(filter: FilterType) {
        _state.update { current ->
            current.copy(
                selectedFilter = filter,
                filteredSurahs = filterSurahsByType(current.content, filter, current.query)
            )
        }
    }

    fun retry() {
        loadQuranContent()
    }

    fun toggleFavorite(surahNumber: Int) {
        viewModelScope.launch {
            appPreferencesDataSource.toggleFavoriteSurah(surahNumber)
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            appPreferencesDataSource.favoriteSurahs.collect { favoriteSet ->
                _state.update { current ->
                    val content = current.content ?: return@update current
                    val updatedContent = content.copy(
                        surahs = content.surahs.map { surah ->
                            surah.copy(isFavorite = favoriteSet.contains(surah.number.toString()))
                        }
                    )
                    current.copy(
                        content = updatedContent,
                        filteredSurahs = filterSurahsByType(updatedContent, current.selectedFilter, current.query)
                    )
                }
            }
        }
    }

    private fun loadQuranContent() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getQuranContentUseCase()
                .onSuccess { content ->
                    val favoriteSet = try {
                        appPreferencesDataSource.favoriteSurahs.first()
                    } catch (e: Exception) {
                        emptySet()
                    }
                    val mappedContent = content.copy(
                        surahs = content.surahs.map { surah ->
                            surah.copy(isFavorite = favoriteSet.contains(surah.number.toString()))
                        }
                    )
                    _state.update {
                        it.copy(
                            isLoading = false,
                            content = mappedContent,
                            filteredSurahs = filterSurahsByType(mappedContent, it.selectedFilter, it.query),
                            filteredReaders = filterReaders(mappedContent, it.query),
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage,
                        )
                    }
                }
        }
    }

    private fun filterSurahs(content: QuranContent?, query: String): List<QuranSurah> {
        val surahs = content?.surahs.orEmpty()
        if (query.isBlank()) {
            return surahs
        }

        val normalizedQuery = query.trim()
        return surahs.filter { surah ->
            surah.nameArabic.contains(normalizedQuery, ignoreCase = true) ||
                surah.nameEnglish.contains(normalizedQuery, ignoreCase = true) ||
                surah.revelationType.contains(normalizedQuery, ignoreCase = true) ||
                surah.versesCount.toString().contains(normalizedQuery) ||
                surah.pageNumber.toString().contains(normalizedQuery)
        }
    }

    private fun filterSurahsByType(content: QuranContent?, filter: FilterType, query: String): List<QuranSurah> {
        val surahs = content?.surahs.orEmpty()
        
        val filteredByType = when (filter) {
            FilterType.ALL -> surahs
            FilterType.MAKKAH -> surahs.filter {
                it.revelationType.contains("مك", ignoreCase = true) ||
                    it.revelationType.contains("mak", ignoreCase = true)
            }
            FilterType.MADINAH -> surahs.filter {
                it.revelationType.contains("مدن", ignoreCase = true) ||
                    it.revelationType.contains("med", ignoreCase = true)
            }
            FilterType.FAVORITES -> surahs.filter { it.isFavorite }
        }
        
        if (query.isBlank()) {
            return filteredByType
        }

        val normalizedQuery = query.trim()
        return filteredByType.filter { surah ->
            surah.nameArabic.contains(normalizedQuery, ignoreCase = true) ||
                surah.nameEnglish.contains(normalizedQuery, ignoreCase = true) ||
                surah.revelationType.contains(normalizedQuery, ignoreCase = true) ||
                surah.versesCount.toString().contains(normalizedQuery) ||
                surah.pageNumber.toString().contains(normalizedQuery)
        }
    }

    private fun filterReaders(content: QuranContent?, query: String): List<QuranReader> {
        val readers = content?.readers.orEmpty()
        if (query.isBlank()) {
            return readers
        }

        val normalizedQuery = query.trim()
        return readers.filter { reader ->
            reader.name.contains(normalizedQuery, ignoreCase = true)
        }
    }
}
