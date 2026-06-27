package com.almahmoudApp.al_mahmoudapp.feature.tasbeeh.presentation.screen

import AmiriFont
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassCard
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.liquidSource
import com.almahmoudApp.al_mahmoudapp.feature.tasbeeh.domain.model.TasbeehDhikr
import com.almahmoudApp.al_mahmoudapp.feature.tasbeeh.presentation.state.TasbeehUiState
import com.almahmoudApp.al_mahmoudapp.feature.tasbeeh.presentation.viewmodel.TasbeehViewModel

@Composable
fun TasbeehRoute(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TasbeehViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TasbeehScreen(
        state = state,
        contentPadding = contentPadding,
        onBack = onBack,
        onIncrement = viewModel::incrementCount,
        onReset = viewModel::resetCount,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TasbeehScreen(
    state: TasbeehUiState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

    // Congratulations audio playback
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, R.raw.almahmoud32).apply {
                start()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    // Load Lottie compositions
    val clickComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.click)
    )
    val progressComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.progress)
    )
    val flowerComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.flower)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "السبحة الإلكترونية",
                        fontFamily = AmiriFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        onReset()
                        mediaPlayer?.stop()
                        mediaPlayer?.release()
                        mediaPlayer = null
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "إعادة ضبط"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.padding(contentPadding)
    ) { innerPadding ->
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .liquidSource()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Liquid Glass Dhikr Card
                    LiquidGlassCard(
                        onClick = {},
                        cornerRadius = 24.dp,
                        refraction = 0.35f,
                        frost = 10f,
                        dispersion = 0.25f,
                        glowAlpha = 0.4f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = state.currentDhikr,
                                fontFamily = AmiriFont,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                lineHeight = 42.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "التقدم: ${state.count} / ${TasbeehDhikr.MaxCount}",
                                fontFamily = AmiriFont,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Milestone Progress Indicators
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "مؤشرات الإنجاز",
                            fontFamily = AmiriFont,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            maxItemsInEachRow = 5
                        ) {
                            repeat(10) { index ->
                                val milestoneCompleted = state.count >= (index + 1) * TasbeehDhikr.MilestoneStep
                                
                                Box(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (milestoneCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (milestoneCompleted) {
                                        LottieAnimation(
                                            composition = progressComposition,
                                            iterations = LottieConstants.IterateForever,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Congratulation Card
                    AnimatedVisibility(visible = state.isFinished) {
                        LiquidGlassCard(
                            onClick = {},
                            cornerRadius = 16.dp,
                            refraction = 0.2f,
                            frost = 6f,
                            dispersion = 0.1f,
                            glowAlpha = 0.3f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                LottieAnimation(
                                    composition = flowerComposition,
                                    iterations = LottieConstants.IterateForever,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    text = state.congratulationMessage,
                                    fontFamily = AmiriFont,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                LottieAnimation(
                                    composition = flowerComposition,
                                    iterations = LottieConstants.IterateForever,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }

                    // Main Click Target
                    if (!state.isFinished) {
                        var isPressed by remember { mutableStateOf(false) }
                        val buttonScale by animateFloatAsState(targetValue = if (isPressed) 0.88f else 1.0f, label = "buttonScale")

                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .scale(buttonScale)
                                .clickable(
                                    interactionSource = null,
                                    indication = null,
                                    onClick = {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onIncrement()
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            LottieAnimation(
                                composition = clickComposition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier.size(230.dp)
                            )
                            Text(
                                text = "اضغط",
                                fontFamily = AmiriFont,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    } else {
                        // Reset Button at finish state
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .scale(1.0f)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                )
                                .clickable {
                                    onReset()
                                    mediaPlayer?.stop()
                                    mediaPlayer?.release()
                                    mediaPlayer = null
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "بدء من جديد",
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "بدء من جديد",
                                    fontFamily = AmiriFont,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
