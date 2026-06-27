package com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.screen

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.EditLocationAlt
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.AppButton
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassCard
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerDay
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerLocation
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerTimeItem
import com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.components.PrayerAyahLine
import com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.components.PrayerDateHeader
import com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.components.PrayerTimeRow
import com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.state.PrayerUiState
import com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.viewmodel.PrayerViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import java.util.Calendar

@Composable
fun PrayerRoute(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    viewModel: PrayerViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    PrayerScreen(
        state = state,
        contentPadding = contentPadding,
        hazeState = hazeState,
        modifier = modifier,
        onBack = onBack,
        onRefresh = viewModel::refresh,
        onOpenLocationSheet = viewModel::openLocationSheet,
        onCloseLocationSheet = viewModel::closeLocationSheet,
        onCityChanged = viewModel::onCityChanged,
        onCountryChanged = viewModel::onCountryChanged,
        onSaveLocation = viewModel::saveManualLocation,
        onUseCurrentLocation = viewModel::useCurrentLocation,
        onClearError = viewModel::clearError,
        onPermissionDenied = { viewModel.showError(it) },
    )
}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun PrayerScreen(
    state: PrayerUiState,
    contentPadding: PaddingValues,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onOpenLocationSheet: () -> Unit,
    onCloseLocationSheet: () -> Unit,
    onCityChanged: (String) -> Unit,
    onCountryChanged: (String) -> Unit,
    onSaveLocation: () -> Unit,
    onUseCurrentLocation: () -> Unit,
    onClearError: () -> Unit,
    onPermissionDenied: (String) -> Unit,
) {
    val context = LocalContext.current
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { granted ->
        val hasPermission = granted[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            granted[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasPermission) {
            onUseCurrentLocation()
        } else {
            onPermissionDenied(context.getString(R.string.prayer_location_permission_required))
        }
    }

    val latestOnUseCurrentLocation by rememberUpdatedState(onUseCurrentLocation)

    fun requestCurrentLocation() {
        val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            latestOnUseCurrentLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .haze(state = hazeState),
        color = MaterialTheme.colorScheme.background,
    ) {
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            PrayerBackground()

            when {
                state.isLoading && state.dashboard == null -> LoadingView()
                state.dashboard == null -> ErrorView(
                    message = state.errorMessage ?: stringResource(R.string.prayer_no_data),
                )
                else -> PrayerContent(
                    state = state,
                    contentPadding = contentPadding,
                    onBack = onBack,
                    onCurrentLocationClick = ::requestCurrentLocation,
                    onManualLocationClick = onOpenLocationSheet,
                    onRefreshClick = onRefresh,
                    onClearError = onClearError,
                )
            }

            if (state.isLocationSheetVisible) {
                PrayerLocationSheet(
                    city = state.manualCity,
                    country = state.manualCountry,
                    onCityChanged = onCityChanged,
                    onCountryChanged = onCountryChanged,
                    onUseCurrentLocation = ::requestCurrentLocation,
                    onSaveLocation = onSaveLocation,
                    onDismiss = onCloseLocationSheet,
                )
            }
        }
    }
}

@Composable
private fun PrayerBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.24f),
                        MaterialTheme.colorScheme.background,
                    ),
                ),
            ),
    )
}

