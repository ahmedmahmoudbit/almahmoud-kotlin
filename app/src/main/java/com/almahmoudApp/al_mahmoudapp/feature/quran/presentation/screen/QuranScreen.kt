package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AudioFile
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TextSnippet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.EmptyView
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranSurah
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.QuranUiState
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.QuranViewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope

enum class FilterType {
    ALL, MAKKAH, MADINAH, FAVORITES
}

@Composable
fun QuranRoute(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    viewModel: QuranViewModel = hiltViewModel(),
    onNavigateToText: (Int, Int, String) -> Unit = { _, _, _ -> },
    onNavigateToAudio: (Int, Int, String) -> Unit = { _, _, _ -> },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    QuranScreen(
        state = state,
        contentPadding = contentPadding,
        modifier = modifier,
        onQueryChange = viewModel::onQueryChange,
        onFilterChange = viewModel::onFilterChange,
        onToggleFavorite = viewModel::toggleFavorite,
        onNavigateToText = onNavigateToText,
        onNavigateToAudio = onNavigateToAudio,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    state: QuranUiState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit,
    onFilterChange: (FilterType) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onNavigateToText: (Int, Int, String) -> Unit,
    onNavigateToAudio: (Int, Int, String) -> Unit,
) {
    var selectedSurah by remember { mutableStateOf<QuranSurah?>(null) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val tabs = listOf(
        FilterType.ALL to "الكل",
        FilterType.MAKKAH to "مكية",
        FilterType.MADINAH to "مدنية",
        FilterType.FAVORITES to "المفضلة",
    )

    // RTL layout
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding()),
        ) {
            // Header
            QuranHeader()

            // Search bar
            QuranSearchBar(
                query = state.query,
                onQueryChange = onQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            // Tab row
            val selectedTabIndex = tabs.indexOfFirst { it.first == state.selectedFilter }.coerceAtLeast(0)
            val coroutineScope = rememberCoroutineScope()
            val pagerState = rememberPagerState(initialPage = selectedTabIndex) { tabs.size }

            // Sync ViewModel selectedFilter with pager swipe transitions
            LaunchedEffect(pagerState.currentPage) {
                onFilterChange(tabs[pagerState.currentPage].first)
            }

            // Sync external filter changes back to pager
            LaunchedEffect(state.selectedFilter) {
                val filterIndex = tabs.indexOfFirst { it.first == state.selectedFilter }.coerceAtLeast(0)
                if (pagerState.currentPage != filterIndex) {
                    pagerState.animateScrollToPage(filterIndex)
                }
            }

            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
            ) {
                tabs.forEachIndexed { index, (type, label) ->
                    val isActive = pagerState.currentPage == index

                    Tab(
                        selected = isActive,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                ),
                            )
                        },
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                val filter = tabs[page].first
                val surahsForPage = remember(state.content, filter, state.query) {
                    val content = state.content ?: return@remember emptyList()
                    content.surahs.filter { surah ->
                        val matchesQuery = state.query.isEmpty() || 
                                surah.nameArabic.contains(state.query) || 
                                surah.nameEnglish.contains(state.query, ignoreCase = true) ||
                                surah.number.toString() == state.query

                        val matchesFilter = when (filter) {
                            FilterType.ALL -> true
                            FilterType.MAKKAH -> surah.revelationType.equals("Meccan", ignoreCase = true)
                            FilterType.MADINAH -> surah.revelationType.equals("Medinan", ignoreCase = true)
                            FilterType.FAVORITES -> surah.isFavorite
                        }

                        matchesQuery && matchesFilter
                    }
                }

                if (surahsForPage.isEmpty()) {
                    EmptyView()
                } else {
                    SurahList(
                        surahs = surahsForPage,
                        contentPadding = contentPadding,
                        onSurahClick = { selectedSurah = it },
                        onToggleFavorite = onToggleFavorite,
                    )
                }
            }
        }
    }

    // Bottom sheet for choosing mode
    if (selectedSurah != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedSurah = null },
            sheetState = bottomSheetState,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            SurahModeBottomSheet(
                surah = selectedSurah!!,
                onTextClick = {
                    val surah = selectedSurah!!
                    onNavigateToText(surah.number, surah.pageNumber, surah.nameArabic)
                    selectedSurah = null
                },
                onAudioClick = {
                    val surah = selectedSurah!!
                    onNavigateToAudio(surah.number, surah.pageNumber, surah.nameArabic)
                    selectedSurah = null
                },
            )
        }
    }
}

