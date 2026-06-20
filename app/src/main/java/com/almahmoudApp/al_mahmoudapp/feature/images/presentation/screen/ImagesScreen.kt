package com.almahmoudApp.al_mahmoudapp.feature.images.presentation.screen

import AmiriFont
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.allowHardware
import coil3.asDrawable
import coil3.request.SuccessResult
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.EmptyView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
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
                state.islamicImages.isEmpty() && state.wallpaperImages.isEmpty() -> EmptyView()
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
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedImageForPreview by remember { mutableStateOf<IslamicImage?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(vertical = 14.dp),
    ) {
        // Top Bar
        ImagesTopBar(onBack = onBack, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(10.dp))

        // Dynamic tabs
        val titles = if (NumberLocalization.isArabic()) {
            listOf("خلفيات إسلامية", "افهمها بطريقتك")
        } else {
            listOf("Wallpapers", "Islamic Quotes")
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            divider = {},
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = AmiriFont
                            )
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List contents
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            if (selectedTab == 0) {
                // Wallpapers (3-column regular grid)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.wallpaperImages) { image ->
                        WallpaperGridItem(image = image, onClick = { selectedImageForPreview = image })
                    }
                }
            } else {
                // Islamic Quotes (Staggered Grid)
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.islamicImages) { image ->
                        QuoteStaggeredItem(image = image, onClick = { selectedImageForPreview = image })
                    }
                }
            }
        }
    }

    // Fullscreen Image viewer dialog
    selectedImageForPreview?.let { image ->
        FullscreenImageDialog(
            image = image,
            onDismiss = { selectedImageForPreview = null }
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
            text = if (NumberLocalization.isArabic()) "الصور والخلفيات" else "Images & Wallpapers",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = AmiriFont),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun WallpaperGridItem(
    image: IslamicImage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image.url)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun QuoteStaggeredItem(
    image: IslamicImage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Generate simple pseudo-random heights for aesthetic staggering effect
    val itemHeight = remember(image.url) {
        val heights = listOf(200.dp, 240.dp, 280.dp, 320.dp)
        heights[kotlin.math.abs(image.url.hashCode()) % heights.size]
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image.url)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun FullscreenImageDialog(
    image: IslamicImage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    var isSettingWallpaper by remember { mutableStateOf(false) }

    @androidx.annotation.RequiresPermission(android.Manifest.permission.SET_WALLPAPER)
    fun setWallpaper(lockScreenOnly: Boolean = false) {
        isSettingWallpaper = true
        scope.launch(Dispatchers.IO) {
            try {
                val loader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(image.url)
                    .allowHardware(false)
                    .build()
                val result = (loader.execute(request) as? SuccessResult)?.image
                val drawable = result?.asDrawable(context.resources)
                val bitmap = (drawable as? BitmapDrawable)?.bitmap
                if (bitmap != null) {
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val flag = if (lockScreenOnly) WallpaperManager.FLAG_LOCK else WallpaperManager.FLAG_SYSTEM
                        wallpaperManager.setBitmap(bitmap, null, true, flag)
                    } else {
                        wallpaperManager.setBitmap(bitmap)
                    }
                    withContext(Dispatchers.Main) {
                        val msg = if (NumberLocalization.isArabic()) "تم تعيين الخلفية بنجاح" else "Wallpaper set successfully"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val msg = if (NumberLocalization.isArabic()) "فشل تعيين الخلفية" else "Failed to set wallpaper"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            } finally {
                isSettingWallpaper = false
            }
        }
    }

    fun saveToGallery() {
        isSaving = true
        scope.launch(Dispatchers.IO) {
            try {
                val loader = coil3.ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(image.url)
                    .allowHardware(false)
                    .build()
                val result = (loader.execute(request) as? coil3.request.SuccessResult)?.image
                val drawable = result?.asDrawable(context.resources)
                val bitmap = (drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                if (bitmap != null) {
                    val filename = "AlMahmoud_${System.currentTimeMillis()}.jpg"
                    var fos: java.io.OutputStream? = null
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
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
                        val msg = if (NumberLocalization.isArabic()) "تم حفظ الصورة في الاستوديو بنجاح" else "Image saved to gallery successfully"
                        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val msg = if (NumberLocalization.isArabic()) "فشل حفظ الصورة" else "Failed to save image"
                    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                }
            } finally {
                isSaving = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Close Button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .padding(top = 44.dp, start = 20.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .align(Alignment.TopStart)
            ) {
                Icon(imageVector = Icons.Rounded.Close, contentDescription = null, tint = Color.White)
            }

            // Image Preview (Interactive)
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image.url)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )

            // Bottom Actions Panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Download Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(enabled = !isSaving) { saveToGallery() }
                    ) {
                        IconButton(
                            onClick = { saveToGallery() },
                            enabled = !isSaving,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(imageVector = Icons.Rounded.Download, contentDescription = null, tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (NumberLocalization.isArabic()) "حفظ" else "Save",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontFamily = AmiriFont
                        )
                    }

                    // Share Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, image.url)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, null))
                        }
                    ) {
                        IconButton(
                            onClick = {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, image.url)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, null))
                            },
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(imageVector = Icons.Rounded.Share, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (NumberLocalization.isArabic()) "مشاركة" else "Share",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontFamily = AmiriFont
                        )
                    }
                }

                // If wallpaper, show wallpaper options
                if (image.isWallpaper) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { setWallpaper(lockScreenOnly = false) },
                            enabled = !isSettingWallpaper,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Rounded.Wallpaper, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (NumberLocalization.isArabic()) "شاشة الرئيسة" else "Home Screen",
                                fontFamily = AmiriFont
                            )
                        }

                        Button(
                            onClick = { setWallpaper(lockScreenOnly = true) },
                            enabled = !isSettingWallpaper,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Rounded.Wallpaper, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (NumberLocalization.isArabic()) "شاشة القفل" else "Lock Screen",
                                fontFamily = AmiriFont
                            )
                        }
                    }
                }
            }
        }
    }
}
