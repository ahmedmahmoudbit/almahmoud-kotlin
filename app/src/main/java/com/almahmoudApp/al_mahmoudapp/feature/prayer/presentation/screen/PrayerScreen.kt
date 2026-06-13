package com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.screen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.AppButton
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerDay
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerTimeItem
import com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.state.PrayerUiState
import com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.viewmodel.PrayerViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

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
                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                        .navigationBarsPadding()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    PrayerHeader(
                        location = state.dashboard.location.city,
                        country = state.dashboard.location.country,
                        onBack = onBack,
                        onRefresh = onRefresh,
                    )

                    AnimatedVisibility(visible = state.errorMessage != null) {
                        ErrorBanner(
                            message = state.errorMessage.orEmpty(),
                            onDismiss = onClearError,
                        )
                    }

                    PrayerSummaryCard(
                        day = state.dashboard.today,
                        nextPrayerName = state.dashboard.nextPrayerName,
                        nextPrayerTime = state.dashboard.nextPrayerTime,
                        remainingText = state.dashboard.remainingText,
                        countdownOverride = state.prayerCountdownText,
                    )

                    PrayerTodayRow(
                        day = state.dashboard.today,
                    )

                    PrayerActionsRow(
                        isRefreshing = state.isRefreshing,
                        onCurrentLocationClick = {
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
                        },
                        onChangeLocationClick = onOpenLocationSheet,
                    )
                }
            }

            if (state.isLocationSheetVisible) {
                PrayerLocationSheet(
                    city = state.manualCity,
                    country = state.manualCountry,
                    onCityChanged = onCityChanged,
                    onCountryChanged = onCountryChanged,
                    onUseCurrentLocation = {
                        val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                            androidx.core.content.ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            onUseCurrentLocation()
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                ),
                            )
                        }
                    },
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
private fun PrayerHeader(
    location: String,
    country: String,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = null)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.prayer_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "$location, $country",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        IconButton(onClick = onRefresh) {
            Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
        }
    }
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
private fun PrayerSummaryCard(
    day: PrayerDay,
    nextPrayerName: String,
    nextPrayerTime: String,
    remainingText: String,
    countdownOverride: String,
) {
    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.13f),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.prayer_next_prayer),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = nextPrayerName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = nextPrayerTime,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = countdownOverride.ifBlank { remainingText },
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = day.readableDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PrayerTodayRow(day: PrayerDay) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = stringResource(R.string.prayer_today_times),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            day.prayerTimes.forEach { prayer ->
                PrayerTimeChip(
                    item = prayer,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun PrayerTimeChip(
    item: PrayerTimeItem,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Text(
                text = item.time,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PrayerActionsRow(
    isRefreshing: Boolean,
    onCurrentLocationClick: () -> Unit,
    onChangeLocationClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AppButton(
            text = stringResource(R.string.prayer_use_current_location),
            onClick = onCurrentLocationClick,
            isLoading = isRefreshing,
            icon = Icons.Outlined.MyLocation,
            modifier = Modifier.weight(1f),
        )
        OutlinedButton(
            onClick = onChangeLocationClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.prayer_change_location))
        }
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
    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
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
