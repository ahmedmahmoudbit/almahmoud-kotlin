package com.almahmoudApp.al_mahmoudapp.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.usecase.GetHomeFeaturesUseCase
import com.almahmoudApp.al_mahmoudapp.feature.home.presentation.state.HomeUiState
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.usecase.GetPrayerDashboardUseCase
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.usecase.UseCurrentPrayerLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlinx.coroutines.Job

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeFeaturesUseCase: GetHomeFeaturesUseCase,
    private val getPrayerDashboardUseCase: GetPrayerDashboardUseCase,
    private val useCurrentPrayerLocationUseCase: UseCurrentPrayerLocationUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
    private var countdownJob: Job? = null

    init {
        loadHomeFeatures()
        loadPrayerSummary()
    }

    fun requestLocationUpdates() {
        viewModelScope.launch {
            useCurrentPrayerLocationUseCase()
                .onSuccess {
                    loadPrayerSummary()
                }
        }
    }

    fun refreshPrayerTimes() {
        loadPrayerSummary()
    }

    private fun loadHomeFeatures() {
        viewModelScope.launch {
            getHomeFeaturesUseCase()
                .onSuccess { content ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            features = content.features,
                            quotes = content.quotes,
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

    private fun loadPrayerSummary() {
        viewModelScope.launch {
            getPrayerDashboardUseCase()
                .onSuccess { dashboard ->
                    _state.update {
                        it.copy(
                            prayerDashboard = dashboard,
                            prayerCountdownText = formatRemaining(dashboard.nextPrayerAtMillis),
                            isPrayerLoading = false,
                            prayerErrorMessage = null,
                        )
                    }
                    startCountdownTicker(dashboard.nextPrayerAtMillis)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isPrayerLoading = false,
                            prayerErrorMessage = error.localizedMessage,
                        )
                    }
                }
        }
    }

    private fun startCountdownTicker(nextPrayerAtMillis: Long) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                _state.update {
                    it.copy(prayerCountdownText = formatRemaining(nextPrayerAtMillis))
                }
                delay(COUNTDOWN_REFRESH_MS)
            }
        }
    }

    private fun formatRemaining(nextPrayerAtMillis: Long): String {
        val diff = max(nextPrayerAtMillis - System.currentTimeMillis(), 0L)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
        return "${hours}h ${minutes}m"
    }

    override fun onCleared() {
        countdownJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val COUNTDOWN_REFRESH_MS = 60_000L
    }
}
