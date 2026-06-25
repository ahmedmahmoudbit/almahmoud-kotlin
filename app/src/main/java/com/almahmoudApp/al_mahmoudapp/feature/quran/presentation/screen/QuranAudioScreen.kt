package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.screen

import android.media.AudioManager
import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.data.AudioCacheManager
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.QuranViewModel
import dev.chrisbanes.haze.HazeState
import java.text.DecimalFormat
import kotlinx.coroutines.delay

private val DARK_BG = Color(0xFF0A1628)
private val DARK_BG_LIGHT = Color(0xFF0D2547)
private val ACCENT_GOLD = Color(0xFFFFD54F)
private val ACCENT_TEAL = Color(0xFF4DD0C4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranAudioRoute(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    readerName: String,
    readerImage: String,
    audioBaseUrl: String,
    page: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuranViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val audioCacheManager = remember { AudioCacheManager(context) }

    var currentReaderName by rememberSaveable { mutableStateOf(readerName) }
    var currentReaderImage by rememberSaveable { mutableStateOf(readerImage) }
    var currentAudioBaseUrl by rememberSaveable { mutableStateOf(audioBaseUrl) }
    var currentPage by rememberSaveable { mutableIntStateOf(page.coerceAtLeast(1)) }

    var isPlaying by rememberSaveable { mutableStateOf(false) }
    var isPrepared by rememberSaveable { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var isLooping by rememberSaveable { mutableStateOf(false) }
    var positionMs by rememberSaveable { mutableIntStateOf(0) }
    var durationMs by rememberSaveable { mutableIntStateOf(1) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }
    var loopSnackbarMessage by remember { mutableStateOf<String?>(null) }

    val quranState by viewModel.state.collectAsStateWithLifecycle()

    var showSurahSheet by remember { mutableStateOf(false) }
    var showReaderSheet by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            player?.release()
            player = null
        }
    }

    val audioUrl = quranAudioUrl(currentAudioBaseUrl, currentPage)

    LaunchedEffect(audioUrl) {
        player?.release()
        isPrepared = false
        positionMs = 0
        durationMs = 1

        if (currentAudioBaseUrl.isBlank()) return@LaunchedEffect

        isDownloading = true
        try {
            val cacheResult = audioCacheManager.getAudioFile(audioUrl)
            isDownloading = false

            cacheResult.onSuccess { localFile ->
                val mediaPlayer = MediaPlayer()
                player = mediaPlayer
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer.setDataSource(localFile.absolutePath)
                mediaPlayer.isLooping = isLooping
                mediaPlayer.setOnPreparedListener {
                    isPrepared = true
                    durationMs = it.duration.coerceAtLeast(1)
                    if (isPlaying) {
                        it.start()
                    }
                }
                mediaPlayer.setOnCompletionListener {
                    if (!isLooping) {
                        isPlaying = false
                        positionMs = 0
                    }
                }
                mediaPlayer.prepareAsync()
            }
            cacheResult.onFailure {
                isDownloading = false
            }
        } catch (e: Exception) {
            isDownloading = false
        }
    }

    LaunchedEffect(isLooping) {
        player?.isLooping = isLooping
    }

    LaunchedEffect(loopSnackbarMessage) {
        if (loopSnackbarMessage != null) {
            delay(2000)
            loopSnackbarMessage = null
        }
    }

    LaunchedEffect(isPlaying, isPrepared) {
        if (isPlaying && isPrepared) {
            try {
                player?.start()
                while (player?.isPlaying == true) {
                    positionMs = player?.currentPosition ?: 0
                    durationMs = player?.duration?.coerceAtLeast(1) ?: 1
                    delay(500)
                }
            } catch (_: Exception) { }
        }
    }

    // Pulse animation for playing state
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")
    val waveScale1 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave1",
    )
    val waveAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "waveAlpha1",
    )
    val waveScale2 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "wave2",
    )
    val waveAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "waveAlpha2",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DARK_BG, DARK_BG_LIGHT, DARK_BG),
                ),
            ),
    ) {
        // Decorative gradient circles
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-50).dp, y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ACCENT_TEAL.copy(alpha = 0.12f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 60.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            ACCENT_GOLD.copy(alpha = 0.08f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Back button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }

                // Options button
                var showOptionsMenu by remember { mutableStateOf(false) }

                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                            .clickable { showOptionsMenu = !showOptionsMenu },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = stringResource(R.string.quran_options),
                            tint = Color.White,
                            modifier = Modifier.size(22.dp),
                        )
                    }

                    AnimatedVisibility(
                        visible = showOptionsMenu,
                        enter = fadeIn(tween(200)) + expandVertically(),
                        exit = fadeOut(tween(200)) + shrinkVertically(),
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .width(210.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(DARK_BG.copy(alpha = 0.96f))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                                .padding(vertical = 6.dp),
                        ) {
                            OptionsMenuItem(
                                text = stringResource(R.string.quran_change_background),
                                icon = Icons.Rounded.Wallpaper,
                                onClick = { showOptionsMenu = false },
                            )
                            OptionsDivider()
                            OptionsMenuItem(
                                text = stringResource(R.string.quran_choose_surah),
                                icon = Icons.AutoMirrored.Rounded.MenuBook,
                                onClick = {
                                    showOptionsMenu = false
                                    showSurahSheet = true
                                },
                            )
                            OptionsDivider()
                            OptionsMenuItem(
                                text = stringResource(R.string.quran_choose_reader),
                                icon = Icons.Rounded.Person,
                                onClick = {
                                    showOptionsMenu = false
                                    showReaderSheet = true
                                },
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.6f))

            // Reader image with pulse effect
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (isPlaying && isPrepared) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .graphicsLayer(
                                scaleX = waveScale1,
                                scaleY = waveScale1,
                                alpha = waveAlpha1,
                            )
                            .border(3.dp, Color.White, CircleShape),
                    )
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .graphicsLayer(
                                scaleX = waveScale2,
                                scaleY = waveScale2,
                                alpha = waveAlpha2,
                            )
                            .border(1.5.dp, Color.White, CircleShape),
                    )
                }

                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                ) {
                    if (currentReaderImage.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(currentReaderImage)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .crossfade(true)
                                .build(),
                            contentDescription = currentReaderName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.8f))

            val currentSurah = quranState.content?.surahs?.firstOrNull { it.pageNumber == currentPage }
            val surahLabel = currentSurah?.nameArabic ?: "${stringResource(R.string.quran_page)} $currentPage"

            // Player card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 18.dp, vertical = 18.dp),
            ) {
                // Header
                Text(
                    text = "$surahLabel - $currentReaderName",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Progress bar
                val currentPosStr = remember(positionMs) {
                    val sec = (positionMs / 1000) % 60
                    val min = (positionMs / 1000) / 60
                    String.format("%02d:%02d", min, sec)
                }
                val durationStr = remember(durationMs) {
                    val totalSec = (durationMs / 1000)
                    if (totalSec <= 0) "00:00" else {
                        val sec = totalSec % 60
                        val min = totalSec / 60
                        String.format("%02d:%02d", min, sec)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = currentPosStr,
                        color = Color.White.copy(alpha = 0.55f),
                        style = MaterialTheme.typography.labelSmall,
                    )

                    WaveformSeekbar(
                        positionMs = positionMs,
                        durationMs = durationMs,
                        onSeek = { newPos ->
                            positionMs = newPos
                            player?.seekTo(newPos)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                    )

                    Text(
                        text = "-$durationStr",
                        color = Color.White.copy(alpha = 0.55f),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Player buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {
                        val target = (positionMs - 10000).coerceAtLeast(0)
                        player?.seekTo(target)
                        positionMs = target
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.FastRewind,
                            contentDescription = "Rewind 10s",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp),
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
                                isPlaying = true
                            }
                        },
                        modifier = Modifier.size(48.dp),
                    ) {
                        if (isDownloading || (isPlaying && !isPrepared)) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.5.dp,
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(42.dp),
                            )
                        }
                    }

                    IconButton(onClick = {
                        val target = (positionMs + 10000).coerceAtMost(durationMs)
                        player?.seekTo(target)
                        positionMs = target
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.FastForward,
                            contentDescription = "Forward 10s",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp),
                        )
                    }

                    IconButton(onClick = {
                        isLooping = !isLooping
                        loopSnackbarMessage = if (isLooping) "تم تفعيل إعادة التشغيل" else "تم إيقاف إعادة التشغيل"
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Repeat,
                            contentDescription = "Repeat",
                            tint = if (isLooping) ACCENT_TEAL else Color.White.copy(alpha = 0.65f),
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "بمجرد تشغيل المقطع لأول مرة، سيتم حفظه وسيكون تشغيله أسرع في المرات المقبلة",
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Loop toggle notification
        AnimatedVisibility(
            visible = loopSnackbarMessage != null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 96.dp),
            enter = fadeIn(tween(250)) + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut(tween(250)) + slideOutVertically(targetOffsetY = { -it }),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF0A2218).copy(alpha = 0.96f))
                    .border(1.dp, ACCENT_TEAL.copy(alpha = 0.50f), RoundedCornerShape(32.dp))
                    .padding(horizontal = 24.dp, vertical = 10.dp),
            ) {
                Text(
                    text = loopSnackbarMessage ?: "",
                    color = ACCENT_TEAL,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }

    // Surah selection sheet
    if (showSurahSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSurahSheet = false },
            containerColor = DARK_BG,
            scrimColor = Color.Black.copy(alpha = 0.6f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.quran_choose_surah),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp),
                )

                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                    quranState.content?.surahs?.let { surahs ->
                        items(surahs.size) { index ->
                            val surah = surahs[index]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentPage = surah.pageNumber
                                        showSurahSheet = false
                                        isPlaying = false
                                        isPrepared = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(text = "${surah.number}. ${surah.nameEnglish}", color = Color.White)
                                Text(text = surah.nameArabic, color = ACCENT_GOLD)
                            }
                        }
                    }
                }
            }
        }
    }

    // Reader selection sheet
    if (showReaderSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReaderSheet = false },
            containerColor = DARK_BG,
            scrimColor = Color.Black.copy(alpha = 0.6f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.quran_choose_reader),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp),
                )

                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                    quranState.content?.readers?.let { readers ->
                        items(readers.size) { index ->
                            val reader = readers[index]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentReaderName = reader.name
                                        currentReaderImage = reader.imageUrl
                                        currentAudioBaseUrl = reader.audioBaseUrl
                                        showReaderSheet = false
                                        isPlaying = false
                                        isPrepared = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.1f)),
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(reader.imageUrl)
                                            .diskCachePolicy(CachePolicy.ENABLED)
                                            .memoryCachePolicy(CachePolicy.ENABLED)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = reader.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = reader.name, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionsMenuItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ACCENT_TEAL,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun OptionsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(Color.White.copy(alpha = 0.10f)),
    )
}

private fun quranAudioUrl(baseUrl: String, page: Int): String {
    val formattedPage = DecimalFormat("000").format(page)
    return baseUrl + arabicToDecimal(formattedPage) + ".mp3"
}

private fun arabicToDecimal(number: String): String {
    val chars = CharArray(number.length)
    for (index in number.indices) {
        val ch = number[index]
        chars[index] = when {
            ch in '\u0660'..'\u0669' -> (ch.code - '\u0660'.code + '0'.code).toChar()
            ch in '\u06f0'..'\u06f9' -> (ch.code - '\u06f0'.code + '0'.code).toChar()
            else -> ch
        }
    }
    return String(chars)
}

@Composable
private fun WaveformSeekbar(
    positionMs: Int,
    durationMs: Int,
    onSeek: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeColor = Color.White
    val inactiveColor = Color.White.copy(alpha = 0.25f)

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
            },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalWidth = size.width
            val height = size.height
            val numBars = barHeights.size
            val gap = 2.5.dp.toPx()
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
                    cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx()),
                )
            }
        }
    }
}
