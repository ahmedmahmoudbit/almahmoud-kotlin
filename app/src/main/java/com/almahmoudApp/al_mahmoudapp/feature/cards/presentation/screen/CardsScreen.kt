package com.almahmoudApp.al_mahmoudapp.feature.cards.presentation.screen

import AmiriFont
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.GlassButton
import com.almahmoudApp.al_mahmoudapp.core.ui.components.GlassButtonVariant
import com.almahmoudApp.al_mahmoudapp.core.ui.components.GlassIconButton
import com.almahmoudApp.al_mahmoudapp.core.ui.components.GlassTab
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassCard
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.liquidSource
import com.almahmoudApp.al_mahmoudapp.feature.cards.domain.model.CardCategory
import com.almahmoudApp.al_mahmoudapp.feature.cards.presentation.state.CardsUiState
import com.almahmoudApp.al_mahmoudapp.feature.cards.presentation.viewmodel.CardsViewModel

@Composable
fun CardsRoute(
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CardsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CardsScreen(
        state = state,
        contentPadding = contentPadding,
        onBack = onBack,
        onCategoryChanged = viewModel::onCategoryChanged,
        onNameChanged = viewModel::onNameChanged,
        onEidTemplateChanged = viewModel::onEidTemplateChanged,
        onGenderChanged = viewModel::onGenderChanged,
        onEidImageSelected = viewModel::onEidImageSelected,
        onGenerate = viewModel::generatePreview,
        onDownload = viewModel::saveToGallery,
        onOpenEidPopup = viewModel::openEidPopup,
        onEidPopupDismissed = viewModel::onEidPopupDismissed,
        onEidScaleChanged = viewModel::onEidScaleChanged,
        onEidOffsetChanged = viewModel::onEidOffsetChanged,
        onSaveEidComposite = viewModel::saveEidComposite,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    state: CardsUiState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onCategoryChanged: (CardCategory) -> Unit,
    onNameChanged: (String) -> Unit,
    onEidTemplateChanged: (Int) -> Unit,
    onGenderChanged: (Boolean) -> Unit,
    onEidImageSelected: (Uri) -> Unit,
    onGenerate: () -> Unit,
    onDownload: () -> Unit,
    onOpenEidPopup: () -> Unit = {},
    onEidPopupDismissed: () -> Unit = {},
    onEidScaleChanged: (Float) -> Unit = {},
    onEidOffsetChanged: (Float, Float) -> Unit = { _, _ -> },
    onSaveEidComposite: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onEidImageSelected(it)
            if (state.category == CardCategory.EID_FITR) {
                onOpenEidPopup()
            }
        }
    }

    LaunchedEffect(state.statusMessage) {
        state.statusMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "صانع بطاقات الإهداء والتعزية",
                        fontFamily = AmiriFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                navigationIcon = {
                    LiquidGlassCard(
                        onClick = onBack,
                        modifier = Modifier.size(40.dp),
                    ) {
                        val bgLuminance = MaterialTheme.colorScheme.background.let { 0.299f * it.red + 0.587f * it.green + 0.114f * it.blue }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = if (bgLuminance < 0.5f) Color.White else Color.Black,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.padding(contentPadding),
    ) { innerPadding ->
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(CardCategory.entries.size) { index ->
                        val cat = CardCategory.entries[index]
                        GlassTab(
                            text = cat.title,
                            isSelected = state.category == cat,
                            onClick = { onCategoryChanged(cat) },
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                // Name Input
                if (state.category != CardCategory.EID_FITR) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = onNameChanged,
                            label = {
                                Text(
                                    text = "اكتب الاسم هنا...",
                                    fontFamily = AmiriFont,
                                    fontSize = 13.sp,
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = AmiriFont,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Right,
                            ),
                            placeholder = {
                                Text(
                                    text = "الاسم",
                                    fontFamily = AmiriFont,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    onGenerate()
                                },
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                        )
                    }
                }

                if (state.category != CardCategory.EID_FITR) {
                GlassButton(
                    onClick = {
                        keyboardController?.hide()
                        onGenerate()
                    },
                    isLoading = state.isGenerating,
                    variant = GlassButtonVariant.Primary,
                    cornerRadius = 22.dp,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (!state.isGenerating) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "رسم وتصميم البطاقة",
                                fontFamily = AmiriFont,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
                }

                if (state.category == CardCategory.MAWALEED) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onGenderChanged(true) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Male,
                                contentDescription = "ولد",
                                tint = if (state.isBoy) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier.size(28.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ولد",
                                fontFamily = AmiriFont,
                                fontWeight = FontWeight.Bold,
                                color = if (state.isBoy) MaterialTheme.colorScheme.primary else Color.Gray,
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onGenderChanged(false) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Female,
                                contentDescription = "بنت",
                                tint = if (!state.isBoy) Color(0xFFE91E63) else Color.Gray,
                                modifier = Modifier.size(28.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "بنت",
                                fontFamily = AmiriFont,
                                fontWeight = FontWeight.Bold,
                                color = if (!state.isBoy) Color(0xFFE91E63) else Color.Gray,
                            )
                        }
                    }
                }

                if (state.category == CardCategory.EID_FITR) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "اختر الإطار:",
                            fontFamily = AmiriFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(5) { index ->
                                val isSelected = state.eidTemplateIndex == index
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { onEidTemplateChanged(index) }
                                        .then(
                                            if (isSelected) Modifier.border(
                                                2.dp, MaterialTheme.colorScheme.primary,
                                                RoundedCornerShape(12.dp)
                                            ) else Modifier
                                        ),
                                ) {
                                    Image(
                                        painter = painterResource(
                                            when (index) {
                                                0 -> R.drawable.eid1
                                                1 -> R.drawable.eid2
                                                2 -> R.drawable.eid3
                                                3 -> R.drawable.eid4
                                                else -> R.drawable.eid5
                                            }
                                        ),
                                        contentDescription = "إطار ${index + 1}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                    )
                                }
                            }
                        }
                        Text(
                            text = "اختر صورة التهنئة:",
                            fontFamily = AmiriFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        LiquidGlassCard(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            cornerRadius = 16.dp,
                            refraction = 0.3f,
                            frost = 4f,
                            dispersion = 0.1f,
                            glowAlpha = 0.3f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "رفع صورة",
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(40.dp),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "اضغط لرفع صورة",
                                    fontFamily = AmiriFont,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                )
                            }
                        }
                    }
                }

                if (state.previewBitmap != null) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                            modifier = Modifier.fillMaxWidth(0.88f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                        ) {
                            Box {
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                                            )
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = state.category.title,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                            ),
                                        )
                                    }
                                    Image(
                                        painter = BitmapPainter(state.previewBitmap.asImageBitmap()),
                                        contentDescription = "معاينة البطاقة",
                                        contentScale = ContentScale.FillWidth,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }

                                GlassIconButton(
                                    onClick = onDownload,
                                    variant = GlassButtonVariant.Plain,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(12.dp)
                                        .size(48.dp),
                                ) {
                                    if (state.isDownloading) {
                                        androidx.compose.material3.CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(22.dp),
                                            strokeWidth = 2.dp,
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = "حفظ",
                                            tint = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.size(22.dp),
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

    if (state.showEidPopup && state.eidUserBitmap != null && state.eidFrameBitmap != null) {
        val eidUserBitmap = state.eidUserBitmap
        val eidFrameBitmap = state.eidFrameBitmap
        EidPopup(
            userBitmap = eidUserBitmap,
            frameBitmap = eidFrameBitmap,
            scale = state.eidScale,
            offsetX = state.eidOffsetX,
            offsetY = state.eidOffsetY,
            isDownloading = state.isDownloading,
            onScaleChanged = onEidScaleChanged,
            onOffsetChanged = onEidOffsetChanged,
            onSave = onSaveEidComposite,
            onDismiss = onEidPopupDismissed,
        )
    }
}

@Composable
private fun EidPopup(
    userBitmap: Bitmap,
    frameBitmap: Bitmap,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    isDownloading: Boolean,
    onScaleChanged: (Float) -> Unit,
    onOffsetChanged: (Float, Float) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    val latestScale by rememberUpdatedState(scale)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            onScaleChanged(latestScale * zoom)
                            onOffsetChanged(pan.x, pan.y)
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    bitmap = userBitmap.asImageBitmap(),
                    contentDescription = "صورة المستخدم",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY,
                        ),
                )
            }

            Image(
                bitmap = frameBitmap.asImageBitmap(),
                contentDescription = "الإطار",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
            )

            GlassIconButton(
                onClick = onDismiss,
                variant = GlassButtonVariant.Plain,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .size(44.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "رجوع",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }

            GlassIconButton(
                onClick = onSave,
                variant = GlassButtonVariant.Plain,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(56.dp),
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "حفظ",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }
}
