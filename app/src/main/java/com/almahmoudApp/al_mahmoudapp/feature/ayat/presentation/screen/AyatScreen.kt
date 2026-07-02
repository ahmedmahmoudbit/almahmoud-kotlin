package com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.screen

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.components.AnasheedFullscreenPlayer
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.components.AnasheedListHeader
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.components.AnasheedMiniPlayer
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.components.AnasheedItemCard
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.state.AyatUiState
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.viewmodel.AyatViewModel
import kotlinx.coroutines.delay

@Composable
fun AyatSoundRoute(
    contentPadding: PaddingValues,
    topicId: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
}

@Composable
fun AyatRoute(
    contentPadding: PaddingValues,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AyatViewModel = hiltViewModel(),
    onTopicSelected: (Int) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AyatScreen(
        state = state,
        contentPadding = contentPadding,
        modifier = modifier,
        onBack = onBack,
        onFavoriteToggled = { viewModel.toggleFavorite(it) }
    )
}

@Composable
fun AyatScreen(
    state: AyatUiState,
    contentPadding: PaddingValues,
    onFavoriteToggled: (String) -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var isPlayingAudio by remember { mutableStateOf(false) }
    var isAudioPrepared by remember { mutableStateOf(false) }
    var audioPositionMs by remember { mutableIntStateOf(0) }
    var audioDurationMs by remember { mutableIntStateOf(1) }
    var currentPlayingIndex by remember { mutableIntStateOf(-1) }
    var mediaPlayerInstance by remember { mutableStateOf<MediaPlayer?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var isRepeatEnabled by remember { mutableStateOf(false) }
    var isPlayerFullscreen by remember { mutableStateOf(false) }

    val filteredList = remember(state.anasheed, state.favoriteAnasheed, searchQuery, showOnlyFavorites) {
        state.anasheed.filter { item ->
            val matchesSearch = item.title.contains(searchQuery, ignoreCase = true)
            val matchesFavorite = !showOnlyFavorites || state.favoriteAnasheed.contains(item.title)
            matchesSearch && matchesFavorite
        }
    }

    val currentItem = if (currentPlayingIndex in filteredList.indices) filteredList[currentPlayingIndex] else null

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayerInstance?.release()
            mediaPlayerInstance = null
        }
    }

    LaunchedEffect(currentPlayingIndex) {
        if (currentPlayingIndex in filteredList.indices) {
            mediaPlayerInstance?.release()
            mediaPlayerInstance = null
            isAudioPrepared = false
            audioPositionMs = 0
            audioDurationMs = 1

            val item = filteredList[currentPlayingIndex]
            val newPlayer = MediaPlayer()
            mediaPlayerInstance = newPlayer
            newPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            val descriptor = context.resources.openRawResourceFd(item.soundId)
            newPlayer.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
            descriptor.close()

            newPlayer.setOnPreparedListener {
                isAudioPrepared = true
                audioDurationMs = it.duration.coerceAtLeast(1)
                if (isPlayingAudio) {
                    it.start()
                }
            }

            newPlayer.setOnCompletionListener {
                isPlayingAudio = false
                audioPositionMs = 0
                if (isRepeatEnabled) {
                    isPlayingAudio = true
                    newPlayer.start()
                } else if (filteredList.isNotEmpty()) {
                    currentPlayingIndex = (currentPlayingIndex + 1) % filteredList.size
                    isPlayingAudio = true
                }
            }
            newPlayer.prepareAsync()
        }
    }

    LaunchedEffect(isPlayingAudio, isAudioPrepared) {
        while (isPlayingAudio && isAudioPrepared && mediaPlayerInstance?.isPlaying == true) {
            audioPositionMs = mediaPlayerInstance?.currentPosition ?: 0
            audioDurationMs = mediaPlayerInstance?.duration?.coerceAtLeast(1) ?: 1
            delay(500)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLoading) {
            LoadingView()
        } else if (state.errorMessage != null) {
            ErrorView(message = state.errorMessage)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    item {
                        AnasheedListHeader(
                            totalAnasheed = filteredList.size,
                            searchQuery = searchQuery,
                            isSearchActive = isSearchActive,
                            showOnlyFavorites = showOnlyFavorites,
                            onBackClick = onBack,
                            onSearchQueryChange = { searchQuery = it },
                            onSearchToggle = { isSearchActive = !isSearchActive },
                            onFavoriteFilterToggle = { showOnlyFavorites = !showOnlyFavorites },
                            onPlayAll = {
                                if (filteredList.isNotEmpty()) {
                                    currentPlayingIndex = 0
                                    isPlayingAudio = true
                                }
                            },
                            onShuffle = {
                                if (filteredList.isNotEmpty()) {
                                    currentPlayingIndex = kotlin.random.Random.nextInt(filteredList.size)
                                    isPlayingAudio = true
                                }
                            },
                        )
                    }

                    itemsIndexed(filteredList, key = { _, item -> item.title }) { index, item ->
                        val isItemPlaying = index == currentPlayingIndex && isPlayingAudio
                        val isFavorite = state.favoriteAnasheed.contains(item.title)

                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            AnasheedItemCard(
                                item = item,
                                isPlaying = isItemPlaying,
                                isFavorite = isFavorite,
                                onFavoriteClick = { onFavoriteToggled(item.title) },
                                onClick = {
                                    if (currentPlayingIndex == index && isAudioPrepared) {
                                        if (mediaPlayerInstance?.isPlaying == true) {
                                            mediaPlayerInstance?.pause()
                                            isPlayingAudio = false
                                        } else {
                                            mediaPlayerInstance?.start()
                                            isPlayingAudio = true
                                        }
                                    } else {
                                        currentPlayingIndex = index
                                        isPlayingAudio = true
                                    }
                                },
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        if (currentPlayingIndex >= 0 && currentItem != null) {
            val isFavorite = state.favoriteAnasheed.contains(currentItem.title)
            AnasheedMiniPlayer(
                item = currentItem,
                isPlaying = isPlayingAudio,
                isFavorite = isFavorite,
                audioPositionMs = audioPositionMs,
                audioDurationMs = audioDurationMs,
                onFavoriteToggle = { onFavoriteToggled(currentItem.title) },
                onPlayPauseClick = {
                    if (isAudioPrepared) {
                        if (mediaPlayerInstance?.isPlaying == true) {
                            mediaPlayerInstance?.pause()
                            isPlayingAudio = false
                        } else {
                            mediaPlayerInstance?.start()
                            isPlayingAudio = true
                        }
                    }
                },
                onMiniPlayerClick = { isPlayerFullscreen = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp),
            )
        }

        AnimatedVisibility(
            visible = isPlayerFullscreen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            if (currentItem != null) {
                val isFavorite = state.favoriteAnasheed.contains(currentItem.title)
                AnasheedFullscreenPlayer(
                    item = currentItem,
                    isPlaying = isPlayingAudio,
                    isFavorite = isFavorite,
                    isRepeatEnabled = isRepeatEnabled,
                    audioPositionMs = audioPositionMs,
                    audioDurationMs = audioDurationMs,
                    onFavoriteToggle = { onFavoriteToggled(currentItem.title) },
                    onPlayPauseClick = {
                        if (isAudioPrepared) {
                            if (mediaPlayerInstance?.isPlaying == true) {
                                mediaPlayerInstance?.pause()
                                isPlayingAudio = false
                            } else {
                                mediaPlayerInstance?.start()
                                isPlayingAudio = true
                            }
                        } else {
                            isPlayingAudio = !isPlayingAudio
                        }
                    },
                    onPreviousClick = {
                        if (filteredList.isNotEmpty()) {
                            currentPlayingIndex = if (currentPlayingIndex - 1 < 0) filteredList.size - 1 else currentPlayingIndex - 1
                            isPlayingAudio = true
                        }
                    },
                    onNextClick = {
                        if (filteredList.isNotEmpty()) {
                            currentPlayingIndex = (currentPlayingIndex + 1) % filteredList.size
                            isPlayingAudio = true
                        }
                    },
                    onShuffleClick = {
                        if (filteredList.isNotEmpty()) {
                            currentPlayingIndex = kotlin.random.Random.nextInt(filteredList.size)
                            isPlayingAudio = true
                        }
                    },
                    onRepeatClick = { isRepeatEnabled = !isRepeatEnabled },
                    onSeek = { newPosition ->
                        if (isAudioPrepared && mediaPlayerInstance != null) {
                            mediaPlayerInstance?.seekTo(newPosition)
                            audioPositionMs = newPosition
                        }
                    },
                    onCollapse = { isPlayerFullscreen = false },
                )
            }
        }
    }
}

@Composable
fun AudioVisualizerBars(modifier: Modifier = Modifier) {
}

@Composable
fun WaveformSeekbar(
    positionMs: Int,
    durationMs: Int,
    onSeek: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)

    val barHeights = remember {
        floatArrayOf(
            0.15f, 0.2f, 0.25f, 0.3f, 0.32f, 0.28f, 0.35f, 0.42f, 0.48f, 0.5f,
            0.45f, 0.55f, 0.65f, 0.58f, 0.42f, 0.32f, 0.25f, 0.38f, 0.46f, 0.58f,
            0.72f, 0.85f, 0.95f, 1.0f, 0.92f, 0.78f, 0.65f, 0.55f, 0.48f, 0.35f,
            0.22f, 0.28f, 0.34f, 0.42f, 0.55f, 0.68f, 0.75f, 0.72f, 0.58f, 0.46f,
            0.32f, 0.22f, 0.18f, 0.24f, 0.3f, 0.38f, 0.48f, 0.54f, 0.48f, 0.35f,
            0.25f, 0.2f, 0.18f, 0.15f,
        )
    }

    Box(
        modifier = modifier
            .pointerInput(durationMs) {
                detectTapGestures { offset ->
                    val percentage = (offset.x / size.width).coerceIn(0f, 1f)
                    onSeek((percentage * durationMs).toInt())
                }
            }
            .pointerInput(durationMs) {
                detectDragGestures { change, _ ->
                    val percentage = (change.position.x / size.width).coerceIn(0f, 1f)
                    onSeek((percentage * durationMs).toInt())
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalWidth = size.width
            val height = size.height
            val numBars = barHeights.size
            val gap = 3.dp.toPx()
            val totalGapsWidth = gap * (numBars - 1)
            val barWidth = (totalWidth - totalGapsWidth) / numBars
            val progressPercent = if (durationMs > 0) positionMs.toFloat() / durationMs.toFloat() else 0f
            val activeBarsLimit = (progressPercent * numBars).toInt()

            for (i in 0 until numBars) {
                val amplitude = barHeights[i]
                val barHeight = height * amplitude
                val x = i * (barWidth + gap)
                val y = (height - barHeight) / 2f
                val color = if (i <= activeBarsLimit) activeColor else inactiveColor

                drawRoundRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
                )
            }
        }
    }
}
