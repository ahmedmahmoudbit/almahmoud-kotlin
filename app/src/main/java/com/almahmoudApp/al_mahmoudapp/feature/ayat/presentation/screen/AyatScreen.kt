package com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.screen

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.liquidSource
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatAudioItem
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.components.AnasheedItemCard
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.components.AyatAudioItemCard
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.components.AyatTopicCard
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.state.AyatSoundUiState
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.state.AyatUiState
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.viewmodel.AyatSoundViewModel
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.viewmodel.AyatViewModel
import kotlinx.coroutines.delay
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import LiquidGlassCard
import androidx.lifecycle.viewmodel.compose.viewModel

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
        onTabSelected = { viewModel.selectTab(it) },
        onFilterSelected = { viewModel.setFilterType(it) },
        onFavoriteToggled = { viewModel.toggleFavorite(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyatScreen(
    state: AyatUiState,
    contentPadding: PaddingValues,
    onTopicSelected: (Int) -> Unit,
    onTabSelected: (Int) -> Unit,
    onFilterSelected: (Int) -> Unit,
    onFavoriteToggled: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var playingTitle by remember { mutableStateOf("") }
    var playingDuration by remember { mutableStateOf("") }
    var isPlayingAudio by remember { mutableStateOf(false) }
    var isAudioPrepared by remember { mutableStateOf(false) }
    var audioPositionMs by remember { mutableIntStateOf(0) }
    var audioDurationMs by remember { mutableIntStateOf(1) }
    var currentPlayingIndex by remember { mutableIntStateOf(-1) }
    var currentPlayingType by remember { mutableStateOf("") } // "anasheed" or "benefits"
    var mediaPlayerInstance by remember { mutableStateOf<MediaPlayer?>(null) }
    val lazyListState = rememberLazyListState()

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayerInstance?.release()
            mediaPlayerInstance = null
        }
    }

    LaunchedEffect(currentPlayingIndex, currentPlayingType) {
        if (currentPlayingIndex >= 0) {
            mediaPlayerInstance?.release()
            mediaPlayerInstance = null
            isAudioPrepared = false
            audioPositionMs = 0
            audioDurationMs = 1
            
            val newPlayer = MediaPlayer()
            mediaPlayerInstance = newPlayer
            newPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            
            if (currentPlayingType == "anasheed") {
                val filteredAnasheed = if (state.filterType == 1) {
                    state.anasheed.filter { state.favoriteAnasheed.contains(it.title) }
                } else {
                    state.anasheed
                }
                val item = filteredAnasheed.getOrNull(currentPlayingIndex)
                if (item != null) {
                    playingTitle = item.title
                    playingDuration = item.duration
                    val descriptor = context.resources.openRawResourceFd(item.soundId)
                    newPlayer.setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
                    descriptor.close()
                }
            } else if (currentPlayingType == "benefits") {
                val item = state.benefits.getOrNull(currentPlayingIndex)
                if (item != null) {
                    playingTitle = item.title
                    playingDuration = item.duration
                    newPlayer.setDataSource(item.url)
                }
            }
            
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
                val filteredAnasheed = if (state.filterType == 1) {
                    state.anasheed.filter { state.favoriteAnasheed.contains(it.title) }
                } else {
                    state.anasheed
                }
                val totalSize = if (currentPlayingType == "anasheed") filteredAnasheed.size else state.benefits.size
                if (totalSize > 0) {
                    currentPlayingIndex = (currentPlayingIndex + 1) % totalSize
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

    val visualizerComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loading_sound)
    )

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .liquidSource()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                MaterialTheme.colorScheme.background,
                            ),
                        ),
                    ),
            )
            Image(
                painter = painterResource(id = R.drawable.b7),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .liquidSource()
                    .alpha(0.4f),
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(64.dp)
                        .padding(vertical = 12.dp)
                        .padding(start = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LiquidGlassCard(
                        onClick = {},
                        modifier = Modifier.fillMaxHeight().width(52.dp),
                        cornerRadius = 24.dp,
                        refraction = 0.5f,
                        frost = 10f,
                        dispersion = 0.3f,
                        glowAlpha = 0.6f,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            SidebarTabItem(
                                text = "أناشيد",
                                isSelected = state.selectedTab == 0,
                                onClick = { onTabSelected(0) }
                            )
                            SidebarTabItem(
                                text = "آيات",
                                isSelected = state.selectedTab == 1,
                                onClick = { onTabSelected(1) }
                            )
                            SidebarTabItem(
                                text = "نصائح",
                                isSelected = state.selectedTab == 2,
                                onClick = { onTabSelected(2) }
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    val titleText = when (state.selectedTab) {
                        0 -> "الأناشيد الإسلامية"
                        1 -> stringResource(R.string.home_feature_sound)
                        else -> "نصائح إسلامية"
                    }
                    var showFilterMenu by remember { mutableStateOf(false) }
                    val filteredAnasheedForRandom = remember(state.anasheed, state.favoriteAnasheed, state.filterType) {
                        if (state.filterType == 1) {
                            state.anasheed.filter { state.favoriteAnasheed.contains(it.title) }
                        } else {
                            state.anasheed
                        }
                    }
                    TopAppBar(
                        title = {
                            Text(
                                text = titleText,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        },
                        actions = {
                            if (state.selectedTab == 0) {
                                // Filter Menu Button
                                Box {
                                    IconButton(
                                        onClick = { showFilterMenu = true },
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        LiquidGlassCard(
                                            onClick = { showFilterMenu = true },
                                            modifier = Modifier.size(36.dp),
                                            cornerRadius = 18.dp,
                                            refraction = 0.4f,
                                            frost = 6f,
                                            dispersion = 0.2f,
                                            glowAlpha = 0.5f,
                                        ) {
                                            Icon(
                                                imageVector = if (state.filterType == 1) Icons.Filled.Favorite else Icons.Outlined.FilterList,
                                                contentDescription = "Filter",
                                                tint = if (state.filterType == 1) Color.Red else Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    
                                    DropdownMenu(
                                        expanded = showFilterMenu,
                                        onDismissRequest = { showFilterMenu = false },
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("الكل") },
                                            onClick = {
                                                onFilterSelected(0)
                                                showFilterMenu = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("المفضلة") },
                                            onClick = {
                                                onFilterSelected(1)
                                                showFilterMenu = false
                                            }
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                // Auto-Play (Randomize) Button
                                IconButton(
                                    onClick = {
                                        if (filteredAnasheedForRandom.isNotEmpty()) {
                                            currentPlayingIndex = kotlin.random.Random.nextInt(filteredAnasheedForRandom.size)
                                            currentPlayingType = "anasheed"
                                            isPlayingAudio = true
                                        }
                                    },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    LiquidGlassCard(
                                        onClick = {
                                            if (filteredAnasheedForRandom.isNotEmpty()) {
                                                currentPlayingIndex = kotlin.random.Random.nextInt(filteredAnasheedForRandom.size)
                                                currentPlayingType = "anasheed"
                                                isPlayingAudio = true
                                            }
                                        },
                                        modifier = Modifier.size(36.dp),
                                        cornerRadius = 18.dp,
                                        refraction = 0.4f,
                                        frost = 6f,
                                        dispersion = 0.2f,
                                        glowAlpha = 0.5f,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Shuffle,
                                            contentDescription = "Auto Play",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            when {
                                state.isLoading -> LoadingView()
                                state.errorMessage != null -> ErrorView(message = state.errorMessage)
                                else -> {
                                    when (state.selectedTab) {
                                        0 -> { 
                                            val filteredAnasheed = remember(state.anasheed, state.favoriteAnasheed, state.filterType) {
                                                if (state.filterType == 1) {
                                                    state.anasheed.filter { state.favoriteAnasheed.contains(it.title) }
                                                } else {
                                                    state.anasheed
                                                }
                                            }
                                            if (filteredAnasheed.isEmpty()) {
                                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = if (state.filterType == 1) "لا توجد أناشيد في المفضلة" else "لا توجد أناشيد متاحة",
                                                        color = Color.White.copy(alpha = 0.6f),
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                }
                                            } else {
                                                LazyColumn(
                                                    state = lazyListState,
                                                    contentPadding = PaddingValues(bottom = if (currentPlayingIndex >= 0) 100.dp else 16.dp),
                                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                                    modifier = Modifier.fillMaxSize()
                                                ) {
                                                    items(filteredAnasheed.size, key = { index -> filteredAnasheed[index].title }) { index ->
                                                        val item = filteredAnasheed[index]
                                                        val isItemPlaying = currentPlayingIndex == index && currentPlayingType == "anasheed" && isPlayingAudio
                                                        val isFavorite = state.favoriteAnasheed.contains(item.title)
                                                        AnasheedItemCard(
                                                            item = item,
                                                            isPlaying = isItemPlaying,
                                                            isFavorite = isFavorite,
                                                            onFavoriteClick = { onFavoriteToggled(item.title) },
                                                            onClick = {
                                                                if (currentPlayingIndex == index && currentPlayingType == "anasheed" && isAudioPrepared) {
                                                                    if (mediaPlayerInstance?.isPlaying == true) {
                                                                        mediaPlayerInstance?.pause()
                                                                        isPlayingAudio = false
                                                                    } else {
                                                                        mediaPlayerInstance?.start()
                                                                        isPlayingAudio = true
                                                                    }
                                                                } else {
                                                                    currentPlayingIndex = index
                                                                    currentPlayingType = "anasheed"
                                                                    isPlayingAudio = true
                                                                }
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        1 -> { 
                                            LazyColumn(
                                                contentPadding = PaddingValues(bottom = if (currentPlayingIndex >= 0) 100.dp else 16.dp),
                                                verticalArrangement = Arrangement.spacedBy(14.dp),
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                items(state.topics, key = { it.id }) { topic ->
                                                    AyatTopicCard(
                                                        topic = topic,
                                                        onClick = { onTopicSelected(topic.id) },
                                                    )
                                                }
                                            }
                                        }
                                        2 -> { 
                                            LazyColumn(
                                                state = lazyListState,
                                                contentPadding = PaddingValues(bottom = if (currentPlayingIndex >= 0) 100.dp else 16.dp),
                                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                                modifier = Modifier.fillMaxSize()
                                            ) {
                                                items(state.benefits.size, key = { index -> state.benefits[index].url }) { index ->
                                                    val item = state.benefits[index]
                                                    val isItemPlaying = currentPlayingIndex == index && currentPlayingType == "benefits" && isPlayingAudio
                                                    AyatAudioItemCard(
                                                        item = item,
                                                        isPlaying = isItemPlaying,
                                                        isCached = false,
                                                        onClick = {
                                                            if (currentPlayingIndex == index && currentPlayingType == "benefits" && isAudioPrepared) {
                                                                if (mediaPlayerInstance?.isPlaying == true) {
                                                                    mediaPlayerInstance?.pause()
                                                                    isPlayingAudio = false
                                                                } else {
                                                                    mediaPlayerInstance?.start()
                                                                    isPlayingAudio = true
                                                                }
                                                            } else {
                                                                currentPlayingIndex = index
                                                                currentPlayingType = "benefits"
                                                                isPlayingAudio = true
                                                            }
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom Scrim Fade Overlay
                        if (currentPlayingIndex >= 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                                MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                                                MaterialTheme.colorScheme.background
                                            )
                                        )
                                    )
                            )
                        }

                        // Floating Glass Bottom Player Card
                        if (currentPlayingIndex >= 0) {
                            val filteredAnasheed = remember(state.anasheed, state.favoriteAnasheed, state.filterType) {
                                if (state.filterType == 1) {
                                    state.anasheed.filter { state.favoriteAnasheed.contains(it.title) }
                                } else {
                                    state.anasheed
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(horizontal = 4.dp, vertical = 8.dp)
                            ) {
                                LiquidGlassCard(
                                    onClick = {},
                                    modifier = Modifier.fillMaxWidth().height(80.dp),
                                    cornerRadius = 20.dp,
                                    refraction = 0.45f,
                                    frost = 10f,
                                    dispersion = 0.25f,
                                    glowAlpha = 0.5f,
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Circular Play/Pause + Progress
                                        Box(
                                            modifier = Modifier.size(52.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                progress = if (audioDurationMs > 0) audioPositionMs.toFloat() / audioDurationMs.toFloat() else 0f,
                                                modifier = Modifier.fillMaxSize(),
                                                color = MaterialTheme.colorScheme.primary,
                                                strokeWidth = 2.5.dp,
                                                trackColor = Color.White.copy(alpha = 0.15f),
                                            )
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
                                                    .size(42.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primary),
                                            ) {
                                                Icon(
                                                    imageVector = if (isPlayingAudio) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                        }

                                        // Title and Subtitle
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = playingTitle,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = if (currentPlayingType == "anasheed") "أنشودة إسلامية" else "نصيحة إسلامية",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.55f)
                                            )
                                        }

                                        // Controls: Skip Previous, Skip Next, Favorite
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    val totalSize = if (currentPlayingType == "anasheed") filteredAnasheed.size else state.benefits.size
                                                    if (totalSize > 0) {
                                                        val prevIndex = if (currentPlayingIndex - 1 < 0) totalSize - 1 else currentPlayingIndex - 1
                                                        currentPlayingIndex = prevIndex
                                                        isPlayingAudio = true
                                                    }
                                                },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.SkipPrevious,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    val totalSize = if (currentPlayingType == "anasheed") filteredAnasheed.size else state.benefits.size
                                                    if (totalSize > 0) {
                                                        currentPlayingIndex = (currentPlayingIndex + 1) % totalSize
                                                        isPlayingAudio = true
                                                    }
                                                },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.SkipNext,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            if (currentPlayingType == "anasheed") {
                                                val isFavorite = state.favoriteAnasheed.contains(playingTitle)
                                                IconButton(
                                                    onClick = { onFavoriteToggled(playingTitle) },
                                                    modifier = Modifier.size(36.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                                        contentDescription = "Favorite",
                                                        tint = if (isFavorite) Color.Red else Color.White.copy(alpha = 0.7f),
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(100.dp)
            .width(52.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f)
            ),
            modifier = Modifier.rotate(-90f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
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
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .liquidSource()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                MaterialTheme.colorScheme.background,
                            ),
                        ),
                    ),
            )
            Image(
                painter = painterResource(id = R.drawable.b7),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .liquidSource()
                    .alpha(0.4f),
            )
            when {
                state.isLoading -> LoadingView()
                state.errorMessage != null -> ErrorView(message = state.errorMessage)
                state.topic == null -> ErrorView(message = stringResource(R.string.error_view_message))
                else -> AyatSoundContent(
                    state = state,
                    contentPadding = contentPadding,
                    onBack = onBack,
                    resolveAudioPath = { viewModel.resolveAudioPath(it) }
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
    resolveAudioPath: suspend (String) -> String,
) {
    var currentIndex by rememberSaveable(state.topic?.id) { mutableIntStateOf(0) }
    var isPlaying by rememberSaveable(state.topic?.id) { mutableStateOf(false) }
    var isPrepared by rememberSaveable(state.topic?.id) { mutableStateOf(false) }
    var positionMs by rememberSaveable(state.topic?.id) { mutableIntStateOf(0) }
    var durationMs by rememberSaveable(state.topic?.id) { mutableIntStateOf(1) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }
    val lazyListState = rememberLazyListState()

    DisposableEffect(state.topic?.id) {
        onDispose {
            player?.release()
            player = null
        }
    }

    // Audio playback lifecycle + scroll - triggered when a new track is selected
    LaunchedEffect(currentIndex) {
        if (state.items.isNotEmpty() && currentIndex >= 0) {
            lazyListState.animateScrollToItem(currentIndex)

            player?.release()
            player = null
            isPrepared = false
            positionMs = 0
            durationMs = 1

            val item = state.items.getOrNull(currentIndex) ?: return@LaunchedEffect
            val resolvedPath = resolveAudioPath(item.url)
            val newPlayer = MediaPlayer()
            newPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            newPlayer.setDataSource(resolvedPath)
            newPlayer.setOnPreparedListener {
                isPrepared = true
                durationMs = it.duration.coerceAtLeast(1)
                if (isPlaying) {
                    it.start()
                }
            }
            newPlayer.setOnCompletionListener {
                isPlaying = false
                positionMs = 0
                if (state.items.isNotEmpty()) {
                    currentIndex = (currentIndex + 1) % state.items.size
                    isPlaying = true
                }
            }
            newPlayer.prepareAsync()
            player = newPlayer
        }
    }

    // Seekbar position updater
    LaunchedEffect(isPlaying, isPrepared) {
        while (isPlaying && isPrepared && player?.isPlaying == true) {
            positionMs = player?.currentPosition ?: 0
            durationMs = player?.duration?.coerceAtLeast(1) ?: 1
            delay(500)
        }
    }

    val visualizerComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loading_sound)
    )

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
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        state.topic?.let { topic ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center,
            ) {
                LiquidGlassCard(
                    onClick = {},
                    modifier = Modifier.fillMaxSize(),
                    cornerRadius = 24.dp,
                    refraction = 0.45f,
                    frost = 4f,
                    dispersion = 0.2f,
                    glowAlpha = 0.5f,
                ) {
                    AsyncImage(
                        model = topic.backgroundUrl,
                        contentDescription = topic.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    
                    if (isPlaying) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            LottieAnimation(
                                composition = visualizerComposition,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier.size(100.dp),
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LiquidGlassCard(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                refraction = 0.4f,
                frost = 8f,
                dispersion = 0.3f,
                glowAlpha = 0.5f,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = String.format("%02d:%02d", (positionMs / 1000) / 60, (positionMs / 1000) % 60),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                        
                        Slider(
                            value = positionMs.toFloat(),
                            onValueChange = { newValue ->
                                if (isPrepared && player != null) {
                                    player?.seekTo(newValue.toInt())
                                    positionMs = newValue.toInt()
                                }
                            },
                            valueRange = 0f..durationMs.toFloat(),
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                        )
                        
                        Text(
                            text = String.format("%02d:%02d", (durationMs / 1000) / 60, (durationMs / 1000) % 60),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = {
                                if (state.items.isNotEmpty()) {
                                    currentIndex = kotlin.random.Random.nextInt(state.items.size)
                                    isPlaying = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Shuffle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                if (state.items.isNotEmpty()) {
                                    currentIndex = if (currentIndex - 1 < 0) state.items.size - 1 else currentIndex - 1
                                    isPlaying = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SkipPrevious,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                if (isPrepared) {
                                    if (player?.isPlaying == true) {
                                        player?.pause()
                                        isPlaying = false
                                    } else {
                                        player?.start()
                                        isPlaying = true
                                    }
                                } else {
                                    isPlaying = !isPlaying
                                }
                            },
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                        ) {
                            if (isPlaying && !isPrepared) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (state.items.isNotEmpty()) {
                                    currentIndex = (currentIndex + 1) % state.items.size
                                    isPlaying = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SkipNext,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(18.dp))
            
            LazyColumn(
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.items.size, key = { index -> state.items[index].url }) { index ->
                    val item = state.items[index]
                    val isItemPlaying = currentIndex == index && isPlaying
                    val isItemCached = state.cachedUrls.contains(item.url)
                    AyatAudioItemCard(
                        item = item,
                        isPlaying = isItemPlaying,
                        isCached = isItemCached,
                        onClick = {
                            if (currentIndex == index && isPrepared) {
                                if (player?.isPlaying == true) {
                                    player?.pause()
                                    isPlaying = false
                                } else {
                                    player?.start()
                                    isPlaying = true
                                }
                            } else {
                                currentIndex = index
                                isPlaying = true
                            }
                        },
                    )
                }
            }
        }
    }
}
