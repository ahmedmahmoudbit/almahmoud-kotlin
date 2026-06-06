package com.almahmoudApp.al_mahmoudapp.feature.qotof.presentation.screen

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.EmptyView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.model.QotofItem
import com.almahmoudApp.al_mahmoudapp.feature.qotof.presentation.viewmodel.QotofViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

@Composable
fun QotofRoute(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QotofViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier
            .fillMaxSize()
            .haze(state = hazeState),
        color = MaterialTheme.colorScheme.background,
    ) {
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> LoadingView()
                state.errorMessage != null -> ErrorView(message = state.errorMessage)
                state.filteredItems.isEmpty() -> EmptyView()
                else -> QotofContent(
                    contentPadding = contentPadding,
                    state = state,
                    onBack = onBack,
                    onQueryChange = viewModel::onQueryChange,
                    onItemSelected = viewModel::onItemSelected,
                    onDismissItem = viewModel::dismissSelectedItem,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QotofContent(
    contentPadding: PaddingValues,
    state: com.almahmoudApp.al_mahmoudapp.feature.qotof.presentation.state.QotofUiState,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onItemSelected: (QotofItem) -> Unit,
    onDismissItem: () -> Unit,
) {
    // Use partial-expand to 75% — but Material3 does not support fractional by default,
    // so we wrap content in a fixed-height wrapContentHeight and let it size naturally.
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var triggerParticles by remember { mutableStateOf(false) }

    LaunchedEffect(state.selectedItem) {
        triggerParticles = state.selectedItem != null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(vertical = 14.dp),
    ) {
        // Top bar (back + title only — no search)
        QotofTopBar(
            onBack = onBack,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Stacked pager — fills available space
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            val pagerState = rememberPagerState(pageCount = { state.filteredItems.size })

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                // peek next card by leaving horizontal padding
                contentPadding = PaddingValues(horizontal = 40.dp),
                // pre-compose nearby pages so they are visible as the peek
                beyondViewportPageCount = 3,
                pageSpacing = 12.dp,
            ) { page ->
                val item = state.filteredItems.getOrNull(page) ?: return@HorizontalPager

                val pageOffset = ((page - pagerState.currentPage) -
                        pagerState.currentPageOffsetFraction)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(100f - abs(pageOffset))
                        .graphicsLayer {
                            when {
                                pageOffset < 0f -> {
                                    // Swipe-away: slide off left with tilt + fade
                                    translationX = pageOffset * size.width * 1.05f
                                    rotationZ = pageOffset * 12f
                                    alpha = (1f + pageOffset * 2f).coerceIn(0f, 1f)
                                }
                                else -> {
                                    // Upcoming cards: keep inline, scale down, fade slightly
                                    val scale = (1f - pageOffset * 0.06f).coerceIn(0.82f, 1f)
                                    scaleX = scale
                                    scaleY = scale
                                    translationY = pageOffset * 18.dp.toPx()
                                    alpha = (1f - pageOffset * 0.3f).coerceIn(0.4f, 1f)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    QotofCard(
                        item = item,
                        onClick = { onItemSelected(item) },
                    )
                }
            }
        }

        // Swipe / tap hint
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "← اسحب للسؤال التالي · انقر لمعرفة الإجابة",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textAlign = TextAlign.Center,
        )
    }

    // ── Bottom Sheet: 75% height, scrollable answer ──────────────────────────
    state.selectedItem?.let { item ->
        ModalBottomSheet(
            onDismissRequest = onDismissItem,
            sheetState = bottomSheetState,
            dragHandle = {
                // Gradient glow handle
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(width = 44.dp, height = 4.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    Color(0xFFFFD700),
                                    Color(0xFF00E676),
                                )
                            ),
                            RoundedCornerShape(2.dp)
                        )
                )
            },
        ) {
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.75f)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(top = 20.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    // Encouragement header
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = "أحسنت القراءة! ✨",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E676),
                                ),
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = "« فَمَنْ يُرِدِ اللَّهُ بِهِ خَيْرًا يُفَقِّهْهُ فِي الدِّينِ »",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                                    fontStyle = FontStyle.Italic,
                                ),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    // Question
                    item {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    // Answer
                    item {
                        Text(
                            text = item.body,
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 30.sp),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start,
                        )
                    }

                    // Copy + Share actions
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CopyButton(
                                onClick = {
                                    val textToCopy = "السؤال: ${item.title}\n\nالإجابة:\n${item.body}"
                                    clipboardManager.setText(AnnotatedString(textToCopy))
                                }
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            IconButton(
                                onClick = {
                                    val shareText = "السؤال: ${item.title}\n\nالإجابة:\n${item.body}"
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, null))
                                },
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        CircleShape,
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }
                    }
                }

                // Particle burst overlay
                ParticleBurstEffect(
                    trigger = triggerParticles,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .align(Alignment.TopCenter),
                )
            }
        }
    }
}

@Composable
private fun QotofTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconButton(onClick = onBack) {
            Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
        }
        Text(
            text = stringResource(R.string.qotof_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun QotofCard(
    item: QotofItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp)
        ) {
            // Faint watermark question mark
            Text(
                text = "؟",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 160.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 8.dp, y = 16.dp),
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Hashtag row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("#قطوف", "#سؤال_وجواب", "#فتاوى").forEach { tag ->
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            ),
                        )
                    }
                }

                // Question text only
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 34.sp,
                        fontSize = 20.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun CopyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isCopied by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isCopied) 1.2f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "CopyScale",
    )

    IconButton(
        onClick = {
            onClick()
            isCopied = true
        },
        modifier = modifier
            .scale(scale)
            .background(
                if (isCopied) Color(0xFF00E676).copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                CircleShape,
            ),
    ) {
        Icon(
            imageVector = if (isCopied) Icons.Rounded.Check else Icons.Rounded.ContentCopy,
            contentDescription = null,
            tint = if (isCopied) Color(0xFF00E676) else MaterialTheme.colorScheme.onSurface,
        )
    }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(1500)
            isCopied = false
        }
    }
}

@Composable
private fun ParticleBurstEffect(
    trigger: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!trigger) return

    val particles = remember(trigger) {
        List(28) {
            val angle = Random.nextFloat() * 2 * Math.PI
            val speed = Random.nextFloat() * 5f + 2f
            Particle(
                x = 0.5f,
                y = 0.35f,
                vx = (Math.cos(angle) * speed).toFloat(),
                vy = (Math.sin(angle) * speed - 3.5f).toFloat(),
                color = if (Random.nextBoolean()) Color(0xFFFFD700) else Color(0xFF00E676),
                size = Random.nextFloat() * 9f + 4f,
            )
        }
    }

    val progress = remember(trigger) { Animatable(0f) }

    LaunchedEffect(trigger) {
        progress.animateTo(1f, animationSpec = tween(durationMillis = 1400))
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier) {
            val w = size.width
            val h = size.height
            val t = progress.value
            particles.forEach { p ->
                val cx = w * p.x + p.vx * t * 28f
                val cy = h * p.y + p.vy * t * 28f
                val alpha = (1f - t).coerceIn(0f, 1f)
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.size,
                    center = androidx.compose.ui.geometry.Offset(cx, cy),
                )
            }
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
)
