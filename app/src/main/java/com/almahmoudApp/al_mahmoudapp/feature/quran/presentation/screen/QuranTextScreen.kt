package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.screen

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FontDownload
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.EmptyView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassCard
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading.ArabicTextUtils
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading.QuranContent
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading.QuranError
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading.QuranLoading
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading.QuranVerseDetailsSheet
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.QuranTextViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.delay
private const val SURAH_AT_TAWBAH = 9
private const val SURAH_AL_FATIHAH = 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranTextScreen(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    surahNumber: Int,
    page: Int,
    surahName: String,
    onBack: () -> Unit,
    onNavigateToNextSurah: (Int, Int, String) -> Unit,
    onNavigateToMushaf: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: QuranTextViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var controlsVisible by rememberSaveable { mutableStateOf(true) }
    var activeTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val fontSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollSpeedSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
    
    LaunchedEffect(state.currentAudioUrl) {
        val url = state.currentAudioUrl
        if (url != null && state.isAudioPlaying) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setAudioStreamType(AudioManager.STREAM_MUSIC)
                    setDataSource(url)
                    setOnPreparedListener { it.start() }
                    setOnCompletionListener {
                        viewModel.onAudioCompleted()
                    }
                    setOnErrorListener { _, _, _ ->
                        viewModel.onAudioCompleted()
                        true
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                viewModel.onAudioCompleted()
            }
        } else if (url == null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    LaunchedEffect(state.isAutoScrolling) {
        if (state.isAutoScrolling) {
            controlsVisible = false
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .haze(state = hazeState),
        color = MaterialTheme.colorScheme.background,
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
                when {
                    state.isLoading -> QuranLoading()
                    state.errorMessage != null -> QuranError(
                        message = state.errorMessage,
                        onRetry = viewModel::retry,
                    )
                    state.verses.isEmpty() -> EmptyView()
                    else -> ReadingBody(
                        contentPadding = contentPadding,
                        surahNumber = surahNumber,
                        surahName = surahName,
                        fontSize = state.fontSize.toFloat(),
                        verses = state.verses,
                        selectedVerse = state.selectedVerse,
                        listState = listState,
                        controlsVisible = controlsVisible && !state.isAutoScrolling,
                        isAutoScrolling = state.isAutoScrolling,
                        scrollSpeed = state.scrollSpeed,
                        onBack = onBack,
                        onOpenFontSizeSheet = viewModel::showFontSizeSheet,
                        onOpenScrollSpeedSheet = viewModel::showScrollSpeedSheet,
                        onStopAutoScroll = viewModel::stopAutoScroll,
                        onToggleControls = {
                            if (state.isAutoScrolling) {
                                viewModel.stopAutoScroll()
                            }
                            controlsVisible = !controlsVisible
                        },
                        onVerseSelected = { verse ->
                            controlsVisible = false
                            viewModel.onVerseSelected(verse)
                        },
                        onNextSurahClick = {
                            val nextSurahInfo = com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading.SurahPageMapping.getNextSurahInfo(surahNumber)
                            if (nextSurahInfo != null) {
                                val (nextNumber, nextPage, nextName) = nextSurahInfo
                                onNavigateToNextSurah(nextNumber, nextPage, nextName)
                            }
                        },
                        onNavigateToMushaf = onNavigateToMushaf,
                    )
                }

                if (state.selectedVerse != null) {
                    ModalBottomSheet(
                        onDismissRequest = viewModel::dismissVerseDetails,
                        sheetState = bottomSheetState,
                    ) {
                        QuranVerseDetailsSheet(
                            state = state,
                            surahName = surahName.ifBlank { stringResource(R.string.quran_title) },
                            activeTabIndex = activeTabIndex,
                            onTabSelected = { activeTabIndex = it },
                            onLoadAudio = {
                                viewModel.loadVerseAudio(surahNumber, state.selectedVerse?.verseNumber ?: 1)
                            },
                            onPlayAudio = { url, reciterName ->
                                viewModel.playAudio(url, reciterName)
                            },
                            onStopAudio = {
                                viewModel.stopAudio()
                            },
                        )
                    }
                }

                if (state.showFontSizeSheet) {
                    ModalBottomSheet(
                        onDismissRequest = viewModel::hideFontSizeSheet,
                        sheetState = fontSheetState,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    ) {
                        FontSizeBottomSheetContent(
                            currentSize = state.fontSize,
                            onIncrease = viewModel::increaseFontSize,
                            onDecrease = viewModel::decreaseFontSize,
                        )
                    }
                }

                if (state.showScrollSpeedSheet) {
                    ModalBottomSheet(
                        onDismissRequest = viewModel::hideScrollSpeedSheet,
                        sheetState = scrollSpeedSheetState,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    ) {
                        ScrollSpeedBottomSheetContent(
                            currentSpeed = state.scrollSpeed,
                            onSpeedSelected = viewModel::setScrollSpeed,
                            onStartAutoScroll = {
                                viewModel.hideScrollSpeedSheet()
                                viewModel.startAutoScroll()
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingBody(
    contentPadding: PaddingValues,
    surahNumber: Int,
    surahName: String,
    fontSize: Float,
    verses: List<com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse>,
    selectedVerse: com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse?,
    listState: androidx.compose.foundation.lazy.LazyListState,
    controlsVisible: Boolean,
    isAutoScrolling: Boolean,
    scrollSpeed: Float,
    onBack: () -> Unit,
    onOpenFontSizeSheet: () -> Unit,
    onOpenScrollSpeedSheet: () -> Unit,
    onStopAutoScroll: () -> Unit,
    onToggleControls: () -> Unit,
    onVerseSelected: (com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse) -> Unit,
    onNextSurahClick: () -> Unit,
    onNavigateToMushaf: ((Int) -> Unit)? = null,
) {
    // Search state
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var highlightedVerseNumber by remember { mutableStateOf(-1) }
    var isAutoHighlight by remember { mutableStateOf(false) }
    
    // Filter verses based on search
    val filteredVerses = remember(verses, searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            verses.filter { verse ->
                ArabicTextUtils.matchesSearch(verse.content, searchQuery)
            }
        }
    }

    // Auto-scroll to first found verse when typing
    LaunchedEffect(filteredVerses, searchQuery) {
        if (searchQuery.isNotBlank() && filteredVerses.isNotEmpty()) {
            val firstFound = filteredVerses.first()
            val originalIndex = verses.indexOfFirst { it.verseNumber == firstFound.verseNumber }
            if (originalIndex >= 0) {
                isAutoHighlight = true
                highlightedVerseNumber = firstFound.verseNumber
                listState.animateScrollToItem(index = originalIndex + 1)
            }
        } else {
            highlightedVerseNumber = -1
        }
    }

    // Auto-clear highlight after 2 seconds
    LaunchedEffect(highlightedVerseNumber) {
        if (highlightedVerseNumber > 0) {
            delay(2000)
            highlightedVerseNumber = -1
            isAutoHighlight = false
        }
    }

    // Clear highlight only on user-initiated scroll
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling && !isAutoHighlight) {
                    highlightedVerseNumber = -1
                    if (isAutoScrolling) {
                        onStopAutoScroll()
                    }
                }
            }
    }

    // Auto-scroll logic - smooth continuous scrolling
    LaunchedEffect(isAutoScrolling, scrollSpeed) {
        if (isAutoScrolling) {
            while (true) {
                delay(16)
                val scrollAmount = (2 * scrollSpeed).toInt().coerceAtLeast(1)
                listState.animateScrollBy(scrollAmount.toFloat())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        AnimatedVisibility(
            visible = controlsVisible,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
        ) {
            QuranReadingTopBar(
                onBack = onBack,
                onOpenFontSizeSheet = onOpenFontSizeSheet,
                onOpenScrollSpeedSheet = onOpenScrollSpeedSheet,
                isAutoScrolling = isAutoScrolling,
                onToggleSearch = { isSearchVisible = !isSearchVisible },
                onNavigateToMushaf = onNavigateToMushaf,
            )
        }
        
        // Search bar
        AnimatedVisibility(
            visible = isSearchVisible,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            VerseSearchBar(
                query = searchQuery,
                onQueryChange = { 
                    searchQuery = it
                },
                resultCount = filteredVerses.size,
                onClose = {
                    isSearchVisible = false
                    searchQuery = ""
                    highlightedVerseNumber = -1
                },
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        QuranContent(
            verses = verses.filterNot { surahNumber == SURAH_AL_FATIHAH && it.verseNumber == 1 },
            surahNumber = surahNumber,
            surahName = surahName,
            fontSize = fontSize,
            showBasmala = surahNumber != SURAH_AT_TAWBAH,
            selectedVerse = selectedVerse,
            highlightedVerseNumber = highlightedVerseNumber,
            listState = listState,
            onVerseSelected = onVerseSelected,
            onTap = onToggleControls,
            onNextSurahClick = onNextSurahClick,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun QuranReadingTopBar(
    onBack: () -> Unit,
    onOpenFontSizeSheet: () -> Unit,
    onOpenScrollSpeedSheet: () -> Unit,
    isAutoScrolling: Boolean,
    onToggleSearch: () -> Unit,
    onNavigateToMushaf: ((Int) -> Unit)? = null,
) {
    var showSettings by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        GlassCircleButton(
            onClick = onBack,
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(R.string.quran_back),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Search button
            GlassCircleButton(
                onClick = onToggleSearch,
                icon = Icons.Rounded.Search,
                contentDescription = "بحث",
            )
            
            // Settings button
            Box {
                GlassCircleButton(
                    onClick = { showSettings = !showSettings },
                    icon = Icons.Rounded.Tune,
                    contentDescription = stringResource(R.string.quran_options),
                )

                DropdownMenu(
                    expanded = showSettings,
                    onDismissRequest = { showSettings = false },
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0A1628).copy(alpha = 0.96f))
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(16.dp),
                        )
                        .width(210.dp),
                ) {
                    // Font size option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showSettings = false
                                onOpenFontSizeSheet()
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(R.string.quran_control_font),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Icon(
                            imageVector = Icons.Rounded.FontDownload,
                            contentDescription = null,
                            tint = Color(0xFF4DD0C4),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    // Divider line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(Color.White.copy(alpha = 0.10f))
                    )
                    // Auto scroll option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showSettings = false
                                onOpenScrollSpeedSheet()
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = stringResource(R.string.quran_auto_scroll),
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Icon(
                            imageVector = Icons.Rounded.Autorenew,
                            contentDescription = null,
                            tint = Color(0xFF4DD0C4),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    if (onNavigateToMushaf != null) {
                        // Divider line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.5.dp)
                                .background(Color.White.copy(alpha = 0.10f))
                        )
                        // Mushaf view option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showSettings = false
                                    onNavigateToMushaf(1)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "عرض المصحف",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Icon(
                                imageVector = Icons.Rounded.MenuBook,
                                contentDescription = null,
                                tint = Color(0xFF4DD0C4),
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassCircleButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
) {
    LiquidGlassCard(
        onClick = onClick,
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
                imageVector = icon,
                contentDescription = contentDescription,
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun FontSizeBottomSheetContent(
    currentSize: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    
    // Animation for size change
    val animatedSize by animateFloatAsState(
        targetValue = currentSize.toFloat(),
        animationSpec = tween(200),
        label = "font_size",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
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
        
        // Title
        Text(
            text = stringResource(R.string.quran_control_font),
            style = MaterialTheme.typography.titleMedium,
            color = onSurfaceColor,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Preview card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(surfaceVariantColor.copy(alpha = 0.5f))
                .padding(vertical = 20.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "بسم الله الرحمن الرحيم",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = animatedSize.sp,
                ),
                color = onSurfaceColor,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Font size display
        Text(
            text = "$currentSize",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = primaryColor,
        )
        
        Text(
            text = "نقطة",
            style = MaterialTheme.typography.bodySmall,
            color = onSurfaceColor.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Font size controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Decrease button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (currentSize > 22) primaryColor else primaryColor.copy(alpha = 0.3f),
                        shape = CircleShape,
                    )
                    .clickable(enabled = currentSize > 22) { onDecrease() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "A-",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = if (currentSize > 22) primaryColor else primaryColor.copy(alpha = 0.3f),
                )
            }

            // Increase button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (currentSize < 42) primaryColor else primaryColor.copy(alpha = 0.3f),
                        shape = CircleShape,
                    )
                    .clickable(enabled = currentSize < 42) { onIncrease() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "A+",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = if (currentSize < 42) primaryColor else primaryColor.copy(alpha = 0.3f),
                )
            }
        }
    }
}

@Composable
private fun ScrollSpeedBottomSheetContent(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    onStartAutoScroll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    
    val presetSpeeds = listOf(
        0.5f,
        1.0f,
        1.5f,
        2.0f,
        3.0f,
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Title
        Text(
            text = stringResource(R.string.quran_scroll_speed),
            style = MaterialTheme.typography.titleMedium,
            color = onSurfaceColor,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Speed options in horizontal row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            presetSpeeds.forEach { speed ->
                val isSelected = currentSpeed == speed
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) primaryColor else primaryColor.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .clickable { onSpeedSelected(speed) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${speed}x",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) primaryColor else onSurfaceColor.copy(alpha = 0.7f),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Custom speed slider
        Text(
            text = stringResource(R.string.quran_custom_speed),
            style = MaterialTheme.typography.bodySmall,
            color = onSurfaceColor.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Slider(
            value = currentSpeed,
            onValueChange = { onSpeedSelected(it) },
            valueRange = 0.1f..5.0f,
            steps = 49,
            colors = SliderDefaults.colors(
                thumbColor = primaryColor,
                activeTrackColor = primaryColor,
                inactiveTrackColor = primaryColor.copy(alpha = 0.2f),
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = "${String.format("%.1f", currentSpeed)}x",
            style = MaterialTheme.typography.bodyLarge,
            color = primaryColor,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Start button with border only
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.5.dp,
                    color = primaryColor,
                    shape = RoundedCornerShape(12.dp),
                )
                .clickable { onStartAutoScroll() }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.quran_start_scroll),
                style = MaterialTheme.typography.titleSmall,
                color = primaryColor,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun VerseSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    resultCount: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(surfaceVariantColor.copy(alpha = 0.5f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = primaryColor.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp),
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                decorationBox = { inner ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = "ابحث في الآيات...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            )
                        }
                        inner()
                    }
                },
                modifier = Modifier.weight(1f),
            )

            if (query.isNotEmpty()) {
                // Result count
                Text(
                    text = if (resultCount > 0) "$resultCount نتيجة" else "لا نتائج",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (resultCount > 0) primaryColor else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                )
            }

            // Close button
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable { onClose() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "إغلاق",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
