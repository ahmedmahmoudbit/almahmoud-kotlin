package com.almahmoudApp.al_mahmoudapp.core.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.core.theme.AppLanguage
import com.almahmoudApp.al_mahmoudapp.core.theme.ThemeMode
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.domain.usecase.ObserveOnboardingCompletedUseCase
import com.almahmoudApp.al_mahmoudapp.feature.settings.domain.usecase.ObserveAppLanguageUseCase
import com.almahmoudApp.al_mahmoudapp.feature.settings.domain.usecase.ObserveThemeModeUseCase
import com.almahmoudApp.al_mahmoudapp.feature.settings.domain.usecase.SetAppLanguageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor(
    observeOnboardingCompletedUseCase: ObserveOnboardingCompletedUseCase,
    observeThemeModeUseCase: ObserveThemeModeUseCase,
    observeAppLanguageUseCase: ObserveAppLanguageUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(AppStartupState())
    val state: StateFlow<AppStartupState> = _state.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _appLanguage = MutableStateFlow(AppLanguage.ARABIC)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            observeOnboardingCompletedUseCase().collect { isCompleted ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        isOnboardingCompleted = isCompleted,
                    )
                }
            }
        }
        viewModelScope.launch {
            observeThemeModeUseCase().collect { mode ->
                _themeMode.value = mode
            }
        }
        viewModelScope.launch {
            observeAppLanguageUseCase().collect { language ->
                _appLanguage.value = language
            }
        }
    }
}
