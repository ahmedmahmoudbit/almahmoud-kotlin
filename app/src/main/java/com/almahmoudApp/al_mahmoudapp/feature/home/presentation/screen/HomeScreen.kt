package com.almahmoudApp.al_mahmoudapp.feature.home.presentation.screen

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.EmptyView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.liquidSource
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeFeature
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeFeatureKey
import com.almahmoudApp.al_mahmoudapp.feature.home.presentation.components.HomeFeatureCard
import com.almahmoudApp.al_mahmoudapp.feature.home.presentation.state.HomeUiState
import com.almahmoudApp.al_mahmoudapp.feature.home.presentation.viewmodel.HomeViewModel
import coil3.compose.AsyncImage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import androidx.compose.ui.graphics.Color

@Composable
fun HomeRoute(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onFeatureSelected: (HomeFeatureKey) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
                .padding(horizontal = 18.dp, vertical = 18.dp),
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            QuoteTicker(quotes = state.quotes)
            Spacer(modifier = Modifier.height(24.dp))
            HomeFeaturePager(
                features = state.features,
                onFeatureSelected = onFeatureSelected,
                modifier = Modifier.fillMaxWidth(),
            )
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
