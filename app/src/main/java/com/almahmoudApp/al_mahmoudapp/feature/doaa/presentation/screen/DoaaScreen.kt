package com.example.almahmoud.doaa

import AmiriFont
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.feature.doaa.presentation.state.DoaaUiState
import com.almahmoudApp.al_mahmoudapp.feature.doaa.presentation.viewmodel.DoaaViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

private val GoldLight = Color(0xFFD4C882)
private val GoldDim = Color(0xFF8A7A3E)
private val CardText = Color(0xFFECF5DC)
private val BgDeep = Color(0xFF0B1A2E)
private val BgMid = Color(0xFF0D2040)
private val BgGreen = Color(0xFF0A2E1A)

private val cardGradients = listOf (
    listOf(Color(0xFF0D2A18), Color(0xFF123322)),
    listOf(Color(0xFF0D1F2E), Color(0xFF102438)),
    listOf(Color(0xFF1E1228), Color(0xFF251535)),
    listOf(Color(0xFF1A2010), Color(0xFF1E2612)),
    listOf(Color(0xFF1A0E14), Color(0xFF221018)),
    listOf(Color(0xFF0E1E2A), Color(0xFF102535)),
    listOf(Color(0xFF0A2018), Color(0xFF0D2A1E)),
    listOf(Color(0xFF1C1408), Color(0xFF24190A)),
    listOf(Color(0xFF100D20), Color(0xFF150F28)),
    listOf(Color(0xFF0D2220), Color(0xFF102A26)),
)

@Composable
fun DoaaRoute(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: DoaaViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DoaaScreen(state = state, contentPadding = contentPadding, modifier = modifier)
}

@Composable
fun DoaaScreen(
    state: DoaaUiState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(BgDeep, BgMid, BgGreen),
                    start = Offset(0f, 0f),
                    end = Offset(400f, 900f),
                )
            )
            .padding(contentPadding),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(22.dp))

            // Bismillah
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                style = TextStyle(
                    fontFamily = AmiriFont,
                    fontSize = 15.sp,
                    color = GoldLight.copy(alpha = 0.72f),
                ),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(5.dp))

            // Title
            Text(
                text = "أدعية مأثورة",
                style = TextStyle(
                    fontFamily = AmiriFont,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEAF2DC),
                ),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))

            // Subtitle
            Text(
                text = "اسحب البطاقة • اضغط مطولاً للنسخ",
                style = TextStyle(
                    fontFamily = AmiriFont,
                    fontSize = 11.sp,
                    color = GoldDim.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Light,
                ),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))

            when {
                state.isLoading -> LoadingView()
                state.errorMessage != null -> ErrorView(message = state.errorMessage)
                else -> SwipeCardStack(
                    items = state.items,
                    onCopy = { copyToClipboard(context, it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
            }
        }
    }
}

