package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.almahmoudApp.al_mahmoudapp.R
import java.io.File

// ─────────────────────────────────────────────
// Video URLs  (Google Drive direct-download links)
// ─────────────────────────────────────────────
internal val quranBackgroundVideos: List<String> = listOf(
    driveDirectUrl("146wi3k9UrPceU909_44rrtf9wmSBfgw0"),
    driveDirectUrl("1RgJZ10G-RgfPCk0Z8rMBPlESnGWaPJmk"),
    driveDirectUrl("1uaikKCF4LVDU30xVFuEiTPz191v4yVFF"),
    driveDirectUrl("1HWorEIFyrMwmoHWBdbQKqigpTUBxhJQz"),
    driveDirectUrl("18oVUIL3m_ivXsL5uYo8oi9jbVXjMZdWA"),
    driveDirectUrl("1W95RK0HXaOery5a-Kr2D3EfOHylcqEYa"),
    driveDirectUrl("1Rqps3TukU3yol8qPgZuuRBYfk6bZsELo"),
    driveDirectUrl("11Ths0Cc_ldQHpn86SwN1EkZvfrydxOKt"),
    driveDirectUrl("1-otEirDeLXF3eCr8B1qC0gUPMhfmNJ7I"),
)

private fun driveDirectUrl(fileId: String): String =
    "https://drive.google.com/uc?export=download&id=$fileId"

// ─────────────────────────────────────────────
// Cache singleton – one instance per process
// ─────────────────────────────────────────────
private object QuranVideoCache {
    @Volatile
    private var cache: SimpleCache? = null

    @OptIn(UnstableApi::class)
    fun get(context: Context): SimpleCache {
        return cache ?: synchronized(this) {
            cache ?: SimpleCache(
                File(context.cacheDir, "quran_video_cache"),
                LeastRecentlyUsedCacheEvictor(500L * 1024 * 1024), // 500 MB
                androidx.media3.database.StandaloneDatabaseProvider(context),
            ).also { cache = it }
        }
    }
}

// ─────────────────────────────────────────────
// State holder
// ─────────────────────────────────────────────
@Stable
class QuranVideoPlayerState(
    initialVideoIndex: Int = 0,
) {
    var currentVideoIndex by mutableIntStateOf(initialVideoIndex)
        private set

    var showVideoPicker by mutableStateOf(false)

    fun selectVideo(index: Int) {
        currentVideoIndex = index
        showVideoPicker = false
    }
}

@Composable
fun rememberQuranVideoPlayerState(initialIndex: Int = 0): QuranVideoPlayerState =
    remember { QuranVideoPlayerState(initialIndex) }

// ─────────────────────────────────────────────
// Public composable: full-screen video background
// with blue overlay + switcher button
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class, UnstableApi::class)
@Composable
fun QuranVideoBackground(
    state: QuranVideoPlayerState,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val context = LocalContext.current

    // Build ExoPlayer with caching
    val player = rememberCachedExoPlayer(context)

    // Swap video when index changes
    LaunchedEffect(state.currentVideoIndex) {
        val url = quranBackgroundVideos.getOrNull(state.currentVideoIndex) ?: return@LaunchedEffect
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.play()
    }

    Box(modifier = modifier.fillMaxSize()) {

        // ── 1. Crossfade between video instances ──────────────────────
        Crossfade(
            targetState = state.currentVideoIndex,
            animationSpec = tween(durationMillis = 800),
            label = "video_crossfade",
        ) { _ ->
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        this.player = player
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                    }
                },
                update = { view ->
                    view.player = player
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        // ── 2. Blue overlay ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A1628).copy(alpha = 0.72f),
                            Color(0xFF0D2547).copy(alpha = 0.60f),
                            Color(0xFF061020).copy(alpha = 0.80f),
                        ),
                    ),
                ),
        )

        // ── 3. Top gradient for TopBar readability ────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF020D1A).copy(alpha = 0.85f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        // ── 4. Bottom gradient fade ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF020D1A).copy(alpha = 0.90f),
                        ),
                    ),
                ),
        )

        // ── 5. Screen content (caller-supplied) ──────────────────────────────
        content()

        // ── 6. Video picker bottom-sheet ────────────────────────────────────────
        if (state.showVideoPicker) {
            VideoPickerBottomSheet(
                currentIndex = state.currentVideoIndex,
                onSelect = { index -> state.selectVideo(index) },
                onDismiss = { state.showVideoPicker = false },
            )
        }
    }
}

