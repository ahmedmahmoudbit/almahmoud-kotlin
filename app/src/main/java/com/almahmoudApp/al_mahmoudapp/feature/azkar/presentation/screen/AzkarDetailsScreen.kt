package com.almahmoudApp.al_mahmoudapp.feature.azkar.presentation.screen

import AmiriFont
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassCard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.AzkarCategory
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.ZikrItem
import com.almahmoudApp.al_mahmoudapp.feature.azkar.presentation.state.AzkarDetailsUiState
import com.almahmoudApp.al_mahmoudapp.feature.azkar.presentation.viewmodel.AzkarDetailsViewModel

// ─── Private palette ─────────────────────────────────────────────────────────
private val GoldAccent = Color(0xFFD4C06A)
private val GoldDim    = Color(0xFFB89B4E)
private val CardBg1    = Color(0xFF175C45)
private val CardBg2    = Color(0xFF0F3E2E)
private val CountBg    = Color(0xFF0D3328)
private val WhiteText  = Color(0xFFF2F8EE)
private val HintText   = Color(0xFFB0C9B0)

// ─── Route ────────────────────────────────────────────────────────────────────
@Composable
fun AzkarDetailsRoute(
    contentPadding: PaddingValues,
    categoryOrdinal: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AzkarDetailsViewModel = hiltViewModel(),
) {
    val category = AzkarCategory.entries.getOrNull(categoryOrdinal)
        ?: AzkarCategory.MORNING

    LaunchedEffect(category) {
        viewModel.loadCategory(category)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    AzkarDetailsScreen(
        state = state,
        contentPadding = contentPadding,
        onBack = onBack,
        modifier = modifier,
    )
}

// ─── Screen ──────────────────────────────────────────────────────────────────
@Composable
fun AzkarDetailsScreen(
    state: AzkarDetailsUiState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.background,
                        ),
                    ),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
            ) {
                AzkarDetailsTopBar(
                    title = state.category?.displayName ?: "الأذكار",
                    onBack = onBack,
                )

                when {
                    state.isLoading -> AzkarLoadingView()
                    state.errorMessage != null -> AzkarErrorView(state.errorMessage)
                    state.items.isEmpty() -> AzkarEmptyView()
                    else -> AzkarItemsList(items = state.items)
                }
            }
        }
    }
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────
@Composable
private fun AzkarDetailsTopBar(title: String, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        LiquidGlassCard(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(44.dp),
            cornerRadius = 999.dp,
            refraction = 0.35f,
            frost = 4f,
            dispersion = 0.1f,
            glowAlpha = 0.25f,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "رجوع",
                tint = GoldAccent,
                modifier = Modifier.size(22.dp),
            )
        }

        Text(
            text = title,
            style = TextStyle(
                fontFamily = AmiriFont,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

// ─── Items List ───────────────────────────────────────────────────────────────
@Composable
private fun AzkarItemsList(items: List<ZikrItem>) {
    LazyColumn(
        modifier = Modifier.navigationBarsPadding(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        itemsIndexed(items) { index, item ->
            AnimatedZikrCard(index = index, item = item)
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// ─── Animated Zikr Card ──────────────────────────────────────────────────────
@Composable
private fun AnimatedZikrCard(index: Int, item: ZikrItem) {
    var visible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        ),
    ) {
        ZikrCard(item = item)
    }
}

@Composable
private fun ZikrCard(item: ZikrItem) {
    val context = LocalContext.current
    // Tap counter: starts at the declared repetition count, ticks down on each tap
    val maxCount = item.count.trim().toIntOrNull() ?: 1
    var remaining by remember(item.text) { mutableIntStateOf(maxCount) }
    val done = remaining == 0

    val cardAlpha by animateFloatAsState(
        targetValue = if (done) 0.45f else 1f,
        animationSpec = tween(400),
        label = "card_alpha",
    )

    val cardShape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardAlpha.coerceAtLeast(0.95f)),
    ) {
        // ── Main text card ─────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(CardBg1, CardBg2),
                        start = Offset(0f, 0f),
                        end = Offset(700f, 350f),
                    ),
                ),
        ) {
            Text(
                text = item.text,
                style = TextStyle(
                    fontFamily = AmiriFont,
                    fontSize = 18.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Normal,
                    color = WhiteText,
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
            )
        }

        // ── Bottom bar: count + copy ──────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp))
                .background(CountBg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Copy button
            CopyButton(
                text = item.text,
                context = context,
                modifier = Modifier.padding(start = 12.dp, top = 8.dp, bottom = 8.dp),
            )

            Spacer(modifier = Modifier.weight(1f))

            // Count label
            Text(
                text = "التكرار :",
                style = TextStyle(
                    fontFamily = AmiriFont,
                    fontSize = 13.sp,
                    color = HintText,
                ),
                modifier = Modifier.padding(end = 8.dp),
            )

            // Tappable counter bubble
            CounterBubble(
                remaining = remaining,
                maxCount = maxCount,
                done = done,
                onClick = { if (!done) remaining-- },
                modifier = Modifier.padding(end = 14.dp, top = 8.dp, bottom = 8.dp),
            )
        }
    }
}