@Composable
fun SwipeCardStack(
    items: List<String>,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var deck by remember(items) { mutableStateOf(items.shuffled()) }
    val scope = rememberCoroutineScope()

    fun onCardGone() {
        deck = deck.drop(1).ifEmpty { items.shuffled() }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (deck.isEmpty()) {
                EmptyView(onShuffle = { deck = items.shuffled() })
            } else {
                val visibleBack = minOf(deck.size, 3)
                for (i in visibleBack - 1 downTo 1) {
                    val scale = 1f - i * 0.045f
                    val transY = (i * 10).dp
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                min = 520.dp,
                                max = 520.dp
                            )
                            .zIndex((50 - i).toFloat())
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationY = transY.toPx()
                            }
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = cardGradients[(i) % cardGradients.size],
                                    start = Offset(0f, 0f),
                                    end = Offset(400f, 300f),
                                )
                            ),
                    )
                }

                key(deck.first()) {
                    DraggableCard(
                        text = deck.first(),
                        colorIndex = (items.size - deck.size),
                        position = items.size - deck.size + 1,
                        total = items.size,
                        onCopy = onCopy,
                        onSwiped = { onCardGone() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                min = 520.dp,
                                max = 520.dp
                            )
                            .zIndex(100f),
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        ProgressDots(
            total = items.size,
            current = items.size - deck.size,
        )
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleButton(
                onClick = { deck = deck.drop(1).ifEmpty { items.shuffled() } },
                icon = Icons.Default.ArrowForward,
                contentDescription = "التالي",
            )

            Text(
                text = "${items.size - deck.size + 1} / ${items.size}",
                style = TextStyle(
                    fontFamily = AmiriFont,
                    fontSize = 13.sp,
                    color = GoldDim.copy(alpha = 0.8f),
                ),
            )

            CircleButton(
                onClick = { deck = items.shuffled() },
                icon = Icons.Default.Refresh,
                contentDescription = "خلط",
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun DraggableCard(
    text: String,
    colorIndex: Int,
    position: Int,
    total: Int,
    onCopy: (String) -> Unit,
    onSwiped: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val offsetX = remember { Animatable(0f) }
    val rotation = remember { derivedStateOf { offsetX.value * 0.065f } }

    val colors = cardGradients[colorIndex % cardGradients.size]

    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = offsetX.value
                rotationZ = rotation.value
            }
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = colors,
                    start = Offset(0f, 0f),
                    end = Offset(400f, 300f),
                )
            ).combinedClickable(
                onClick = {},
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCopy(text)
                }
            ).pointerInput(text) {
                var longPressJob: kotlinx.coroutines.Job? = null
                var hasMoved = false

                detectDragGestures(
                    onDragStart = {
                        hasMoved = false
                        longPressJob = scope.launch {
                            delay(600)
                            if (!hasMoved) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onCopy(text)
                            }
                        }
                    },
                    onDrag = { change, amount ->
                        hasMoved = true
                        longPressJob?.cancel()
                        scope.launch { offsetX.snapTo(offsetX.value + amount.x) }
                        change.consume()
                    },
                    onDragEnd = {
                        longPressJob?.cancel()
                        if (abs(offsetX.value) > 90f) {
                            val dir = if (offsetX.value > 0f) 1 else -1
                            scope.launch {
                                offsetX.animateTo(
                                    targetValue = dir * 650f,
                                    animationSpec = tween(300, easing = FastOutSlowInEasing),
                                )
                                onSwiped()
                            }
                        } else {
                            scope.launch {
                                offsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium,
                                    ),
                                )
                            }
                        }
                    },
                    onDragCancel = {
                        longPressJob?.cancel()
                        scope.launch { offsetX.animateTo(0f, spring()) }
                    },
                )
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "$position / $total",
                style = TextStyle(
                    fontFamily = AmiriFont,
                    fontSize = 11.sp,
                    color = GoldDim.copy(alpha = 0.42f),
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = AmiriFont,
                    fontSize = 21.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.Normal,
                    color = CardText,
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .wrapContentHeight(),
            )
            Text(
                text = "← اسحب للتالي →",
                style = TextStyle(
                    fontFamily = AmiriFont,
                    fontSize = 11.sp,
                    color = GoldDim.copy(alpha = 0.32f),
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun ProgressDots(total: Int, current: Int) {
    val dots = minOf(total, 8)
    val step = maxOf(total / dots, 1)
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(dots) { i ->
            val active = i == (current / step).coerceAtMost(dots - 1)
            val width by animateDpAsState(if (active) 22.dp else 6.dp, label = "dot_$i")
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(width)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        if (active) Color(0xFFC3E178).copy(alpha = 0.85f)
                        else GoldDim.copy(alpha = 0.22f),
                    ),
            )
        }
    }
}

@Composable
fun CircleButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.06f)),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color(0xFFB9D773).copy(alpha = 0.65f),
        )
    }
}

@Composable
fun EmptyView(onShuffle: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("🌙", fontSize = 38.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "انتهت البطاقات",
            style = TextStyle(
                fontFamily = AmiriFont,
                fontSize = 18.sp,
                color = GoldDim.copy(alpha = 0.65f)
            ),
        )
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = onShuffle,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B3E1C)),
            shape = RoundedCornerShape(20.dp),
        ) {
            Text(
                text = "إعادة خلط البطاقات ↺",
                style = TextStyle(fontFamily = AmiriFont, color = GoldLight, fontSize = 14.sp),
            )
        }
    }
}

@Composable
fun LoadingView() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = GoldLight)
    }
}

@Composable
fun ErrorView(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = TextStyle(fontFamily = AmiriFont, color = Color(0xFFE07070), fontSize = 16.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp),
        )
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
    cm.setPrimaryClip(ClipData.newPlainText("doaa", "$text\n\n — تطبيق المحمود ❤"))
    Toast.makeText(context, "تم النسخ ✓", Toast.LENGTH_SHORT).show()
}