@Composable
private fun QuranHeader() {
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.1f))
                    .border(1.dp, primaryColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.quran_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.quran_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun QuranSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val hasText = query.isNotEmpty()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(surfaceVariantColor.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = primaryColor.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp),
        )

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            decorationBox = { inner ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    if (!hasText) {
                        Text(
                            text = stringResource(R.string.quran_search_surah),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        )
                    }
                    inner()
                }
            },
            modifier = Modifier.weight(1f),
        )

        AnimatedVisibility(
            visible = hasText,
            enter = fadeIn(tween(180)),
        ) {
            IconButton(
                onClick = { onQueryChange(""); focusManager.clearFocus() },
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun SurahList(
    surahs: List<QuranSurah>,
    contentPadding: PaddingValues,
    onSurahClick: (QuranSurah) -> Unit,
    onToggleFavorite: (Int) -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp,
        ),
        modifier = Modifier.fillMaxSize(),
    ) {
        itemsIndexed(
            items = surahs,
            key = { _, surah -> surah.number },
        ) { index, surah ->
            QuranSurahListItem(
                surah = surah,
                index = index,
                onClick = { onSurahClick(surah) },
                onToggleFavorite = { onToggleFavorite(surah.number) },
            )
            if (index < surahs.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f),
                )
            }
        }
    }
}

@Composable
private fun QuranSurahListItem(
    surah: QuranSurah,
    index: Int,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val animAlpha = remember { Animatable(0f) }
    val animOffset = remember { Animatable(12f) }

    LaunchedEffect(surah.number) {
        val delayMs = (index.coerceAtMost(15) * 20L)
        delay(delayMs)
        launch {
            animAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(200, easing = FastOutSlowInEasing),
            )
        }
        animOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(250, easing = FastOutSlowInEasing),
        )
    }

    val isMakki = surah.revelationType.contains("mak", ignoreCase = true) ||
            surah.revelationType.contains("مك", ignoreCase = true)
    val badgeColor = if (isMakki) Color(0xFFFF8A00) else Color(0xFF00BCD4)
    val badgeLabel = if (isMakki) "مكية" else "مدنية"
    val primaryColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = animAlpha.value
                translationY = animOffset.value
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.size(36.dp),
        ) {
            Icon(
                imageVector = if (surah.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = null,
                tint = if (surah.isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(20.dp),
            )
        }

        // Surah info (middle, aligned right in RTL)
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = surah.nameArabic,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
                maxLines = 1,
            )
            Spacer(Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "${surah.versesCount} آية",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
                Text(
                    text = "ص ${surah.pageNumber}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(badgeColor.copy(alpha = 0.1f))
                        .padding(horizontal = 5.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = badgeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = badgeColor,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(primaryColor.copy(alpha = 0.08f))
                .border(0.5.dp, primaryColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = surah.number.toString(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                ),
                color = primaryColor,
            )
        }
    }
}

@Composable
private fun SurahModeBottomSheet(
    surah: QuranSurah,
    onTextClick: () -> Unit,
    onAudioClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Handle bar
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(surfaceVariantColor)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Surah name
        Text(
            text = surah.nameArabic,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Surah info
        Text(
            text = "${surah.versesCount} آية • صفحة ${surah.pageNumber}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Text mode button
        ModeButton(
            icon = Icons.Rounded.TextSnippet,
            title = "قراءة نصية",
            subtitle = "قراءة القرآن الكريم بالنص",
            onClick = onTextClick,
            containerColor = primaryColor.copy(alpha = 0.1f),
            iconColor = primaryColor,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Audio mode button
        ModeButton(
            icon = Icons.Rounded.AudioFile,
            title = "استماع صوتي",
            subtitle = "الاستماع لتلاوة القرآن الكريم",
            onClick = onAudioClick,
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f),
            iconColor = Color(0xFF4CAF50),
        )
    }
}

@Composable
private fun ModeButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    containerColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }
    }
}
