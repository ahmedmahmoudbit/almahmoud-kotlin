package com.almahmoudApp.al_mahmoudapp.feature.images.presentation.screen

import AmiriFont
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.allowHardware
import coil3.asDrawable
import coil3.request.SuccessResult
import com.almahmoudApp.al_mahmoudapp.core.ui.components.EmptyView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassCard
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.core.util.NumberLocalization
import com.almahmoudApp.al_mahmoudapp.feature.images.domain.model.IslamicImage
import com.almahmoudApp.al_mahmoudapp.feature.images.presentation.state.ImagesUiState
import com.almahmoudApp.al_mahmoudapp.feature.images.presentation.viewmodel.ImagesViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

@Composable
fun ImagesRoute(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ImagesViewModel = hiltViewModel(),
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
                state.wallpaperImages.isEmpty() -> EmptyView()
                else -> ImagesScreenContent(
                    contentPadding = contentPadding,
                    state = state,
                    onBack = onBack,
                )
            }
        }
    }
}

@Composable
private fun ImagesScreenContent(
    contentPadding: PaddingValues,
    state: ImagesUiState,
    onBack: () -> Unit,
) {
    var selectedImageIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(vertical = 14.dp),
    ) {
        ImagesTopBar(onBack = onBack, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.wallpaperImages) { image ->
                val index = state.wallpaperImages.indexOf(image)
                WallpaperStaggeredItem(
                    image = image,
                    onClick = { selectedImageIndex = index }
                )
            }
        }
    }

    if (selectedImageIndex >= 0) {
        FullscreenWallpaperViewer(
            wallpapers = state.wallpaperImages,
            initialIndex = selectedImageIndex,
            onDismiss = { selectedImageIndex = -1 }
        )
    }
}

@Composable
private fun ImagesTopBar(
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
            text = if (NumberLocalization.isArabic()) "خلفيات" else "Wallpapers",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = AmiriFont),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun WallpaperStaggeredItem(
    image: IslamicImage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isImageLoading by remember { mutableStateOf(true) }

    val itemHeight = remember(image.url) {
        val heights = listOf(160.dp, 200.dp, 240.dp, 280.dp)
        heights[kotlin.math.abs(image.url.hashCode()) % heights.size]
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image.url)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onState = { state ->
                    isImageLoading = state is AsyncImagePainter.State.Loading ||
                        state is AsyncImagePainter.State.Empty
                }
            )
            if (isImageLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun FullscreenWallpaperViewer(
    wallpapers: List<IslamicImage>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    var isSavingIndex by remember { mutableIntStateOf(-1) }

    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { wallpapers.size }
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 1
            ) { page ->
                val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                val scaleFactor = 1f - abs(pageOffset) * 0.07f
                val alphaFactor = 1f - abs(pageOffset) * 0.4f

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scaleFactor
                            scaleY = scaleFactor
                            alpha = alphaFactor
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(wallpapers[page].url)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            LiquidGlassCard(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(top = 44.dp, start = 16.dp)
                    .size(44.dp)
                    .align(Alignment.TopStart),
                cornerRadius = 999.dp,
                refraction = 0.55f,
                frost = 8f,
                dispersion = 0.20f,
                glowAlpha = 0.70f,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = if (NumberLocalization.isArabic()) "رجوع" else "Back",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }

            LiquidGlassCard(
                onClick = {
                    val currentIndex = pagerState.currentPage
                    isSaving = true
                    isSavingIndex = currentIndex
                    scope.launch(Dispatchers.IO) {
                        try {
                            val loader = coil3.ImageLoader(context)
                            val request = ImageRequest.Builder(context)
                                .data(wallpapers[currentIndex].url)
                                .allowHardware(false)
                                .build()
                            val result = (loader.execute(request) as? SuccessResult)?.image
                            val drawable = result?.asDrawable(context.resources)
                            val bitmap = (drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                            if (bitmap != null) {
                                val filename = "AlMahmoud_${System.currentTimeMillis()}.jpg"
                                var fos: java.io.OutputStream? = null
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    val resolver = context.contentResolver
                                    val contentValues = android.content.ContentValues().apply {
                                        put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename)
                                        put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                                        put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES)
                                    }
                                    val imageUri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                    if (imageUri != null) {
                                        fos = resolver.openOutputStream(imageUri)
                                    }
                                } else {
                                    val imagesDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES)
                                    val imageFile = java.io.File(imagesDir, filename)
                                    fos = java.io.FileOutputStream(imageFile)
                                }
                                fos?.use {
                                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, it)
                                }
                                withContext(Dispatchers.Main) {
                                    val msg = if (NumberLocalization.isArabic()) "تم حفظ الخلفية بنجاح" else "Wallpaper saved successfully"
                                    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                val msg = if (NumberLocalization.isArabic()) "فشل حفظ الخلفية" else "Failed to save wallpaper"
                                android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                            }
                        } finally {
                            withContext(Dispatchers.Main) {
                                isSaving = false
                                isSavingIndex = -1
                            }
                        }
                    }
                },
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 60.dp)
                    .size(44.dp)
                    .align(Alignment.BottomEnd),
                cornerRadius = 999.dp,
                refraction = 0.55f,
                frost = 8f,
                dispersion = 0.20f,
                glowAlpha = 0.70f,
            ) {
                if (isSaving && isSavingIndex == pagerState.currentPage) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Download,
                        contentDescription = if (NumberLocalization.isArabic()) "تحميل" else "Download",
                        tint = Color.White,
                    )
                }
            }
        }
    }
}
