package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.screen

import LiquidGlassCard
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items as listItems
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.AppButton
import com.almahmoudApp.al_mahmoudapp.core.ui.components.EmptyView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassDefaults
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerseDetails
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranReader
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranSurah
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.QuranTextUiState
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.QuranViewModel
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.QuranTextViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import java.text.DecimalFormat
import kotlin.math.max
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.graphics.Color
import androidx.core.text.HtmlCompat
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.heightIn
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.QuranVideoBackground
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.rememberQuranVideoPlayerState



@Composable
fun QuranActionRoute(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    surahNumber: Int,
    page: Int,
    surahName: String,
    onBack: () -> Unit,
    onRead: (Int, Int, String) -> Unit,
    onAudio: (Int, Int, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .haze(state = hazeState),
        color = MaterialTheme.colorScheme.background,
    ) {
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                QuranTopBar(title = surahName.ifBlank { stringResource(R.string.quran_title) }, onBack = onBack)
                Spacer(modifier = Modifier.height(16.dp))
                LiquidGlassCard(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    refraction = 0.55f,
                    frost = 8f,
                    dispersion = 0.35f,
                    glowAlpha = 0.55f,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp),
                        )
                        Text(
                            text = surahName,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = "${stringResource(R.string.quran_page)} $page",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                AppButton(
                    text = stringResource(R.string.quran_reading),
                    onClick = { onRead(surahNumber, page, surahName) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
                AppButton(
                    text = stringResource(R.string.quran_audio),
                    onClick = { onAudio(surahNumber, page, surahName) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun QuranReadersRoute(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    surahNumber: Int,
    page: Int,
    surahName: String,
    onBack: () -> Unit,
    onReaderSelected: (String, String, String, Int, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuranViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val readers = state.content?.readers.orEmpty()

    Surface(
        modifier = modifier
            .fillMaxSize()
            .haze(state = hazeState),
        color = MaterialTheme.colorScheme.background,
    ) {
        when {
            state.isLoading -> LoadingView()
            state.errorMessage != null -> ErrorView(message = state.errorMessage)
            readers.isEmpty() -> EmptyView()
            else -> LiquidHost(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    QuranTopBar(title = surahName.ifBlank { stringResource(R.string.quran_audio) }, onBack = onBack)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.quran_choose_reader),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                            gridItems(readers) { reader ->
                            QuranReaderCard(
                                reader = reader,
                                onClick = {
                                    onReaderSelected(
                                        reader.name,
                                        reader.imageUrl,
                                        reader.audioBaseUrl,
                                        page,
                                        surahName,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranTextRoute(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    surahNumber: Int,
    page: Int,
    surahName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuranTextViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var controlsVisible by rememberSaveable { mutableStateOf(true) }
    var isAutoScrolling by rememberSaveable { mutableStateOf(false) }
    var autoScrollSpeed by rememberSaveable { mutableStateOf(0f) }
    var fontSize by rememberSaveable { mutableStateOf(28f) }
    var activeTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Surface(
        modifier = modifier
            .fillMaxSize()
            .haze(state = hazeState),
        color = MaterialTheme.colorScheme.background,
    ) {
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                QuranMushafBackground()

                when {
                    state.isLoading -> LoadingView()
                    state.errorMessage != null -> Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ErrorView(message = state.errorMessage)
                        Spacer(modifier = Modifier.height(16.dp))
                        AppButton(
                            text = stringResource(R.string.quran_retry),
                            onClick = viewModel::retry,
                        )
                    }
                    state.verses.isEmpty() -> EmptyView()
                    else -> {
                        LaunchedEffect(isAutoScrolling, autoScrollSpeed) {
                            if (!isAutoScrolling || autoScrollSpeed <= 0f) return@LaunchedEffect
                            while (isAutoScrolling) {
                                if (!listState.canScrollForward) {
                                    isAutoScrolling = false
                                    autoScrollSpeed = 0f
                                    break
                                }
                                listState.scrollBy(autoScrollSpeed)
                                delay(16)
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(contentPadding),
                        ) {
                            QuranTextControlsBar(
                                visible = controlsVisible,
                                fontSize = fontSize,
                                isAutoScrolling = isAutoScrolling,
                                onBack = onBack,
                                onToggleControls = { controlsVisible = !controlsVisible },
                                onIncreaseFont = { fontSize = (fontSize + 2f).coerceAtMost(40f) },
                                onDecreaseFont = { fontSize = (fontSize - 2f).coerceAtLeast(20f) },
                                onAutoScroll = { speed ->
                                    if (isAutoScrolling && autoScrollSpeed == speed) {
                                        isAutoScrolling = false
                                        autoScrollSpeed = 0f
                                    } else {
                                        isAutoScrolling = true
                                        autoScrollSpeed = speed
                                    }
                                },
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            QuranMushafHeader(
                                surahName = surahName,
                                page = page,
                                verseCount = state.verses.size,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LazyColumn(
                                state = listState,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 24.dp),
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                val verseItems = state.verses.filterNot { surahNumber == 1 && it.verseNumber == 1 }
                                if (surahNumber != 9) {
                                    item {
                                        QuranBasmalaCard(modifier = Modifier.fillMaxWidth())
                                    }
                                }
                                listItems(
                                    items = verseItems,
                                    key = { verse -> "${verse.surahNumber}_${verse.verseNumber}" },
                                ) { verse ->
                                    QuranVerseRow(
                                        verse = verse,
                                        fontSize = fontSize,
                                        surahName = surahName,
                                        onClick = {
                                            controlsVisible = false
                                            viewModel.onVerseSelected(verse)
                                        },
                                    )
                                }
                            }
                        }

                        if (state.selectedVerse != null) {
                            ModalBottomSheet(
                                onDismissRequest = viewModel::dismissVerseDetails,
                                sheetState = bottomSheetState,
                            ) {
                                QuranVerseDetailsSheet(
                                    state = state,
                                    activeTabIndex = activeTabIndex,
                                    onTabSelected = { activeTabIndex = it },
                                )
                            }
                        }

                        if (!controlsVisible) {
                            FloatingActionButton(
                                onClick = { controlsVisible = true },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                }
            }
    }
}
}

@Composable
private fun QuranMushafBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    )
}

@Composable
private fun QuranTextControlsBar(
    visible: Boolean,
    fontSize: Float,
    isAutoScrolling: Boolean,
    onBack: () -> Unit,
    onToggleControls: () -> Unit,
    onIncreaseFont: () -> Unit,
    onDecreaseFont: () -> Unit,
    onAutoScroll: (Float) -> Unit,
) {
    AnimatedVisibility(visible = visible) {
        LiquidGlassCard(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            refraction = 0.55f,
            frost = 8f,
            dispersion = 0.35f,
            glowAlpha = 0.55f,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                    Text(
                        text = stringResource(R.string.quran_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                    IconButton(onClick = onToggleControls) {
                        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = { onAutoScroll(1f) }) { Text("1x") }
                    TextButton(onClick = { onAutoScroll(1.25f) }) { Text("1.25x") }
                    TextButton(onClick = { onAutoScroll(1.5f) }) { Text("1.5x") }
                    TextButton(onClick = { onAutoScroll(2f) }) { Text("2x") }
                    Text(
                        text = if (isAutoScrolling) "Auto" else "Stop",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDecreaseFont) { Text("A-") }
                    Text(
                        text = "${fontSize.toInt()}",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    TextButton(onClick = onIncreaseFont) { Text("A+") }
                }
            }
        }
    }
}

@Composable
private fun QuranMushafHeader(
    surahName: String,
    page: Int,
    verseCount: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = surahName,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.quran_page) + " " + page,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.quran_verses_count, verseCount),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun QuranBasmalaCard(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(14.dp),
            )
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.quran_basmala),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun QuranVerseRow(
    verse: QuranVerse,
    fontSize: Float,
    surahName: String,
    onClick: () -> Unit,
) {
    val verseText = remember(verse.content, verse.verseNumber) {
        "${verse.content} ${verseEndSymbol(verse.verseNumber)}"
    }

    Text(
        text = verseText,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Medium,
            fontSize = fontSize.sp,
        ),
        textAlign = TextAlign.Center,
        lineHeight = (fontSize + 8f).sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuranVerseDetailsSheet(
    state: QuranTextUiState,
    activeTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    val selectedVerse = state.selectedVerse ?: return
    val details = state.selectedVerseDetails

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "سورة ${selectedVerse.surahNumber} - الآية ${selectedVerse.verseNumber}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        ScrollableTabRow(
            selectedTabIndex = activeTabIndex,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTabIndex]),
                    color = MaterialTheme.colorScheme.primary,
                )
            },
        ) {
            Tab(
                selected = activeTabIndex == 0,
                onClick = { onTabSelected(0) },
                text = { Text("التفسير") },
            )
            Tab(
                selected = activeTabIndex == 1,
                onClick = { onTabSelected(1) },
                text = { Text("المعاني") },
            )
        }

        when {
            state.isVerseDetailsLoading -> LoadingView(modifier = Modifier.fillMaxWidth())
            details == null -> state.verseDetailsErrorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            activeTabIndex == 0 -> Text(
                text = stripHtml(details.tafseerText),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
            )
            else -> Text(
                text = stripHtml(details.maanyText),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuranReaderCard(
    reader: QuranReader,
    onClick: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    LiquidGlassCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        refraction = 0.55f,
        frost = 8f,
        dispersion = 0.35f,
        glowAlpha = 0.55f,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        ) {
            coil3.compose.SubcomposeAsyncImage(
                model = coil3.request.ImageRequest.Builder(context)
                    .data(reader.imageUrl)
                    .diskCachePolicy(coil3.request.CachePolicy.ENABLED)
                    .memoryCachePolicy(coil3.request.CachePolicy.ENABLED)
                    .crossfade(true)
                    .build(),
                contentDescription = reader.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.75f),
                            )
                        )
                    ),
            )
            Text(
                text = reader.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 8.dp, vertical = 10.dp),
            )
        }
    }
}

@Composable
private fun QuranTopBar(
    title: String,
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.size(48.dp))
    }
}

private fun arabicToDecimal(number: String): String {
    val chars = CharArray(number.length)
    for (index in number.indices) {
        val ch = number[index]
        chars[index] =
            when {
                ch in '\u0660'..'\u0669' -> (ch.code - '\u0660'.code + '0'.code).toChar()
                ch in '\u06f0'..'\u06f9' -> (ch.code - '\u06f0'.code + '0'.code).toChar()
                else -> ch
            }
    }
    return String(chars)
}

private fun Int.toArabicNumerals(): String {
    return buildString {
        forEachDigit(this@toArabicNumerals) { append(it) }
    }
}

private inline fun forEachDigit(number: Int, appendDigit: (Char) -> Unit) {
    number.toString().forEach { digit ->
        appendDigit(
            when (digit) {
                '0' -> '٠'
                '1' -> '١'
                '2' -> '٢'
                '3' -> '٣'
                '4' -> '٤'
                '5' -> '٥'
                '6' -> '٦'
                '7' -> '٧'
                '8' -> '٨'
                '9' -> '٩'
                else -> digit
            }
        )
    }
}

private fun verseEndSymbol(verseNumber: Int): String {
    return "(${verseNumber.toArabicNumerals()})"
}

private fun stripHtml(text: String): String {
    return HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
}
