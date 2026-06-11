package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel

import LiquidGlassCard
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.EmptyView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.liquidSource
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranSurah
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.QuranUiState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuranRoute(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    viewModel: QuranViewModel = hiltViewModel(),
    onSurahSelected: (QuranSurah) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    QuranScreen(
        state = state,
        contentPadding = contentPadding,
        hazeState = hazeState,
        modifier = modifier,
        onQueryChange = viewModel::onQueryChange,
        onSurahSelected = onSurahSelected,
        onRetry = viewModel::retry,
    )
}

@Composable
fun QuranScreen(
    state: QuranUiState,
    contentPadding: PaddingValues,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit,
    onSurahSelected: (QuranSurah) -> Unit,
    onRetry: () -> Unit = {},
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .haze(state = hazeState),
        color = MaterialTheme.colorScheme.background,
    ) {
        when {
            state.isLoading            -> LoadingView()
            state.errorMessage != null -> ErrorView(message = state.errorMessage)
            else                       -> QuranContent(
                state = state,
                contentPadding = contentPadding,
                onQueryChange = onQueryChange,
                onSurahSelected = onSurahSelected,
            )
        }
    }
}

// ─────────────────────────────────────────────
// Content — الشبكة تتحكم بالسكرول الكامل
// ─────────────────────────────────────────────
@Composable
private fun QuranContent(
    state: QuranUiState,
    contentPadding: PaddingValues,
    onQueryChange: (String) -> Unit,
    onSurahSelected: (QuranSurah) -> Unit,
) {
    val revelationFilter = remember { mutableStateOf("all") }
    val gridState = rememberLazyGridState()

    val isScrolled by remember {
        derivedStateOf { gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 40 }
    }
    val headerAlpha by animateFloatAsState(
        targetValue = if (isScrolled) 0f else 1f,
        animationSpec = tween(300),
        label = "header_alpha",
    )
    val headerHeight by animateFloatAsState(
        targetValue = if (isScrolled) 0f else 1f,
        animationSpec = tween(350),
        label = "header_height",
    )

    val displayedSurahs = remember(state.filteredSurahs, revelationFilter.value) {
        when (revelationFilter.value) {
            "makki"  -> state.filteredSurahs.filter {
                it.revelationType.contains("mak", ignoreCase = true) ||
                        it.revelationType.contains("مك", ignoreCase = true)
            }
            "madani" -> state.filteredSurahs.filter {
                it.revelationType.contains("mad", ignoreCase = true) ||
                        it.revelationType.contains("مد", ignoreCase = true)
            }
            else     -> state.filteredSurahs
        }
    }

    LiquidHost(modifier = Modifier.fillMaxSize()) {
        QuranBackground()
        Image(
            painter = painterResource(id = R.drawable.b7),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .liquidSource()
                .alpha(0.4f),
        )
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 12.dp,
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding()),
        ) {

            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = headerAlpha
                            scaleY = 0.8f + headerHeight * 0.2f
                        },
                ) {
                    QuranHeader()
                    Spacer(Modifier.height(14.dp))
                }
            }

            // ── شريط البحث ──
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Column {
                    QuranSearchBar(
                        query = state.query,
                        onQueryChange = onQueryChange,
                    )
                    Spacer(Modifier.height(10.dp))
                    QuranRevelationTabs(
                        selected = revelationFilter.value,
                        onSelect = { revelationFilter.value = it },
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }

            // ── السور ──
            if (displayedSurahs.isEmpty()) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                    EmptyView()
                }
            } else {
                itemsIndexed(
                    items = displayedSurahs,
                    key = { _, surah -> surah.number },
                ) { index, surah ->
                    QuranSurahCard(
                        surah = surah,
                        index = index,
                        onClick = { onSurahSelected(surah) },
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Background
// ─────────────────────────────────────────────
@Composable
private fun QuranBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .liquidSource()
            .background(MaterialTheme.colorScheme.background),
    )
    val stars = remember {
        (1..55).map {
            Triple(
                kotlin.random.Random.nextFloat(),
                kotlin.random.Random.nextFloat() * 0.60f,
                kotlin.random.Random.nextFloat() * 0.40f + 0.08f,
            )
        }
    }
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .liquidSource(),
    ) {
        stars.forEach { (x, y, a) ->
            drawCircle(
                color = Color.White.copy(alpha = a),
                radius = 0.6.dp.toPx() + kotlin.random.Random.nextFloat() * 0.8.dp.toPx(),
                center = Offset(size.width * x, size.height * y * 0.55f),
            )
        }
    }
    AsyncImage(
        model = R.drawable.home_mosque_skyline,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .liquidSource()
            .alpha(0.12f),
    )
}

// ─────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────
@Composable
private fun QuranHeader() {
    val gold    = Color(0xFFFFD54F)
    val goldDim = Color(0xFFFFB300)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(goldDim.copy(alpha = 0.18f), Color.Transparent)
                        )
                    ),
            )
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(goldDim.copy(alpha = 0.12f))
                    .border(0.5.dp, goldDim.copy(alpha = 0.28f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.MenuBook,
                    contentDescription = null,
                    tint = gold,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.quran_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.3.sp,
            ),
            color = gold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = stringResource(R.string.quran_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.50f),
            textAlign = TextAlign.Center,
        )
    }
}

