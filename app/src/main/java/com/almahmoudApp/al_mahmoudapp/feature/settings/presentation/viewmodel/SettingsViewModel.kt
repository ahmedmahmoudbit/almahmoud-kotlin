package com.almahmoudApp.al_mahmoudapp.feature.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.core.theme.AppLanguage
import com.almahmoudApp.al_mahmoudapp.core.theme.ThemeMode
import com.almahmoudApp.al_mahmoudapp.feature.settings.domain.usecase.ObserveAppLanguageUseCase
import com.almahmoudApp.al_mahmoudapp.feature.settings.domain.usecase.ObserveThemeModeUseCase
import com.almahmoudApp.al_mahmoudapp.feature.settings.domain.usecase.SetAppLanguageUseCase
import com.almahmoudApp.al_mahmoudapp.feature.settings.domain.usecase.SetThemeModeUseCase
import com.almahmoudApp.al_mahmoudapp.feature.settings.presentation.state.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeThemeModeUseCase: ObserveThemeModeUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    observeAppLanguageUseCase: ObserveAppLanguageUseCase,
    private val setAppLanguageUseCase: SetAppLanguageUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeThemeModeUseCase().collect { themeMode ->
                _state.update { it.copy(themeMode = themeMode) }
            }
        }
        viewModelScope.launch {
            observeAppLanguageUseCase().collect { language ->
                _state.update { it.copy(appLanguage = language) }
            }
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            setThemeModeUseCase(themeMode)
        }
    }

    fun setAppLanguage(language: AppLanguage) {
        viewModelScope.launch {
            setAppLanguageUseCase(language)
        }
    }
}
