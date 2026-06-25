package com.almahmoudApp.al_mahmoudapp.feature.home.presentation.screen

import AmiriFont
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.draw.clip
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.EmptyView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.liquidSource
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.border
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.width
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeFeature
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeFeatureKey
import com.almahmoudApp.al_mahmoudapp.feature.home.presentation.components.HomeFeatureCard
import com.almahmoudApp.al_mahmoudapp.feature.home.presentation.state.HomeUiState
import com.almahmoudApp.al_mahmoudapp.feature.home.presentation.viewmodel.HomeViewModel
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerDay
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerTimeItem
import com.almahmoudApp.al_mahmoudapp.core.util.NumberLocalization
import androidx.compose.ui.graphics.Color

@Composable
fun HomeRoute(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onFeatureSelected: (HomeFeatureKey) -> Unit = {},
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    val locationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions(),
    ) { granted ->
        val hasPermission = granted[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            granted[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasPermission) {
            viewModel.requestLocationUpdates()
        }
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            viewModel.requestLocationUpdates()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.refreshPrayerTimes()
    }

    HomeScreen(
        state = state,
        contentPadding = contentPadding,
        hazeState = hazeState,
        modifier = modifier,
        onFeatureSelected = onFeatureSelected,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    contentPadding: PaddingValues,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    onFeatureSelected: (HomeFeatureKey) -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .haze(
                state = hazeState,
            )
    ) {
        when {
            state.isLoading -> LoadingView()
            state.errorMessage != null -> ErrorView(message = state.errorMessage)
            state.features.isEmpty() -> EmptyView()
            else -> HomeContent(
                state = state,
                contentPadding = contentPadding,
                onFeatureSelected = onFeatureSelected,
            )
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    contentPadding: PaddingValues,
    onFeatureSelected: (HomeFeatureKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        HomeBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            HomePrayerPreview(
                day = state.prayerDashboard?.today,
                location = state.prayerDashboard?.location?.city.orEmpty(),
                country = state.prayerDashboard?.location?.country.orEmpty(),
                nextPrayerName = state.prayerDashboard?.nextPrayerName.orEmpty(),
                nextPrayerTime = state.prayerDashboard?.nextPrayerTime.orEmpty(),
                countdown = state.prayerCountdownText.ifBlank {
                    state.prayerDashboard?.remainingText.orEmpty()
                },
                isLoading = state.isPrayerLoading,
                errorMessage = state.prayerErrorMessage,
            )

            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
            ) {
                QuoteTicker(quotes = state.quotes)
                Spacer(modifier = Modifier.height(20.dp))

                HomeFeaturePager(
                    features = state.features,
                    onFeatureSelected = onFeatureSelected,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun HomeBackground() {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(
                            Color(0xFF0A0A0A),
                            Color(0xFF121212),
                            Color(0xFF0A0A0A),
                        )
                    } else {
                        listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFF5F5F5),
                            Color(0xFFFFFFFF),
                        )
                    },
                ),
            ),
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun QuoteTicker(
    quotes: List<String>,
    modifier: Modifier = Modifier,
) {
    val tickerText = quotes.joinToString(separator = "  •  ")

    Text(
        text = tickerText,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
            .fillMaxWidth()
            .basicMarquee(
                iterations = Int.MAX_VALUE,
                velocity = 42.dp,
            )
            .padding(vertical = 6.dp),
        maxLines = 1,
    )
}

private fun getLocalizedPrayerName(englishName: String, isFriday: Boolean = false): String {
    if (NumberLocalization.isArabic()) {
        return when (englishName.lowercase().trim()) {
            "fajr" -> "الفجر"
            "sunrise", "shorouq", "shoorooq" -> "الشروق"
            "dhuhr" -> if (isFriday) "الجمعة" else "الظهر"
            "asr" -> "العصر"
            "maghrib" -> "المغرب"
            "isha" -> "العشاء"
            else -> englishName
        }
    } else {
        return when (englishName.lowercase().trim()) {
            "fajr" -> "Fajr"
            "sunrise", "shorouq", "shoorooq" -> "Sunrise"
            "dhuhr" -> if (isFriday) "Friday" else "Dhuhr"
            "asr" -> "Asr"
            "maghrib" -> "Maghrib"
            "isha" -> "Isha"
            else -> englishName
        }
    }
}

@Composable
private fun HomePrayerPreview(
    day: PrayerDay?,
    location: String,
    country: String,
    nextPrayerName: String,
    nextPrayerTime: String,
    countdown: String,
    isLoading: Boolean,
    errorMessage: String?,
) {
    val gold = Color(0xFFFFD54F)
    val backgroundColor = MaterialTheme.colorScheme.background
    val now = java.util.Calendar.getInstance()
    val isFriday = now.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.FRIDAY
    val dayName = remember {
        val isArabic = NumberLocalization.isArabic()
        val locale = if (isArabic) {
            java.util.Locale.Builder().setLanguage("ar").build()
        } else {
            java.util.Locale.getDefault()
        }
        java.text.SimpleDateFormat("EEEE", locale).format(now.time)
    }

    if (isLoading && day == null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
            ),
        ) {
            Text(
                text = stringResource(R.string.loading),
                modifier = Modifier.padding(16.dp),
                fontFamily = AmiriFont,
            )
        }
        return
    }

    if (day == null) {
        if (errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f),
                ),
            ) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontFamily = AmiriFont,
                )
            }
        }
        return
    }

    // Safely parse prayer times
    val parsedTimes = remember(day.prayerTimes) {
        val list = mutableListOf<Pair<PrayerTimeItem, java.util.Calendar>>()
        for (prayer in day.prayerTimes) {
            try {
                val cleanTime = prayer.time.split(" ").firstOrNull().orEmpty()
                val parts = cleanTime.split(":")
                if (parts.size == 2) {
                    val hour = parts[0].toInt()
                    val minute = parts[1].toInt()
                    val cal = java.util.Calendar.getInstance()
                    cal.set(java.util.Calendar.HOUR_OF_DAY, hour)
                    cal.set(java.util.Calendar.MINUTE, minute)
                    cal.set(java.util.Calendar.SECOND, 0)
                    cal.set(java.util.Calendar.MILLISECOND, 0)
                    list.add(Pair(prayer, cal))
                }
            } catch (_: Exception) {
                // Ignore parsing errors
            }
        }
        list
    }

    var currentIndex = -1
    for (i in parsedTimes.indices) {
        val cal = parsedTimes[i].second
        if (now.timeInMillis >= cal.timeInMillis) {
            currentIndex = i
        }
    }

    val prevIndex = if (currentIndex != -1) currentIndex else 0
    val nextIndex = if (currentIndex != -1) {
        (currentIndex + 1).coerceAtMost(parsedTimes.size - 1)
    } else 0

    val progressFraction = remember(currentIndex, prevIndex, nextIndex, now.timeInMillis) {
        if (currentIndex == -1 || prevIndex == nextIndex || parsedTimes.isEmpty()) {
            0.5f
        } else {
            val prevMs = parsedTimes[prevIndex].second.timeInMillis
            val nextMs = parsedTimes[nextIndex].second.timeInMillis
            val total = nextMs - prevMs
            if (total <= 0L) {
                1.0f
            } else {
                val passed = now.timeInMillis - prevMs
                (passed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor),
    ) {
        // Full-bleed background image with transparency
        Image(
            painter = painterResource(id = R.drawable.prayer),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .alpha(0.95f),
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.4f to Color.Transparent,
                            0.8f to backgroundColor.copy(alpha = 0.75f),
                            1.0f to backgroundColor,
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
        ) {
            // Day name (top-left) + location (top-right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = NumberLocalization.localize(dayName),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = AmiriFont,
                    ),
                    color = gold,
                )
                Text(
                    text = if (country.isBlank()) location else "$location, $country",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = AmiriFont,
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    textAlign = TextAlign.End,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Next prayer countdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "${stringResource(R.string.prayer_next_prayer)}: ${getLocalizedPrayerName(nextPrayerName, isFriday)}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = AmiriFont,
                        ),
                        color = gold,
                    )
                    val localizedCountdown = remember(countdown) {
                        val temp = countdown
                            .replace("h", if (NumberLocalization.isArabic()) "س" else "h")
                            .replace("m", if (NumberLocalization.isArabic()) "د" else "m")
                        NumberLocalization.localize(temp)
                    }
                    Text(
                        text = "${stringResource(R.string.prayer_remaining)}: $localizedCountdown",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = AmiriFont),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                    )
                }
                Text(
                    text = NumberLocalization.localize(
                        nextPrayerTime.split(" ").firstOrNull().orEmpty(),
                    ),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = AmiriFont,
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Rounded Linear Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .fillMaxHeight()
                        .background(gold),
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // All prayers visible in a single row (no container)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                day.prayerTimes.forEachIndexed { index, prayer ->
                    val isPast = index <= currentIndex
                    val isNext = index == nextIndex
                    HomePrayerChip(
                        prayer = prayer,
                        isNext = isNext,
                        isPast = isPast,
                        isFriday = isFriday,
                        gold = gold,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

/**
 * A single compact prayer item showing its name and time, without a container,
 * with the active/next prayer highlighted in yellow with a dot underneath.
 */
@Composable
private fun HomePrayerChip(
    prayer: PrayerTimeItem,
    isNext: Boolean,
    isPast: Boolean,
    isFriday: Boolean,
    gold: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .graphicsLayer {
                alpha = if (isPast && !isNext) 0.5f else 1.0f
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = getLocalizedPrayerName(prayer.name, isFriday),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = AmiriFont,
            ),
            color = if (isNext) gold else MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = NumberLocalization.localize(prayer.time.split(" ").firstOrNull().orEmpty()),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = AmiriFont,
                fontWeight = FontWeight.Bold,
            ),
            color = if (isNext) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
        )
        if (isNext) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(gold, shape = androidx.compose.foundation.shape.CircleShape)
            )
        }
    }
}

