package com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.util.HijriDateConverter
import com.almahmoudApp.al_mahmoudapp.core.util.NumberLocalization
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerLocation
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.usecase.GetPrayerDashboardUseCase
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.usecase.GetRandomPrayerAyahUseCase
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.usecase.SetPrayerManualLocationUseCase
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.usecase.UseCurrentPrayerLocationUseCase
import com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.state.PrayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.max
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel
class PrayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getPrayerDashboardUseCase: GetPrayerDashboardUseCase,
    private val setPrayerManualLocationUseCase: SetPrayerManualLocationUseCase,
    private val useCurrentPrayerLocationUseCase: UseCurrentPrayerLocationUseCase,
    private val getRandomPrayerAyahUseCase: GetRandomPrayerAyahUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(PrayerUiState())
    val state: StateFlow<PrayerUiState> = _state.asStateFlow()

    private var countdownJob: Job? = null
    private var clockJob: Job? = null
    private var ayahJob: Job? = null

    init {
        loadPrayerDashboard()
        startClockTicker()
        startAyahTicker()
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

    fun refreshAyah() {
        _state.update { it.copy(currentAyah = getRandomPrayerAyahUseCase().ifBlank { it.currentAyah }) }
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
                            prayerCountdownText = formatRemaining(dashboard.nextPrayerAtMillis),
                            manualCity = dashboard.location.city,
                            manualCountry = dashboard.location.country,
                            errorMessage = null,
                        )
                    }
                    startCountdownTicker(dashboard.nextPrayerAtMillis)
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

    private fun startClockTicker() {
        clockJob?.cancel()
        clockJob = viewModelScope.launch {
            while (isActive) {
                updateDateTime()
                delay(CLOCK_REFRESH_MS)
            }
        }
    }

    private fun startAyahTicker() {
        _state.update { it.copy(currentAyah = getRandomPrayerAyahUseCase()) }
        ayahJob?.cancel()
        ayahJob = viewModelScope.launch {
            while (isActive) {
                delay(AYAH_REFRESH_MS)
                val next = getRandomPrayerAyahUseCase()
                if (next.isNotBlank()) {
                    _state.update { it.copy(currentAyah = next) }
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

    private fun updateDateTime() {
        val now = Calendar.getInstance()
        val hijri = HijriDateConverter.fromCalendar(now)
        val isArabic = NumberLocalization.isArabic()

        val dayFormatterLocalized = if (isArabic) dayNameArabicFormatter else dayNameFormatter
        val dayName = NumberLocalization.localize(dayFormatterLocalized.format(now.time))

        val hijriText = NumberLocalization.localize(
            "${hijri.day} ${hijri.monthArabic()} ${hijri.year} هـ",
        )
        val gregorianFormatterLocalized =
            if (isArabic) gregorianArabicFormatter else gregorianFormatter
        val gregorianText = NumberLocalization.localize(gregorianFormatterLocalized.format(now.time))
        val timeText = NumberLocalization.localize(
            timeFormatter.format(now.time).replace("AM", "ص").replace("PM", "م")
                .replace("am", "ص").replace("pm", "م"),
        )
        _state.update {
            it.copy(
                dayName = dayName,
                hijriDate = hijriText,
                gregorianDate = gregorianText,
                currentTime = timeText,
            )
        }
    }

    private fun formatRemaining(nextPrayerAtMillis: Long): String {
        val diff = max(nextPrayerAtMillis - System.currentTimeMillis(), 0L)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
        val raw = "${hours}h ${minutes}m"
        val localized = NumberLocalization.localize("$hours:$minutes")
        return if (NumberLocalization.isArabic()) {
            val parts = localized.split(":")
            "${parts[0]} ساعة ${parts[1]} دقيقة"
        } else {
            raw
        }
    }

    override fun onCleared() {
        countdownJob?.cancel()
        clockJob?.cancel()
        ayahJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val CLOCK_REFRESH_MS = 1_000L
        const val COUNTDOWN_REFRESH_MS = 60_000L
        const val AYAH_REFRESH_MS = 120_000L
        val arabicLocale = Locale("ar")
        val dayNameFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
        val dayNameArabicFormatter = SimpleDateFormat("EEEE", arabicLocale)
        val gregorianFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val gregorianArabicFormatter = SimpleDateFormat("dd MMMM yyyy", arabicLocale)
        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    }
}
