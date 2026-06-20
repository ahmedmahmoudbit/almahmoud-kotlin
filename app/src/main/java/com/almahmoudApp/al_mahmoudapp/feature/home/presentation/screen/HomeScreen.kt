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
    LiquidHost(modifier = modifier.fillMaxSize()) {
        HomeBackground()
        Image(
            painter = painterResource(id = R.drawable.b7),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .liquidSource()
                .alpha(0.4f),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(8.dp))
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

            Spacer(modifier = Modifier.height(20.dp))
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .liquidSource()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        MaterialTheme.colorScheme.background,
                    ),
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
        color = MaterialTheme.colorScheme.primary,
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

private fun getArabicPrayerName(englishName: String, isFriday: Boolean = false): String {
    return when (englishName.lowercase().trim()) {
        "fajr" -> "الفجر"
        "sunrise", "shorouq", "shoorooq" -> "الشروق"
        "dhuhr" -> if (isFriday) "الجمعة" else "الظهر"
        "asr" -> "العصر"
        "maghrib" -> "المغرب"
        "isha" -> "العشاء"
        else -> englishName
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
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Full-bleed background image
        Image(
            painter = painterResource(id = R.drawable.prayer),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth(),
        )

        // Dark scrim for readability + top accent, fading to the screen background at the bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.55f),
                            Color.Black.copy(alpha = 0.65f),
                            backgroundColor,
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
                    color = Color.White.copy(alpha = 0.9f),
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
                        text = "الصلاة القادمة: ${getArabicPrayerName(nextPrayerName, isFriday)}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = AmiriFont,
                        ),
                        color = gold,
                    )
                    Text(
                        text = "المتبقي: ${NumberLocalization.localize(countdown)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = AmiriFont),
                        color = Color.White.copy(alpha = 0.85f),
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
                    color = Color.White,
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Rounded Linear Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .fillMaxHeight()
                        .background(gold),
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // All prayers visible in a single row (no scroll needed for 5 prayers)
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

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

/**
 * A single compact prayer chip showing its name and time, highlighted when it is the next prayer.
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
    val cardModifier = if (isNext) {
        modifier.border(
            width = 1.5.dp,
            color = gold,
            shape = RoundedCornerShape(14.dp),
        )
    } else {
        modifier
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isNext) {
                Color(0xFF0F3628)
            } else {
                Color.White.copy(alpha = 0.12f)
            },
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp)
                .graphicsLayer {
                    alpha = if (isPast && !isNext) 0.5f else 1.0f
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = getArabicPrayerName(prayer.name, isFriday),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = AmiriFont,
                ),
                color = if (isNext) gold else Color.White,
                textAlign = TextAlign.Center,
            )
            Text(
                text = NumberLocalization.localize(prayer.time.split(" ").firstOrNull().orEmpty()),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = AmiriFont,
                    fontWeight = FontWeight.Bold,
                ),
                color = if (isNext) Color.White else Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
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
    val pages = features.chunked(HOME_PAGE_SIZE)
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 2.dp),
            pageSpacing = 18.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(356.dp),
        ) { pageIndex ->
            FeaturePage(
                features = pages[pageIndex],
                onFeatureSelected = onFeatureSelected,
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        HomePagerIndicator(
            pageCount = pages.size,
            selectedPage = pagerState.currentPage,
        )
    }
}

@Composable
private fun FeaturePage(
    features: List<HomeFeature>,
    onFeatureSelected: (HomeFeatureKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        features.chunked(HOME_ROW_SIZE).forEach { rowFeatures ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                rowFeatures.forEach { feature ->
                    HomeFeatureCard(
                        feature = feature,
                        onClick = { onFeatureSelected(feature.key) },
                    )
                }
                repeat(HOME_ROW_SIZE - rowFeatures.size) {
                    Spacer(modifier = Modifier.size(104.dp))
                }
            }
        }
    }
}

@Composable
private fun HomePagerIndicator(
    pageCount: Int,
    selectedPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            Text(
                text = "•",
                color = if (index == selectedPage) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private const val HOME_ROW_SIZE = 3
private const val HOME_PAGE_SIZE = 9