// ─── Counter Bubble ──────────────────────────────────────────────────────────
@Composable
private fun CounterBubble(
    remaining: Int,
    maxCount: Int,
    done: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (done) 1.15f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "bubble_scale",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(42.dp)
            .scale(scale),
    ) {
        LiquidGlassCard(
            onClick = onClick,
            modifier = Modifier.size(42.dp),
            cornerRadius = 999.dp,
            refraction = 0.25f,
            frost = 3f,
            dispersion = 0.08f,
            glowAlpha = if (done) 0.5f else 0.2f,
            tintColor = if (done) Color(0xFF2FA084) else Color.White,
        ) {
            AnimatedContent(
                targetState = if (done) "✓" else remaining.toString(),
                transitionSpec = {
                    fadeIn(tween(180)) togetherWith fadeOut(tween(120))
                },
                label = "count_anim",
            ) { value ->
                Text(
                    text = value,
                    style = TextStyle(
                        fontFamily = AmiriFont,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (done) Color.White else GoldAccent,
                    ),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// ─── Copy Button ─────────────────────────────────────────────────────────────
@Composable
private fun CopyButton(text: String, context: Context, modifier: Modifier = Modifier) {
    LiquidGlassCard(
        onClick = { copyToClipboard(context, text) },
        modifier = modifier.height(32.dp).defaultMinSize(minWidth = 48.dp),
        cornerRadius = 10.dp,
        refraction = 0.25f,
        frost = 3f,
        dispersion = 0.08f,
        glowAlpha = 0.2f,
        tintColor = Color.White,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = "نسخ",
                tint = GoldAccent,
                modifier = Modifier.size(14.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "نسخ",
                style = TextStyle(
                    fontFamily = AmiriFont,
                    fontSize = 12.sp,
                    color = GoldAccent,
                ),
            )
        }
    }
}

// ─── State Views ─────────────────────────────────────────────────────────────
@Composable
private fun AzkarLoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = GoldAccent)
    }
}

@Composable
private fun AzkarErrorView(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = TextStyle(fontFamily = AmiriFont, color = Color(0xFFE07070), fontSize = 16.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Composable
private fun AzkarEmptyView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "لا توجد أذكار",
            style = TextStyle(fontFamily = AmiriFont, color = HintText, fontSize = 18.sp),
        )
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────
private fun copyToClipboard(context: Context, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return
    cm.setPrimaryClip(ClipData.newPlainText("azkar", "$text\n\n — تطبيق المحمود ❤"))
    Toast.makeText(context, "تم النسخ ✓", Toast.LENGTH_SHORT).show()
}
