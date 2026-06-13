package com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.screen

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.QueueMusic
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AnasheedItem
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.state.AyatUiState
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.viewmodel.AyatViewModel
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun AyatSoundRoute(
    contentPadding: PaddingValues,
    topicId: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    // Stub
}

@Composable
fun AyatRoute(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: AyatViewModel = hiltViewModel(),
    onTopicSelected: (Int) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AyatScreen(
        state = state,
        contentPadding = contentPadding,
        modifier = modifier,
        onFavoriteToggled = { viewModel.toggleFavorite(it) }
    )
}

@Composable
fun AyatScreen(
    state: AyatUiState,
    contentPadding: PaddingValues,
    onFavoriteToggled: (String) -> Unit,
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

    // Filter list based on search and favorite options
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
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            if (state.isLoading) {
                LoadingView()
            } else if (state.errorMessage != null) {
                ErrorView(message = state.errorMessage)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                ) {
                    // Header Bar (Back, Search, Favorites Toggle)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        if (isSearchActive) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("بحث...", fontSize = 14.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        contentDescription = "Search",
                                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        searchQuery = ""
                                        isSearchActive = false
                                    }) {
                                        Icon(
                                            imageVector = Icons.Outlined.Close,
                                            contentDescription = "Close",
                                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                                    .height(52.dp),
                                shape = RoundedCornerShape(24.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)
                                )
                            )
                        } else {
                            Text(
                                text = "الأناشيد",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { showOnlyFavorites = !showOnlyFavorites }) {
                                Icon(
                                    imageVector = if (showOnlyFavorites) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Show Favorites Only",
                                    tint = if (showOnlyFavorites) Color.Red else MaterialTheme.colorScheme.onBackground
                                )
                            }
                            if (!isSearchActive) {
                                IconButton(onClick = { isSearchActive = true }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        contentDescription = "Search",
                                        tint = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Album Header Info
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Card(
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier
                                        .size(160.dp)
                                        .shadow(8.dp, RoundedCornerShape(20.dp)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.b7),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "الأناشيد الإسلامية",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "المجموعة الكاملة • ${filteredList.size} نشيد",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                                
                                Spacer(modifier = Modifier.height(24.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            if (filteredList.isNotEmpty()) {
                                                currentPlayingIndex = 0
                                                isPlayingAudio = true
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.PlayArrow,
                                                contentDescription = "Play",
                                                tint = MaterialTheme.colorScheme.background
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = "Play", color = MaterialTheme.colorScheme.background)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            if (filteredList.isNotEmpty()) {
                                                currentPlayingIndex = kotlin.random.Random.nextInt(filteredList.size)
                                                isPlayingAudio = true
                                            }
                                        },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Shuffle,
                                                contentDescription = "Shuffle",
                                                tint = MaterialTheme.colorScheme.onBackground
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = "Shuffle", color = MaterialTheme.colorScheme.onBackground)
                                        }
                                    }
                                }
                            }
                        }

                        // Tracks List items
                        itemsIndexed(filteredList, key = { _, item -> item.title }) { index, item ->
                            val isItemPlaying = index == currentPlayingIndex && isPlayingAudio
                            val isFavorite = state.favoriteAnasheed.contains(item.title)
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isItemPlaying) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                                        else Color.Transparent
                                    )
                                    .clickable {
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
                                    }
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (isItemPlaying) {
                                    // Animated Audio Waves icon next to currently playing item
                                    AudioVisualizerBars(modifier = Modifier.size(20.dp))
                                } else {
                                    Text(
                                        text = String.format("%02d", index + 1),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                                        modifier = Modifier.width(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = if (isItemPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "أنشودة إسلامية • ${item.duration}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                    )
                                }

                                IconButton(onClick = { onFavoriteToggled(item.title) }) {
                                    Icon(
                                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }

                        // Bottom Spacer for Mini Player
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }

        // Mini Player Bar (Docked at the bottom)
        if (currentPlayingIndex >= 0 && currentItem != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF1C1B1F)) // Premium dark base color
                    .clickable { isPlayerFullscreen = true }
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left image with fade
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(27.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.b7),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Gradient fade overlay on the image right-side to blend
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color(0xFF1C1B1F).copy(alpha = 0.6f),
                                            Color(0xFF1C1B1F)
                                        ),
                                        startX = 10f
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentItem.title,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "أنشودة إسلامية",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    IconButton(onClick = { onFavoriteToggled(currentItem.title) }) {
                        val isFavorite = state.favoriteAnasheed.contains(currentItem.title)
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color.Red else Color.White.copy(alpha = 0.6f)
                        )
                    }

                    IconButton(
                        onClick = {
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
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            imageVector = if (isPlayingAudio) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        // Fullscreen Player Overlay (Slide up from bottom)
        AnimatedVisibility(
            visible = isPlayerFullscreen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            if (currentItem != null) {
                val isFavorite = state.favoriteAnasheed.contains(currentItem.title)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Top navigation controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { isPlayerFullscreen = false }) {
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = "Collapse Screen",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Text(
                                text = "Now Playing",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Outlined.QueueMusic,
                                    contentDescription = "Queue list",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        // Large album art
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                shape = RoundedCornerShape(28.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier
                                    .fillMaxHeight(0.85f)
                                    .aspectRatio(1f)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    Image(
                                        painter = painterResource(id = R.drawable.b7),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.5f)
                                                    )
                                                )
                                            )
                                    )
                                }
                            }
                        }

                        // Info section (Title, Artist, and Favorite + More buttons)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { onFavoriteToggled(currentItem.title) }) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = currentItem.title,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "أنشودة إسلامية",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }

                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreHoriz,
                                    contentDescription = "Options",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Waveform Seek Slider
                        Column(modifier = Modifier.fillMaxWidth()) {
                            WaveformSeekbar(
                                positionMs = audioPositionMs,
                                durationMs = audioDurationMs,
                                onSeek = { newPosition ->
                                    if (isAudioPrepared && mediaPlayerInstance != null) {
                                        mediaPlayerInstance?.seekTo(newPosition)
                                        audioPositionMs = newPosition
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = String.format("%02d:%02d", (audioPositionMs / 1000) / 60, (audioPositionMs / 1000) % 60),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = String.format("%02d:%02d", (audioDurationMs / 1000) / 60, (audioDurationMs / 1000) % 60),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Bottom Music Actions buttons panel
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (filteredList.isNotEmpty()) {
                                        currentPlayingIndex = kotlin.random.Random.nextInt(filteredList.size)
                                        isPlayingAudio = true
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Shuffle,
                                    contentDescription = "Shuffle",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (filteredList.isNotEmpty()) {
                                        currentPlayingIndex = if (currentPlayingIndex - 1 < 0) filteredList.size - 1 else currentPlayingIndex - 1
                                        isPlayingAudio = true
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.SkipPrevious,
                                    contentDescription = "Previous",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            IconButton(
                                onClick = {
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
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onBackground)
                            ) {
                                Icon(
                                    imageVector = if (isPlayingAudio) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = MaterialTheme.colorScheme.background,
                                    modifier = Modifier.size(38.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (filteredList.isNotEmpty()) {
                                        currentPlayingIndex = (currentPlayingIndex + 1) % filteredList.size
                                        isPlayingAudio = true
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.SkipNext,
                                    contentDescription = "Next",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            IconButton(
                                onClick = { isRepeatEnabled = !isRepeatEnabled }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Repeat,
                                    contentDescription = "Repeat",
                                    tint = if (isRepeatEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AudioVisualizerBars(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "Visualizer")
    
    // Wave bar heights animations
    val barHeight1 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(animation = tween(450), repeatMode = RepeatMode.Reverse),
        label = "Bar1"
    )
    val barHeight2 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(animation = tween(350), repeatMode = RepeatMode.Reverse),
        label = "Bar2"
    )
    val barHeight3 by infiniteTransition.animateFloat(
        initialValue = 0.1f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(animation = tween(500), repeatMode = RepeatMode.Reverse),
        label = "Bar3"
    )
    val barHeight4 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(animation = tween(400), repeatMode = RepeatMode.Reverse),
        label = "Bar4"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val activeColor = MaterialTheme.colorScheme.primary
        Box(modifier = Modifier.weight(1f).fillMaxHeight(barHeight1).background(activeColor, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.weight(1f).fillMaxHeight(barHeight2).background(activeColor, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.weight(1f).fillMaxHeight(barHeight3).background(activeColor, RoundedCornerShape(1.dp)))
        Box(modifier = Modifier.weight(1f).fillMaxHeight(barHeight4).background(activeColor, RoundedCornerShape(1.dp)))
    }
}

@Composable
fun WaveformSeekbar(
    positionMs: Int,
    durationMs: Int,
    onSeek: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = MaterialTheme.colorScheme.onBackground
    val inactiveColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)

    // Fix a pre-defined array representing visual waveform amplitudes
    val barHeights = remember {
        floatArrayOf(
            0.15f, 0.2f, 0.25f, 0.3f, 0.32f, 0.28f, 0.35f, 0.42f, 0.48f, 0.5f,
            0.45f, 0.55f, 0.65f, 0.58f, 0.42f, 0.32f, 0.25f, 0.38f, 0.46f, 0.58f,
            0.72f, 0.85f, 0.95f, 1.0f, 0.92f, 0.78f, 0.65f, 0.55f, 0.48f, 0.35f,
            0.22f, 0.28f, 0.34f, 0.42f, 0.55f, 0.68f, 0.75f, 0.72f, 0.58f, 0.46f,
            0.32f, 0.22f, 0.18f, 0.24f, 0.3f, 0.38f, 0.48f, 0.54f, 0.48f, 0.35f,
            0.25f, 0.2f, 0.18f, 0.15f
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
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )
            }
        }
    }
}