@Composable
private fun PrayerContent(
    state: PrayerUiState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onCurrentLocationClick: () -> Unit,
    onManualLocationClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onClearError: () -> Unit,
) {
    val dashboard = state.dashboard ?: return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 18.dp)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        PrayerTopBar(
            location = dashboard.location,
            isRefreshing = state.isRefreshing,
            onBack = onBack,
            onCurrentLocationClick = onCurrentLocationClick,
            onManualLocationClick = onManualLocationClick,
            onRefreshClick = onRefreshClick,
        )

        AnimatedVisibility(visible = state.errorMessage != null) {
            ErrorBanner(
                message = state.errorMessage.orEmpty(),
                onDismiss = onClearError,
            )
        }

        PrayerDateHeader(
            dayName = state.dayName,
            hijriDate = state.hijriDate,
            gregorianDate = state.gregorianDate,
            currentTime = state.currentTime,
        )

        PrayerAyahLine(ayah = state.currentAyah)

        PrayerTimesSection(day = dashboard.today)

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun PrayerTopBar(
    location: PrayerLocation,
    isRefreshing: Boolean,
    onBack: () -> Unit,
    onCurrentLocationClick: () -> Unit,
    onManualLocationClick: () -> Unit,
    onRefreshClick: () -> Unit,
) {
    var showOptionsMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        // Glass back button
        LiquidGlassCard(
            onClick = onBack,
            modifier = Modifier.size(44.dp),
            cornerRadius = 999.dp,
            refraction = 0.55f,
            frost = 8f,
            dispersion = 0.20f,
            glowAlpha = 0.70f,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.quran_back),
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }

        // Title + location, centered
        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.prayer_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                text = "${location.city}, ${location.country}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
            )
        }

        // Glass options button with floating popup menu
        Box {
            LiquidGlassCard(
                onClick = { showOptionsMenu = !showOptionsMenu },
                modifier = Modifier.size(44.dp),
                cornerRadius = 999.dp,
                refraction = 0.55f,
                frost = 8f,
                dispersion = 0.20f,
                glowAlpha = 0.70f,
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = stringResource(R.string.prayer_options),
                            tint = Color.White,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = showOptionsMenu,
                onDismissRequest = { showOptionsMenu = false },
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0A1628).copy(alpha = 0.96f))
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(16.dp),
                    )
                    .width(220.dp),
            ) {
                PrayerOptionItem(
                    label = stringResource(R.string.prayer_use_current_location),
                    icon = Icons.Rounded.MyLocation,
                    onClick = {
                        showOptionsMenu = false
                        onCurrentLocationClick()
                    },
                )
                OptionDivider()
                PrayerOptionItem(
                    label = stringResource(R.string.prayer_change_location),
                    icon = Icons.Rounded.EditLocationAlt,
                    onClick = {
                        showOptionsMenu = false
                        onManualLocationClick()
                    },
                )
                OptionDivider()
                PrayerOptionItem(
                    label = stringResource(R.string.prayer_refresh),
                    icon = Icons.Rounded.Refresh,
                    onClick = {
                        showOptionsMenu = false
                        onRefreshClick()
                    },
                )
            }
        }
    }
}

@Composable
private fun PrayerOptionItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun OptionDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(Color.White.copy(alpha = 0.10f)),
    )
}

@Composable
private fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        ),
        shape = RoundedCornerShape(22.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(12.dp))
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.prayer_dismiss))
            }
        }
    }
}

@Composable
private fun PrayerTimesSection(day: PrayerDay) {
    val nextIndex = rememberNextPrayerIndex(day)
    val lastPassedIndex = rememberLastPassedIndex(day)
    val isFriday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        day.prayerTimes.forEachIndexed { index, prayer ->
            PrayerTimeRow(
                item = prayer,
                isNext = index == nextIndex,
                isPast = index <= lastPassedIndex && index != nextIndex,
                isFriday = isFriday,
            )
        }
    }
}

/**
 * Computes the index of the next upcoming prayer based on the current device time.
 */
@Composable
private fun rememberNextPrayerIndex(day: PrayerDay): Int {
    return androidx.compose.runtime.remember(day) {
        val now = Calendar.getInstance()
        val firstUpcoming = day.prayerTimes.indexOfFirst { item ->
            parseToCalendar(item)?.let { it.after(now) } ?: false
        }
        if (firstUpcoming >= 0) firstUpcoming else -1
    }
}

/**
 * Computes the index of the most recent prayer that has already passed.
 */
@Composable
private fun rememberLastPassedIndex(day: PrayerDay): Int {
    return androidx.compose.runtime.remember(day) {
        val now = Calendar.getInstance()
        var lastIndex = -1
        day.prayerTimes.forEachIndexed { index, item ->
            parseToCalendar(item)?.let { if (!it.after(now)) lastIndex = index }
        }
        lastIndex
    }
}

private fun parseToCalendar(item: PrayerTimeItem): Calendar? {
    return try {
        val cleanTime = item.time.split(" ").firstOrNull().orEmpty()
        val parts = cleanTime.split(":")
        if (parts.size != 2) return null
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
        calendar.set(Calendar.MINUTE, parts[1].toInt())
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar
    } catch (_: Exception) {
        null
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PrayerLocationSheet(
    city: String,
    country: String,
    onCityChanged: (String) -> Unit,
    onCountryChanged: (String) -> Unit,
    onUseCurrentLocation: () -> Unit,
    onSaveLocation: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(R.string.prayer_manual_location),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            OutlinedTextField(
                value = city,
                onValueChange = onCityChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.prayer_city)) },
                singleLine = true,
            )

            OutlinedTextField(
                value = country,
                onValueChange = onCountryChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = stringResource(R.string.prayer_country)) },
                singleLine = true,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onUseCurrentLocation,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.prayer_use_current_location))
                }
                AppButton(
                    text = stringResource(R.string.prayer_save_location),
                    onClick = onSaveLocation,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
