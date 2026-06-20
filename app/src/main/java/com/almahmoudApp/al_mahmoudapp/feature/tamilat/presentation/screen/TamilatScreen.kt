package com.almahmoudApp.al_mahmoudapp.feature.tamilat.presentation.screen

import AmiriFont
import android.content.Intent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
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
            .padding(contentPadding)
            .padding(vertical = 14.dp),
    ) {
        // Top bar
        TamilatTopBar(
            onBack = onBack,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Pager containing reflections
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            val pagerState = rememberPagerState(pageCount = { state.reflections.size })

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 48.dp),
                beyondViewportPageCount = 3,
                pageSpacing = 0.dp,
            ) { page ->
                val item = state.reflections.getOrNull(page) ?: return@HorizontalPager
                val pageOffset = ((page - pagerState.currentPage) - pagerState.currentPageOffsetFraction)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(100f - abs(pageOffset))
                        .graphicsLayer {
                            val isBehind = pageOffset > 0f
                            if (isBehind) {
                                // Stacked behind cards: translate horizontally to counteract horizontal pager layout
                                translationX = -pageOffset * size.width
                                
                                // Scale them down based on their depth in the stack
                                val scale = (1f - pageOffset * 0.05f).coerceIn(0.85f, 1f)
                                scaleX = scale
                                scaleY = scale
                                
                                // Translate downwards to show the stacked effect
                                translationY = pageOffset * 16.dp.toPx()
                                
                                // Fade out cards deeper in the stack
                                alpha = (1f - pageOffset * 0.35f).coerceIn(0f, 1f)
                            } else {
                                // The current card or cards swiped away (pageOffset <= 0)
                                // They slide out to the left/right and rotate slightly
                                translationX = pageOffset * size.width * 1.05f
                                rotationZ = pageOffset * 10f
                                alpha = (1f + pageOffset).coerceIn(0f, 1f)
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

        Spacer(modifier = Modifier.height(16.dp))
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
        IconButton(onClick = onBack) {
            Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
        }
        Text(
            text = stringResource(R.string.tamilat_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = AmiriFont),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun TamilatCard(
    item: TamilatItem,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (androidx.compose.foundation.isSystemInDarkTheme()) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
            } else {
                Color.White
            },
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // Large Watermark Quote Icon
            Icon(
                imageVector = Icons.Rounded.FormatQuote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.TopStart)
                    .offset(x = (-16).dp, y = (-24).dp)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Category Tags
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val tag = if (com.almahmoudApp.al_mahmoudapp.core.util.NumberLocalization.isArabic()) "#تأملات" else "#Reflections"
                    val tag2 = if (com.almahmoudApp.al_mahmoudapp.core.util.NumberLocalization.isArabic()) "#مواعظ" else "#Wisdom"
                    listOf(tag, tag2).forEach { text ->
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            ),
                        )
                    }
                }

                // Reflection Text
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.text,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Medium,
                            lineHeight = 38.sp,
                            fontFamily = AmiriFont
                        ),
                        color = if (androidx.compose.foundation.isSystemInDarkTheme()) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            Color(0xFF2E3A59)
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Bottom actions (Copy & Share)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CopyButton(onClick = onCopy)
                    Spacer(modifier = Modifier.size(16.dp))
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
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
            .size(54.dp)
            .scale(scale)
            .background(
                if (isCopied) Color(0xFF00E676).copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
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
