package com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.core.data.preferences.AppPreferencesDataSource
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.usecase.GetAyatTopicsUseCase
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.usecase.GetAnasheedItemsUseCase
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.usecase.GetBenefitsItemsUseCase
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.state.AyatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AyatViewModel @Inject constructor(
    private val getAyatTopicsUseCase: GetAyatTopicsUseCase,
    private val getAnasheedItemsUseCase: GetAnasheedItemsUseCase,
    private val getBenefitsItemsUseCase: GetBenefitsItemsUseCase,
    private val appPreferencesDataSource: AppPreferencesDataSource,
) : ViewModel() {
    private val _state = MutableStateFlow(AyatUiState())
    val state: StateFlow<AyatUiState> = _state.asStateFlow()

    init {
        loadData()
        viewModelScope.launch {
            appPreferencesDataSource.favoriteAnasheed.collect { favorites ->
                _state.value = _state.value.copy(favoriteAnasheed = favorites)
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            val topicsResult = getAyatTopicsUseCase()
            val anasheedResult = getAnasheedItemsUseCase()
            val benefitsResult = getBenefitsItemsUseCase()

            if (topicsResult.isSuccess && anasheedResult.isSuccess && benefitsResult.isSuccess) {
                _state.value = AyatUiState(
                    isLoading = false,
                    topics = topicsResult.getOrDefault(emptyList()),
                    anasheed = anasheedResult.getOrDefault(emptyList()),
                    benefits = benefitsResult.getOrDefault(emptyList()),
                    selectedTab = _state.value.selectedTab,
                    favoriteAnasheed = _state.value.favoriteAnasheed,
                    filterType = _state.value.filterType,
                )
            } else {
                val error = topicsResult.exceptionOrNull()?.message
                    ?: anasheedResult.exceptionOrNull()?.message
                    ?: benefitsResult.exceptionOrNull()?.message
                    ?: "Failed to load audio data"
                _state.value = AyatUiState(
                    isLoading = false,
                    errorMessage = error,
                )
            }
        }
    }

    fun selectTab(index: Int) {
        _state.value = _state.value.copy(selectedTab = index)
    }

    fun toggleFavorite(title: String) {
        viewModelScope.launch {
            appPreferencesDataSource.toggleFavoriteAnasheed(title)
        }
    }

    fun setFilterType(type: Int) {
        _state.value = _state.value.copy(filterType = type)
    }
}