// ─────────────────────────────────────────────
// ExoPlayer with cache – remembered per composition
// ─────────────────────────────────────────────
@Composable
private fun rememberCachedExoPlayer(context: Context): ExoPlayer {
    val videoCache = remember { QuranVideoCache.get(context) }

    val dataSourceFactory = remember {
        CacheDataSource.Factory()
            .setCache(videoCache)
            .setUpstreamDataSourceFactory(
                DefaultHttpDataSource.Factory()
                    .setAllowCrossProtocolRedirects(true),
            )
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    val player = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(
                ProgressiveMediaSource.Factory(dataSourceFactory),
            )
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
                playWhenReady = true
                volume = 0f // muted – only visual background
            }
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    return player
}

// ─────────────────────────────────────────────
// FAB – circular button to open video switcher
// ─────────────────────────────────────────────
@Composable
private fun VideoSwitcherFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val glowAlpha by animateFloatAsState(
        targetValue = 0.85f,
        animationSpec = tween(600),
        label = "glow_alpha",
    )

    Box(
        modifier = modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF3A7BD5).copy(alpha = 0.90f),
                        Color(0xFF1A4A8A).copy(alpha = 0.80f),
                    ),
                ),
            )
            .border(
                width = 1.5.dp,
                color = Color(0xFF6FA3E0).copy(alpha = glowAlpha),
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Wallpaper,
            contentDescription = stringResource(R.string.quran_change_background),
            tint = Color.White,
            modifier = Modifier.size(24.dp),
        )
    }
}

// ─────────────────────────────────────────────
// Bottom-sheet: grid of numbered video thumbnails
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoPickerBottomSheet(
    currentIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0D1B2E),
        scrimColor = Color.Black.copy(alpha = 0.55f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.quran_choose_background),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calculate item width dynamically: 2 items on mobile, 3 on tablet
            val configuration = LocalConfiguration.current
            val screenWidthDp = configuration.screenWidthDp.toFloat()
            val isTablet = screenWidthDp >= 600f
            val visibleCount = if (isTablet) 3 else 2
            val spacingTotal = 12f * (visibleCount - 1)
            val horizontalPaddingTotal = 40f
            val itemWidthDp = (screenWidthDp - spacingTotal - horizontalPaddingTotal) / visibleCount

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                itemsIndexed(quranBackgroundVideos) { index, _ ->
                    VideoThumbnailCard(
                        index = index,
                        isSelected = index == currentIndex,
                        onClick = { onSelect(index) },
                        itemWidthDp = itemWidthDp,
                    )
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoThumbnailCard(
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    itemWidthDp: Float = 160f,
) {
    val context = LocalContext.current
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF3A7BD5) else Color.White.copy(alpha = 0.20f),
        animationSpec = tween(300),
        label = "border_color",
    )
    val url = quranBackgroundVideos.getOrNull(index).orEmpty()

    Box(
        modifier = Modifier
            .width(itemWidthDp.dp)
            .height((itemWidthDp * 1.55f).dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        // Muted miniature player inside the card
        AndroidView(
            factory = { ctx ->
                val videoCache = QuranVideoCache.get(ctx)
                val dataSourceFactory = CacheDataSource.Factory()
                    .setCache(videoCache)
                    .setUpstreamDataSourceFactory(
                        DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
                    )
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

                val exoplayer = ExoPlayer.Builder(ctx)
                    .setMediaSourceFactory(ProgressiveMediaSource.Factory(dataSourceFactory))
                    .build()
                    .apply {
                        repeatMode = Player.REPEAT_MODE_ONE
                        volume = 0f
                        playWhenReady = true
                    }

                exoplayer.setMediaItem(MediaItem.fromUri(url))
                exoplayer.prepare()

                PlayerView(ctx).apply {
                    this.player = exoplayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
            },
            update = { view ->
                // Keep playing
            },
            onRelease = { view ->
                view.player?.release()
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay to dim the video slightly and show thumbnail number
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp)
        ) {
            Text(
                text = "${index + 1}",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                ),
            )
        }
    }
}