@Composable
private fun HomeFeaturePager(
    features: List<HomeFeature>,
    onFeatureSelected: (HomeFeatureKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    HomeStaggeredGrid(
        features = features,
        onFeatureSelected = onFeatureSelected,
        modifier = modifier,
    )
}

/**
 * A two-column staggered (masonry) grid. Cards alternate between two heights so the columns never
 * align, producing the staggered look. Items flow top-to-bottom, distributing evenly between the two
 * columns.
 */
@Composable
private fun HomeStaggeredGrid(
    features: List<HomeFeature>,
    onFeatureSelected: (HomeFeatureKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Distribute features across two columns, preserving order.
    val leftColumn = remember(features) { features.filterIndexed { index, _ -> index % 2 == 0 } }
    val rightColumn = remember(features) { features.filterIndexed { index, _ -> index % 2 == 1 } }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StaggeredColumn(
            features = leftColumn,
            onFeatureSelected = onFeatureSelected,
            startIndexEven = true,
            modifier = Modifier.weight(1f),
        )
        StaggeredColumn(
            features = rightColumn,
            onFeatureSelected = onFeatureSelected,
            startIndexEven = false,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StaggeredColumn(
    features: List<HomeFeature>,
    onFeatureSelected: (HomeFeatureKey) -> Unit,
    startIndexEven: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        features.forEachIndexed { index, feature ->
            // Alternate card heights to create the staggered effect.
            val isLarge = ((index + if (startIndexEven) 0 else 1) % 3 == 0)
            val cardModifier = Modifier
                .fillMaxWidth()
                .height(if (isLarge) STAGGER_HEIGHT_LARGE else STAGGER_HEIGHT_SMALL)
            HomeFeatureCard(
                feature = feature,
                onClick = { onFeatureSelected(feature.key) },
                modifier = cardModifier,
            )
        }
    }
}

private val STAGGER_HEIGHT_SMALL = 140.dp
private val STAGGER_HEIGHT_LARGE = 180.dp
