package com.almahmoudApp.al_mahmoudapp.feature.azkar.presentation.screen

import AmiriFont
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassCard
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.AzkarCategory
import kotlinx.coroutines.delay

// ─── Color Palette ──────────────────────────────────────────────────────────
private val AzkarBgTop    = Color(0xFF0B1A10)
private val AzkarBgMid    = Color(0xFF0D2418)
private val AzkarBgBot    = Color(0xFF0A1A0E)
private val AzkarGoldDim  = Color(0xFFB89B4E)
private val AzkarGoldGlow = Color(0xFFD4C06A)
private val AzkarWhite    = Color(0xFFF0F6EC)

// ─── Category card border colors ───────────────────────────────────────────
private val categoryBorderColors = listOf(
    Color(0xFF4CAF50),
    Color(0xFF2196F3),  // أزرق
    Color(0xFF9C27B0),  // بنفسجي
    Color(0xFFFF9800),  // برتقالي
    Color(0xFF00BCD4),  // سماوي
    Color(0xFFE91E63),  // وردي
    Color(0xFF3F51B5),  // نيلي
)

private val categoryEmojis = listOf("🌅", "🌙", "🤲", "🏠", "🚿", "🕊️", "😴")

// ─── Route ──────────────────────────────────────────────────────────────────
@Composable
fun AzkarListRoute(
    contentPadding: PaddingValues,
    onCategorySelected: (AzkarCategory) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AzkarListScreen(
        contentPadding = contentPadding,
        onCategorySelected = onCategorySelected,
        onBack = onBack,
        modifier = modifier,
    )
}

// ─── Screen ─────────────────────────────────────────────────────────────────
@Composable
fun AzkarListScreen(
    contentPadding: PaddingValues,
    onCategorySelected: (AzkarCategory) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val categories = AzkarCategory.entries

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
                // Top Bar
                AzkarTopBar(onBack = onBack)
                Spacer(modifier = Modifier.height(8.dp))

                // Category list
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    itemsIndexed(categories) { index, category ->
                        AnimatedCategoryCard(
                            index = index,
                            category = category,
                            emoji = categoryEmojis.getOrElse(index) { "📿" },
                            borderColor = categoryBorderColors[index % categoryBorderColors.size],
                            onClick = { onCategorySelected(category) },
                        )
                    }
                }
            }
        }
    }
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────
@Composable
private fun AzkarTopBar(onBack: () -> Unit) {
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
                tint = AzkarGoldGlow,
                modifier = Modifier.size(22.dp),
            )
        }

        Text(
            text = "الأذكار",
            style = TextStyle(
                fontFamily = AmiriFont,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AzkarGoldGlow,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

// ─── Animated Category Card ──────────────────────────────────────────────────
@Composable
private fun AnimatedCategoryCard(
    index: Int,
    category: AzkarCategory,
    emoji: String,
    borderColor: Color,
    onClick: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 80L)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        ),
    ) {
        CategoryCard(
            category = category,
            emoji = emoji,
            borderColor = borderColor,
            onClick = onClick,
        )
    }
}

@Composable
private fun CategoryCard(
    category: AzkarCategory,
    emoji: String,
    borderColor: Color,
    onClick: () -> Unit,
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "card_scale",
    )

    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(shape)
            .background(borderColor.copy(alpha = 0.20f), shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Chevron arrow (RTL: shown on the left = start)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = borderColor.copy(alpha = 0.55f),
                modifier = Modifier.size(24.dp),
            )

            // Category name
            Text(
                text = category.displayName,
                style = TextStyle(
                    fontFamily = AmiriFont,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = borderColor,
                ),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f),
            )

            // Emoji icon
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = emoji,
                fontSize = 28.sp,
            )
        }
    }
}
