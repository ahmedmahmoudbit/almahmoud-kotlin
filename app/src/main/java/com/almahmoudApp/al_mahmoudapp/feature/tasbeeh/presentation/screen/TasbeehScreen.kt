package com.almahmoudApp.al_mahmoudapp.feature.tasbeeh.presentation.screen

import AmiriFont
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassCard
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
        onSetCustom = viewModel::setCustomDhikr,
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TasbeehScreen(
    state: TasbeehUiState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    onSetCustom: (String, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val hapticFeedback = LocalHapticFeedback.current

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

    val clickComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.click)
    )

    val targetCount = if (state.isCustom) state.customTarget else TasbeehDhikr.MilestoneStep

    var tapAnimKey by remember { mutableIntStateOf(0) }
    var showCustomDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.b4),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.6f),
        )

        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            Color.Black,
                        )
                    )
                )
                .fillMaxSize()
        ) {
            // المحتوى مع الـ padding الخاص بـ insets فقط
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    LiquidGlassCard(
                        onClick = { showCustomDialog = true },
                        modifier = Modifier.height(44.dp),
                        cornerRadius = 999.dp,
                        refraction = 0.55f,
                        frost = 8f,
                        dispersion = 0.20f,
                        glowAlpha = 0.70f,
                    ) {
                        Text(
                            text = "تخصيص",
                            fontFamily = AmiriFont,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    LiquidGlassCard(
                        onClick = {
                            onReset()
                            mediaPlayer?.stop()
                            mediaPlayer?.release()
                            mediaPlayer = null
                        },
                        modifier = Modifier.size(44.dp),
                        cornerRadius = 999.dp,
                        refraction = 0.55f,
                        frost = 8f,
                        dispersion = 0.20f,
                        glowAlpha = 0.70f,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "إعادة ضبط",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(80.dp))
                    Text(
                        text = state.currentDhikr,
                        fontFamily = AmiriFont,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 58.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "${state.count} / ${targetCount}",
                        fontFamily = AmiriFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        maxItemsInEachRow = 5
                    ) {
                        if (!state.isCustom) {
                            TasbeehDhikr.milestones.forEachIndexed { index, _ ->
                                val completed =
                                    state.count >= (index + 1) * TasbeehDhikr.MilestoneStep
                                val isCurrent = index == (state.count / TasbeehDhikr.MilestoneStep)
                                    .coerceAtMost(TasbeehDhikr.milestones.lastIndex)

                                LiquidGlassCard(
                                    onClick = {},
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(44.dp),
                                    cornerRadius = 999.dp,
                                    refraction = 0.55f,
                                    frost = 8f,
                                    dispersion = 0.20f,
                                    glowAlpha = 0.70f,
                                ) {
                                    if (completed) {
                                        Icon(
                                            imageVector = Icons.Filled.Favorite,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD700),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${index + 1}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = state.isFinished,
                        enter = fadeIn() + scaleIn()
                    ) {
                        Text(
                            text = state.congratulationMessage,
                            fontFamily = AmiriFont,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD700),
                            textAlign = TextAlign.Center,
                        )
                    }
                    if (!state.isFinished) {
                        var isPressed by remember { mutableStateOf(false) }
                        val buttonScale by animateFloatAsState(
                            targetValue = if (isPressed) 0.88f else 1.0f,
                            label = "buttonScale"
                        )

                        Box(
                            modifier = Modifier
                                .size(320.dp)
                                .scale(buttonScale)
                                .clickable(
                                    interactionSource = null,
                                    indication = null,
                                    onClick = {
                                        tapAnimKey++
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onIncrement()
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            key(tapAnimKey) {
                                LottieAnimation(
                                    composition = clickComposition,
                                    iterations = 1,
                                    modifier = Modifier.size(310.dp)
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(999.dp))
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
                                    imageVector = Icons.Rounded.Refresh,
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
            if (showCustomDialog) {
                CustomDhikrBottomSheet(
                    onDismiss = { showCustomDialog = false },
                    onConfirm = { dhikr, target ->
                        onSetCustom(dhikr, target)
                        showCustomDialog = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomDhikrBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: (dhikr: String, target: Int) -> Unit,
) {
    var dhikrText by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("33") }
    var isError by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "تخصيص الذكر",
                fontFamily = AmiriFont,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dhikrText,
                onValueChange = { dhikrText = it },
                label = { Text("الذكر", fontFamily = AmiriFont) },
                placeholder = { Text("أدخل الذكر", fontFamily = AmiriFont) },
                singleLine = false,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                )
            )

            OutlinedTextField(
                value = targetText,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        targetText = newValue
                        isError = false
                    }
                },
                label = { Text("عدد المرات", fontFamily = AmiriFont) },
                placeholder = { Text("33", fontFamily = AmiriFont) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = isError,
                supportingText = if (isError) {
                    { Text("أدخل رقم صحيح", fontFamily = AmiriFont) }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "إلغاء",
                        fontFamily = AmiriFont,
                        fontSize = 16.sp,
                    )
                }

                Button(
                    onClick = {
                        val target = targetText.toIntOrNull()
                        if (dhikrText.isNotBlank() && target != null && target > 0) {
                            onConfirm(dhikrText.trim(), target)
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        "تأكيد",
                        fontFamily = AmiriFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}
