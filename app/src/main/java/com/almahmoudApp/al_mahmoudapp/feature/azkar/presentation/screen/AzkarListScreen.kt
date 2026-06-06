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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

// ─── Category card gradient pairs ───────────────────────────────────────────
private val categoryGradients = listOf(
    listOf(Color(0xFF1B4D2E), Color(0xFF0D2818)),
    listOf(Color(0xFF1A2B4A), Color(0xFF0D1A2E)),
    listOf(Color(0xFF2A1A3A), Color(0xFF170D22)),
    listOf(Color(0xFF2E2012), Color(0xFF1E1508)),
    listOf(Color(0xFF1E3020), Color(0xFF0E1C10)),
    listOf(Color(0xFF3A1A1A), Color(0xFF220E0E)),
    listOf(Color(0xFF1A2A3A), Color(0xFF0E1820)),
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
                        colors = listOf(AzkarBgTop, AzkarBgMid, AzkarBgBot),
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
                            gradient = categoryGradients[index % categoryGradients.size],
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
        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(44.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.08f)),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "رجوع",
                tint = AzkarGoldGlow,
            )
        }

        // Title
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
    gradient: List<Color>,
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
            gradient = gradient,
            onClick = onClick,
        )
    }
}

@Composable
private fun CategoryCard(
    category: AzkarCategory,
    emoji: String,
    gradient: List<Color>,
    onClick: () -> Unit,
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "card_scale",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = gradient,
                    start = Offset(0f, 0f),
                    end = Offset(600f, 250f),
                ),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    ) {
        // Inner border glow
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.04f)),
        )

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
                tint = AzkarGoldDim.copy(alpha = 0.55f),
                modifier = Modifier.size(24.dp),
            )

            // Category name
            Text(
                text = category.displayName,
                style = TextStyle(
                    fontFamily = AmiriFont,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzkarWhite,
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