// ─────────────────────────────────────────────
// Search Bar
// ─────────────────────────────────────────────
@Composable
private fun QuranSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val gold    = Color(0xFFFFB300)
    val hasText = query.isNotEmpty()

    val borderAlpha by animateFloatAsState(
        targetValue = if (hasText) 0.45f else 0.12f,
        animationSpec = tween(300),
        label = "border_alpha",
    )

    LiquidGlassCard(
        onClick = {},
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        cornerRadius = 14.dp,
        frost = 10f,
        glowAlpha = if (hasText) 0.50f else 0.28f,
        refraction = 0.4f,
        dispersion = 0.2f,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .border(0.5.dp, gold.copy(alpha = borderAlpha), RoundedCornerShape(14.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = if (hasText) gold.copy(alpha = 0.8f)
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                modifier = Modifier.padding(start = 12.dp).size(16.dp),
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                decorationBox = { inner ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = if (hasText) 0.dp else 12.dp),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        if (!hasText) {
                            Text(
                                text = stringResource(R.string.quran_search_surah),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.30f),
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
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(gold.copy(alpha = 0.14f)),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        tint = gold.copy(alpha = 0.85f),
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Revelation Tabs
// ─────────────────────────────────────────────
@Composable
private fun QuranRevelationTabs(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = listOf("all" to "الكل", "makki" to "مكية", "madani" to "مدنية")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tabs.forEach { (key, label) ->
            val isActive  = selected == key
            val tabColor  = when (key) {
                "makki"  -> Color(0xFFFF8A00)
                "madani" -> Color(0xFF00BCD4)
                else     -> Color(0xFFFFB300)
            }
            val bgAlpha by animateFloatAsState(
                targetValue = if (isActive) 0.15f else 0f,
                animationSpec = tween(220),
                label = "tab_$key",
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tabColor.copy(alpha = bgAlpha))
                    .border(
                        0.5.dp,
                        tabColor.copy(alpha = if (isActive) 0.30f else 0.10f),
                        RoundedCornerShape(10.dp),
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) { onSelect(key) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp,
                    ),
                    color = if (isActive) tabColor
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.42f),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// Surah Card — أصغر + انيميشن مُصلح
// ─────────────────────────────────────────────
@Composable
private fun QuranSurahCard(
    surah: QuranSurah,
    index: Int,
    onClick: () -> Unit,
) {
    val animScale  = remember { Animatable(0.88f) }
    val animAlpha  = remember { Animatable(0f) }
    val animOffset = remember { Animatable(18f) }

    LaunchedEffect(surah.number) {
        val delayMs = (index.coerceAtMost(12) * 28L)
        delay(delayMs)
        launch {
            animAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(220, easing = FastOutSlowInEasing),
            )
        }
        launch {
            animScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(280, easing = FastOutSlowInEasing),
            )
        }
        animOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(280, easing = FastOutSlowInEasing),
        )
    }

    val featuredNumbers = setOf(1, 18, 36, 55, 56, 67, 112, 113, 114)
    val isFeatured  = surah.number in featuredNumbers
    val accentColor = if (isFeatured) Color(0xFFFFD54F) else Color(0xFFFFB300)

    val isMakki    = surah.revelationType.contains("mak", ignoreCase = true) ||
            surah.revelationType.contains("مك", ignoreCase = true)
    val badgeColor = if (isMakki) Color(0xFFFF8A00) else Color(0xFF00BCD4)
    val badgeLabel = surah.revelationType.ifBlank { if (isMakki) "مكية" else "مدنية" }

    Box(
        modifier = Modifier
            .graphicsLayer {
                alpha        = animAlpha.value
                scaleX       = animScale.value
                scaleY       = animScale.value
                translationY = animOffset.value
            },
    ) {
        LiquidGlassCard(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.55f),
            cornerRadius = 18.dp,
            frost        = if (isFeatured) 9f else 6f,
            glowAlpha    = if (isFeatured) 0.60f else 0.38f,
            refraction   = 0.48f,
            dispersion   = 0.25f,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.14f))
                            .border(0.5.dp, accentColor.copy(alpha = 0.22f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = surah.number.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                            ),
                            color = accentColor,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor.copy(alpha = 0.13f))
                            .border(0.5.dp, badgeColor.copy(alpha = 0.22f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = badgeLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                            color = badgeColor,
                        )
                    }
                }

                // ── اسم السورة ──
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                ) {
                    Text(
                        text = surah.nameArabic,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                        ),
                        color = Color.White.copy(alpha = 0.88f),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                    )
                    if (surah.nameEnglish.isNotBlank() && surah.nameEnglish != surah.nameArabic) {
                        Text(
                            text = surah.nameEnglish,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                            color = Color.White.copy(alpha = 0.32f),
                            textAlign = TextAlign.End,
                            maxLines = 1,
                        )
                    }
                }

                // ── آيات + صفحة ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "ص ${surah.pageNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        color = Color.White.copy(alpha = 0.22f),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.50f)),
                        )
                        Text(
                            text = "${surah.versesCount} ${stringResource(R.string.quran_verses)}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = Color.White.copy(alpha = 0.46f),
                        )
                    }
                }
            }
        }
    }
}