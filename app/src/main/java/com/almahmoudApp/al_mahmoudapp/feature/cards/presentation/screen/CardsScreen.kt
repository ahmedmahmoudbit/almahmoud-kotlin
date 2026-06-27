package com.almahmoudApp.al_mahmoudapp.feature.cards.presentation.screen

import AmiriFont
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.R
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
        onGenerate = viewModel::generatePreview,
        onDownload = viewModel::saveToGallery,
        modifier = modifier
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
    onGenerate: () -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Observe state statusMessage
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
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.padding(contentPadding)
    ) { innerPadding ->
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .liquidSource()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Category Tabs Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(CardCategory.values().size) { index ->
                            val cat = CardCategory.values()[index]
                            val selected = state.category == cat
                            FilterChip(
                                selected = selected,
                                onClick = { onCategoryChanged(cat) },
                                label = { Text(text = cat.title, fontFamily = AmiriFont, fontSize = 14.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Dynamic Input Options based on category
                LiquidGlassCard(
                    onClick = {},
                    cornerRadius = 20.dp,
                    refraction = 0.2f,
                    frost = 8f,
                    dispersion = 0.15f,
                    glowAlpha = 0.3f,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = onNameChanged,
                            label = { Text(text = "اكتب الاسم هنا...", fontFamily = AmiriFont) },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = AmiriFont,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Right
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    onGenerate()
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Special controls depending on Card Type
                        if (state.category == CardCategory.MAWALEED) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onGenderChanged(true) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Male,
                                        contentDescription = "ولد",
                                        tint = if (state.isBoy) MaterialTheme.colorScheme.primary else Color.Gray,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "ولد",
                                        fontFamily = AmiriFont,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.isBoy) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onGenderChanged(false) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Female,
                                        contentDescription = "بنت",
                                        tint = if (!state.isBoy) Color(0xFFE91E63) else Color.Gray,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "بنت",
                                        fontFamily = AmiriFont,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!state.isBoy) Color(0xFFE91E63) else Color.Gray
                                    )
                                }
                            }
                        }

                        if (state.category == CardCategory.EID_FITR) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "اختر التصميم المفضل:",
                                    fontFamily = AmiriFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(5) { index ->
                                        val isSelected = state.eidTemplateIndex == index
                                        val designRes = when (index) {
                                            0 -> R.drawable.eid1
                                            1 -> R.drawable.eid2
                                            2 -> R.drawable.eid3
                                            3 -> R.drawable.eid4
                                            else -> R.drawable.eid5
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(70.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .border(
                                                    width = 2.dp,
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable { onEidTemplateChanged(index) }
                                        ) {
                                            Image(
                                                painter = painterResource(id = designRes),
                                                contentDescription = "عيد ${index + 1}",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = {
                                keyboardController?.hide()
                                onGenerate()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (state.isGenerating) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(text = "رسم وتصميم البطاقة", fontFamily = AmiriFont, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Card Preview Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.previewBitmap != null) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Image(
                                painter = BitmapPainter(state.previewBitmap.asImageBitmap()),
                                contentDescription = "معاينة البطاقة",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        // Display clean layout hint
                        LiquidGlassCard(
                            onClick = {},
                            cornerRadius = 16.dp,
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val templatePreviewPainter = when (state.category) {
                                    CardCategory.WAFAYAT -> painterResource(id = R.drawable.die)
                                    CardCategory.SADAKA -> painterResource(id = R.drawable.sadaka)
                                    CardCategory.MAWALEED -> painterResource(id = if (state.isBoy) R.drawable.boy else R.drawable.girl)
                                    CardCategory.EID_FITR -> painterResource(
                                        id = when (state.eidTemplateIndex) {
                                            0 -> R.drawable.eid1
                                            1 -> R.drawable.eid2
                                            2 -> R.drawable.eid3
                                            3 -> R.drawable.eid4
                                            else -> R.drawable.eid5
                                        }
                                    )
                                }

                                Image(
                                    painter = templatePreviewPainter,
                                    contentDescription = "قالب البطاقة",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.height(180.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "اكتب الاسم واضغط رسم وتصميم البطاقة للحصول على المعاينة هنا",
                                    fontFamily = AmiriFont,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Download/Save Button
                AnimatedVisibility(
                    visible = state.previewBitmap != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Button(
                        onClick = onDownload,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isDownloading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = "تحميل")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "حفظ في الاستوديو", fontFamily = AmiriFont, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
