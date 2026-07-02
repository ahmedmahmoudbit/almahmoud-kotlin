package com.almahmoudApp.al_mahmoudapp.feature.tamilat.presentation.screen

import AmiriFont
import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassCard
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.feature.tamilat.domain.model.TamilatItem
import com.almahmoudApp.al_mahmoudapp.feature.tamilat.presentation.state.TamilatUiState
import com.almahmoudApp.al_mahmoudapp.feature.tamilat.presentation.viewmodel.TamilatViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun TamilatRoute(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TamilatViewModel = hiltViewModel(),
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
                state.reflections.isEmpty() -> EmptyView()
                else -> TamilatContent(
                    contentPadding = contentPadding,
                    state = state,
                    onBack = onBack,
                )
            }
        }
    }
}

@Composable
private fun TamilatContent(
    contentPadding: PaddingValues,
    state: TamilatUiState,
    onBack: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(contentPadding)
            .padding(vertical = 14.dp)
            .navigationBarsPadding(),
    ) {
        TamilatTopBar(
            onBack = onBack,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            val middlePage = state.reflections.size / 2
            val pagerState = rememberPagerState(
                initialPage = middlePage,
                pageCount = { state.reflections.size },
            )
            val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 3,
            ) { page ->
                val item = state.reflections.getOrNull(page) ?: return@HorizontalPager
                val pageOffset = ((page - pagerState.currentPage) - pagerState.currentPageOffsetFraction)
                val visualOffset = if (isRtl) -pageOffset else pageOffset
                val absOffset = abs(visualOffset)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(100f - absOffset)
                        .graphicsLayer {
                            if (visualOffset > 0f) {
                                translationX = -visualOffset * size.width
                                val scale = (1f - visualOffset * 0.08f).coerceIn(0.82f, 1f)
                                scaleX = scale
                                scaleY = scale
                                translationY = visualOffset * 20.dp.toPx()
                                alpha = (1f - visualOffset * 0.3f).coerceIn(0.4f, 1f)
                                rotationZ = 0f
                            } else {
                                translationX = visualOffset * size.width * 1.1f
                                rotationZ = visualOffset * 12f
                                alpha = (1f + visualOffset * 0.8f).coerceIn(0f, 1f)
                                scaleX = 1f
                                scaleY = 1f
                                translationY = 0f
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    TamilatCard(
                        item = item,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(item.text))
                        },
                        onShare = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, item.text)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, null))
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (com.almahmoudApp.al_mahmoudapp.core.util.NumberLocalization.isArabic())
                "← اسحب لتبديل البطاقات وقراءة المزيد من التأملات والمواعظ"
            else "← Swipe to switch cards and read more reflections and sermons",
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
}

@Composable
private fun TamilatTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        LiquidGlassCard(
            onClick = onBack,
            modifier = Modifier.size(44.dp),
            cornerRadius = 999.dp,
            refraction = 0.55f,
            frost = 8f,
            dispersion = 0.20f,
            glowAlpha = 0.70f,
        ) {
            val bgLuminance = MaterialTheme.colorScheme.background.let { 0.299f * it.red + 0.587f * it.green + 0.114f * it.blue }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                tint = if (bgLuminance < 0.5f) Color.White else Color.Black,
                modifier = Modifier.size(22.dp),
            )
        }
        Text(
            text = stringResource(R.string.tamilat_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = AmiriFont),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.size(44.dp))
    }
}

private val cardDrawables = listOf(
    R.drawable.card1, R.drawable.card2, R.drawable.card3, R.drawable.card4, R.drawable.card5,
    R.drawable.card6, R.drawable.card7, R.drawable.card8, R.drawable.card9,
    R.drawable.card10, R.drawable.card11, R.drawable.card12, R.drawable.card13,
    R.drawable.card14, R.drawable.card15,
)

@Composable
private fun TamilatCard(
    item: TamilatItem,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val drawableRes = cardDrawables[item.id % cardDrawables.size]

    Card(
        modifier = modifier
            .fillMaxWidth(0.78f)
            .fillMaxHeight(0.82f),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(28.dp))
        ) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = 0.55f },
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.3f),
                                Color.Black.copy(alpha = 0.55f),
                            ),
                        )
                    )
            )

            var selectedTag by remember { mutableIntStateOf(0) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val tags = if (com.almahmoudApp.al_mahmoudapp.core.util.NumberLocalization.isArabic()) {
                        listOf("#تأملات", "#مواعظ")
                    } else {
                        listOf("#Reflections", "#Wisdom")
                    }
                    tags.forEachIndexed { index, text ->
                        val isSelected = selectedTag == index
                        LiquidGlassCard(
                            onClick = { selectedTag = index },
                            modifier = Modifier.height(34.dp),
                            cornerRadius = 999.dp,
                            refraction = 0.55f,
                            frost = 8f,
                            dispersion = 0.20f,
                            glowAlpha = if (isSelected) 0.9f else 0.55f,
                        ) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                color = if (isSelected) Color(0xFFFFD700) else Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.padding(horizontal = 14.dp),
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FormatQuote,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Medium,
                                lineHeight = 42.sp,
                                fontFamily = AmiriFont,
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CopyButton(onClick = onCopy)
                    Spacer(modifier = Modifier.width(12.dp))
                    LiquidGlassCard(
                        onClick = onShare,
                        modifier = Modifier.size(50.dp),
                        cornerRadius = 999.dp,
                        refraction = 0.55f,
                        frost = 6f,
                        dispersion = 0.15f,
                        glowAlpha = 0.55f,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
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
        targetValue = if (isCopied) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.4f,
            stiffness = 300f,
        ),
        label = "CopyScale",
    )

    LiquidGlassCard(
        onClick = {
            onClick()
            isCopied = true
        },
        modifier = modifier
            .size(50.dp)
            .scale(scale),
        cornerRadius = 999.dp,
        refraction = 0.55f,
        frost = 6f,
        dispersion = 0.15f,
        glowAlpha = if (isCopied) 0.8f else 0.55f,
    ) {
        Icon(
            imageVector = if (isCopied) Icons.Rounded.Check else Icons.Rounded.ContentCopy,
            contentDescription = null,
            tint = if (isCopied) Color.Black else Color.White,
            modifier = Modifier.size(22.dp),
        )
    }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(1500)
            isCopied = false
        }
    }
}
