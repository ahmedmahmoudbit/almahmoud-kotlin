package com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerLocation
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.usecase.GetPrayerDashboardUseCase
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.usecase.SetPrayerManualLocationUseCase
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.usecase.UseCurrentPrayerLocationUseCase
import com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.state.PrayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PrayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getPrayerDashboardUseCase: GetPrayerDashboardUseCase,
    private val setPrayerManualLocationUseCase: SetPrayerManualLocationUseCase,
    private val useCurrentPrayerLocationUseCase: UseCurrentPrayerLocationUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(PrayerUiState())
    val state: StateFlow<PrayerUiState> = _state.asStateFlow()

    init {
        loadPrayerDashboard()
    }

    fun openLocationSheet() {
        val dashboard = _state.value.dashboard
        _state.update {
            it.copy(
                isLocationSheetVisible = true,
                manualCity = dashboard?.location?.city.orEmpty(),
                manualCountry = dashboard?.location?.country.orEmpty(),
            )
        }
    }

    fun closeLocationSheet() {
        _state.update { it.copy(isLocationSheetVisible = false, errorMessage = null) }
    }

    fun onCityChanged(city: String) {
        _state.update { it.copy(manualCity = city) }
    }

    fun onCountryChanged(country: String) {
        _state.update { it.copy(manualCountry = country) }
    }

    fun refresh() {
        loadPrayerDashboard(forceRefresh = true)
    }

    fun saveManualLocation() {
        val city = _state.value.manualCity.trim()
        val country = _state.value.manualCountry.trim()
        if (city.isBlank() || country.isBlank()) {
            showError(context.getString(R.string.prayer_location_required))
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            setPrayerManualLocationUseCase(PrayerLocation(city = city, country = country))
                .onSuccess {
                    _state.update { it.copy(isLocationSheetVisible = false) }
                    loadPrayerDashboard(forceRefresh = true)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = error.localizedMessage,
                        )
                    }
                }
        }
    }

    fun useCurrentLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            useCurrentPrayerLocationUseCase()
                .onSuccess {
                    _state.update { it.copy(isLocationSheetVisible = false) }
                    loadPrayerDashboard(forceRefresh = true)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = error.localizedMessage,
                        )
                    }
                }
        }
    }

    fun showError(message: String) {
        _state.update { it.copy(errorMessage = message) }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun loadPrayerDashboard(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            getPrayerDashboardUseCase(forceRefresh)
                .onSuccess { dashboard ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            dashboard = dashboard,
                            prayerCountdownText = dashboard.remainingText,
                            manualCity = dashboard.location.city,
                            manualCountry = dashboard.location.country,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = error.localizedMessage,
                        )
                    }
                }
        }
    }
}
