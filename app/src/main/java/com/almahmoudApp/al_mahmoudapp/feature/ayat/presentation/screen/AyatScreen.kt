package com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.screen

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.components.AyatAudioItemCard
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.components.AyatTopicCard
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.state.AyatSoundUiState
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.state.AyatUiState
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.viewmodel.AyatSoundViewModel
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.viewmodel.AyatViewModel
import kotlinx.coroutines.delay

@Composable
fun AyatRoute(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: AyatViewModel = hiltViewModel(),
    onTopicSelected: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AyatScreen(
        state = state,
        contentPadding = contentPadding,
        modifier = modifier,
        onTopicSelected = onTopicSelected,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyatScreen(
    state: AyatUiState,
    contentPadding: PaddingValues,
    onTopicSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.home_feature_ayat),
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                )
                Spacer(modifier = Modifier.height(8.dp))
                when {
                    state.isLoading -> LoadingView()
                    state.errorMessage != null -> ErrorView(message = state.errorMessage)
                    else -> LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        items(state.topics, key = { it.id }) { topic ->
                            AyatTopicCard(
                                topic = topic,
                                onClick = { onTopicSelected(topic.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AyatSoundRoute(
    contentPadding: PaddingValues,
    topicId: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: AyatSoundViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(topicId) {
        viewModel.load(topicId)
    }
    AyatSoundScreen(
        state = state,
        contentPadding = contentPadding,
        modifier = modifier,
        onBack = onBack,
    )
}

@Composable
fun AyatSoundScreen(
    state: AyatSoundUiState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> LoadingView()
                state.errorMessage != null -> ErrorView(message = state.errorMessage)
                state.topic == null -> ErrorView(message = stringResource(R.string.error_view_message))
                else -> AyatSoundContent(
                    state = state,
                    contentPadding = contentPadding,
                    onBack = onBack,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AyatSoundContent(
    state: AyatSoundUiState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
) {
    var currentIndex by rememberSaveable(state.topic?.id) { mutableIntStateOf(0) }
    var isPlaying by rememberSaveable(state.topic?.id) { mutableStateOf(false) }
    var isPrepared by rememberSaveable(state.topic?.id) { mutableStateOf(false) }
    var positionMs by rememberSaveable(state.topic?.id) { mutableIntStateOf(0) }
    var durationMs by rememberSaveable(state.topic?.id) { mutableIntStateOf(1) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(state.topic?.id) {
        onDispose {
            player?.release()
            player = null
        }
    }

    val currentItem = state.items.getOrNull(currentIndex)

    LaunchedEffect(currentItem?.url) {
        player?.release()
        player = null
        isPrepared = false
        positionMs = 0
        durationMs = 1
        currentItem?.let { item ->
            val mediaPlayer = MediaPlayer()
            player = mediaPlayer
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.setDataSource(item.url)
            mediaPlayer.setOnPreparedListener {
                isPrepared = true
                durationMs = it.duration.coerceAtLeast(1)
                if (isPlaying) {
                    it.start()
                }
            }
            mediaPlayer.setOnCompletionListener {
                isPlaying = false
                positionMs = 0
                val nextIndex = (currentIndex + 1) % state.items.size.coerceAtLeast(1)
                if (state.items.isNotEmpty()) {
                    currentIndex = nextIndex
                    isPlaying = true
                }
            }
            mediaPlayer.prepareAsync()
        }
    }

    LaunchedEffect(isPlaying, isPrepared) {
        while (isPlaying && isPrepared && player?.isPlaying == true) {
            positionMs = player?.currentPosition ?: 0
            durationMs = player?.duration?.coerceAtLeast(1) ?: 1
            delay(500)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        TopAppBar(
            title = {
                Text(
                    text = state.topic?.title.orEmpty(),
                    fontWeight = FontWeight.Bold,
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        )
        Spacer(modifier = Modifier.height(12.dp))
        state.topic?.let { topic ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.BottomCenter,
            ) {
                AsyncImage(
                    model = topic.backgroundUrl,
                    contentDescription = topic.title,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = topic.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${positionMs / 1000}s / ${durationMs / 1000}s",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.items, key = { item -> item.url }) { item ->
                    val isItemPlaying = currentItem?.url == item.url && isPlaying
                    AyatAudioItemCard(
                        item = item,
                        isPlaying = isItemPlaying,
                        onClick = {
                            val clickedIndex = state.items.indexOf(item)
                            if (clickedIndex >= 0) {
                                if (currentIndex == clickedIndex && isPrepared) {
                                    if (player?.isPlaying == true) {
                                        player?.pause()
                                        isPlaying = false
                                    } else {
                                        player?.start()
                                        isPlaying = true
                                    }
                                } else {
                                    currentIndex = clickedIndex
                                    isPlaying = true
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}
