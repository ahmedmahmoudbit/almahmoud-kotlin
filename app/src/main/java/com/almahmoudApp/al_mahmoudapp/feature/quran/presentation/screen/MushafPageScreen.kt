package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.screen

import AmiriFont
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.mushaf.MushafPageView
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.AudioReciterItem
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.MushafViewModel
import com.almahmoudApp.al_mahmoudapp.feature.quran.util.QuranConstants
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MushafPageScreen(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    surahNumber: Int? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MushafViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var controlsVisible by remember { mutableStateOf(true) }
    var showPageInput by remember { mutableStateOf(false) }
    var selectedAyahId by remember { mutableStateOf<Int?>(null) }
    var showVerseDetails by remember { mutableStateOf(false) }
    var selectedSurahNo by remember { mutableIntStateOf(0) }
    var selectedVerseNo by remember { mutableIntStateOf(0) }
    var activeTabIndex by remember { mutableIntStateOf(0) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    LaunchedEffect(state.currentAudioUrl) {
        val url = state.currentAudioUrl
        if (url != null && state.isAudioPlaying) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(url)
                    setOnPreparedListener { it.start() }
                    setOnCompletionListener {
                        viewModel.onAudioCompleted()
                    }
                    setOnErrorListener { _, _, _ ->
                        viewModel.onAudioCompleted()
                        true
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                viewModel.onAudioCompleted()
            }
        } else if (url == null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    val pagerState = rememberPagerState(
        initialPage = (state.currentPage - 1).coerceAtLeast(0),
        pageCount = { state.totalPages },
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collectLatest { page ->
            viewModel.loadPage(page + 1)
            selectedAyahId = null
        }
    }

    LaunchedEffect(surahNumber) {
        if (surahNumber != null && surahNumber > 0) {
            viewModel.navigateToSurah(surahNumber)
        }
    }

    LaunchedEffect(state.currentPage) {
        if (state.currentPage > 0 && pagerState.currentPage != state.currentPage - 1) {
            pagerState.scrollToPage(state.currentPage - 1)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopAudio()
        }
    }

        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                ) {
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f),
                                beyondViewportPageCount = 2,
                            ) { pageIndex ->
                                val pageNum = pageIndex + 1
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { controlsVisible = !controlsVisible },
                                    contentAlignment = Alignment.Center,
                                ) {
                                    when {
                                        state.isLoading && state.currentPage != pageNum -> {
                                            CircularProgressIndicator(
                                                modifier = Modifier.padding(top = 120.dp),
                                                color = MaterialTheme.colorScheme.primary,
                                            )
                                        }
                                        state.errorMessage != null && state.page == null -> {
                                            Text(
                                                text = state.errorMessage!!,
                                                color = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.padding(top = 120.dp),
                                            )
                                        }
                                        state.page != null && state.currentPage == pageNum -> {
                                            MushafPageView(
                                                page = state.page!!,
                                                fontSize = 22f,
                                                selectedAyahId = selectedAyahId,
                                                onAyahClick = {
                                                    controlsVisible = !controlsVisible
                                                },
                                                onAyahLongClick = { ayahId ->
                                                    val (surahNo, verseNo) = com.almahmoudApp.al_mahmoudapp.feature.quran.util.QuranConstants.getChapterAndVerseFromAyahId(ayahId)
                                                    selectedAyahId = ayahId
                                                    selectedSurahNo = surahNo
                                                    selectedVerseNo = verseNo
                                                    showVerseDetails = true
                                                    viewModel.loadVerseDetails(surahNo, verseNo)
                                                    viewModel.loadVerseContent(surahNo, verseNo)
                                                },
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .verticalScroll(rememberScrollState()),
                                            )
                                        }
                                        else -> Box(modifier = Modifier.padding(top = 48.dp))
                                    }
                                }
                            }
                        }

                AnimatedVisibility(
                    visible = controlsVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.TopCenter),
                ) {
                    MushafTopBar(
                        currentPage = pagerState.currentPage + 1,
                        totalPages = state.totalPages,
                        onBack = onBack,
                        onPageClick = { showPageInput = true },
                    )
                }

                if (showPageInput) {
                    PageInputDialog(
                        currentPage = state.currentPage,
                        totalPages = state.totalPages,
                        onDismiss = { showPageInput = false },
                        onGoToPage = { page ->
                            showPageInput = false
                            viewModel.goToPage(page)
                        },
                    )
                }

                if (showVerseDetails) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showVerseDetails = false
                            viewModel.stopAudio()
                        },
                        sheetState = bottomSheetState,
                    ) {
                        VerseDetailsBottomSheet(
                            surahNo = selectedSurahNo,
                            verseNo = selectedVerseNo,
                            state = state,
                            activeTabIndex = activeTabIndex,
                            onTabSelected = { activeTabIndex = it },
                            onPlayAudio = { url, name ->
                                viewModel.playAudio(url, name)
                            },
                            onStopAudio = {
                                viewModel.stopAudio()
                            },
                            onLoadAudio = {
                                viewModel.loadVerseAudio(selectedSurahNo, selectedVerseNo)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VerseDetailsBottomSheet(
    surahNo: Int,
    verseNo: Int,
    state: com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.MushafUiState,
    activeTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onPlayAudio: (String, String) -> Unit,
    onStopAudio: () -> Unit,
    onLoadAudio: () -> Unit,
) {
    val details = state.selectedVerseDetails
    val primaryColor = MaterialTheme.colorScheme.primary
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "سورة ${getSurahNameArabic(surahNo)} - الآية ${verseNo.toArabicNumerals()}",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = AmiriFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CopyAyahButton(
                    surahNo = surahNo,
                    verseNo = verseNo,
                    verseContent = state.selectedVerseContent,
                    context = context,
                    primaryColor = primaryColor,
                )

                AudioPlayButton(
                    state = state,
                    onPlayAudio = onPlayAudio,
                    onStopAudio = onStopAudio,
                    onLoadAudio = onLoadAudio,
                )
            }
        }

        if (state.availableReciters.isNotEmpty()) {
            RecitersRow(
                reciters = state.availableReciters,
                currentPlayingReciter = state.currentPlayingReciter,
                isAudioPlaying = state.isAudioPlaying,
                onReciterClick = { reciter ->
                    onPlayAudio(reciter.url, reciter.name)
                },
            )
        }

        if (state.isAudioLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "جاري تحميل القراء...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }

        ScrollableTabRow(
            selectedTabIndex = activeTabIndex,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            contentColor = primaryColor,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTabIndex]),
                    color = primaryColor,
                )
            },
        ) {
            Tab(
                selected = activeTabIndex == 0,
                onClick = { onTabSelected(0) },
                text = { Text("التفسير") },
            )
            Tab(
                selected = activeTabIndex == 1,
                onClick = { onTabSelected(1) },
                text = { Text("المعاني") },
            )
        }

        when {
            state.isVerseDetailsLoading -> LoadingView(modifier = Modifier.fillMaxWidth())
            details == null -> state.verseDetailsErrorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            activeTabIndex == 0 -> DetailsBody(text = stripHtml(details.tafseerText))
            else -> DetailsBody(text = stripHtml(details.maanyText))
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun AudioPlayButton(
    state: com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.MushafUiState,
    onPlayAudio: (String, String) -> Unit,
    onStopAudio: () -> Unit,
    onLoadAudio: () -> Unit,
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(
                width = 1.5.dp,
                color = if (state.isAudioPlaying) Color(0xFF4CAF50) else primaryColor,
                shape = CircleShape,
            )
            .clickable {
                if (state.isAudioPlaying) {
                    onStopAudio()
                } else if (state.availableReciters.isNotEmpty()) {
                    val firstReciter = state.availableReciters.first()
                    onPlayAudio(firstReciter.url, firstReciter.name)
                } else if (!state.isAudioLoading) {
                    onLoadAudio()
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (state.isAudioLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = primaryColor,
            )
        } else if (state.isAudioPlaying) {
            Icon(
                imageVector = Icons.Rounded.Stop,
                contentDescription = "إيقاف",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(20.dp),
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "تشغيل",
                tint = primaryColor,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun CopyAyahButton(
    surahNo: Int,
    verseNo: Int,
    verseContent: String?,
    context: Context,
    primaryColor: Color,
) {
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(shape)
            .border(width = 1.5.dp, color = primaryColor.copy(alpha = 0.5f), shape = shape)
            .clickable {
                verseContent?.let { content ->
                    val surahName = getSurahNameArabic(surahNo)
                    val copyText = "{ $content } ( $verseNo ) [ $surahName ]"
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("ayah", copyText))
                    Toast.makeText(context, "تم نسخ الآية", Toast.LENGTH_SHORT).show()
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.ContentCopy,
            contentDescription = "نسخ الآية",
            tint = primaryColor,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun RecitersRow(
    reciters: List<AudioReciterItem>,
    currentPlayingReciter: String?,
    isAudioPlaying: Boolean,
    onReciterClick: (AudioReciterItem) -> Unit,
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "اختر القارئ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )

        reciters.forEach { reciter ->
            val isPlaying = isAudioPlaying && currentPlayingReciter == reciter.name

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = if (isPlaying) 2.dp else 1.dp,
                        color = if (isPlaying) Color(0xFF4CAF50) else primaryColor.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .clickable { onReciterClick(reciter) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (isPlaying) {
                        AudioWaveAnimation()
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp),
                        )
                    }

                    Text(
                        text = reciter.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isPlaying) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioWaveAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")

    val bar1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bar1",
    )
    val bar2 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bar2",
    )
    val bar3 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bar3",
    )

    Row(
        modifier = Modifier.height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp * bar1)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF4CAF50))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp * bar2)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF4CAF50))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp * bar3)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF4CAF50))
        )
    }
}

@Composable
private fun DetailsBody(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    )
}

@Composable
private fun MushafTopBar(
    currentPage: Int,
    totalPages: Int,
    onBack: () -> Unit,
    onPageClick: () -> Unit,
) {
    Surface(
        color = Color.Black,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.quran_back),
                    tint = Color.White,
                    modifier = Modifier.size(28.dp),
                )
            }

            Text(
                text = "صفحة $currentPage / $totalPages",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier
                    .clickable { onPageClick() }
                    .padding(horizontal = 12.dp),
            )
        }
    }
}

@Composable
private fun PageInputDialog(
    currentPage: Int,
    totalPages: Int,
    onDismiss: () -> Unit,
    onGoToPage: (Int) -> Unit,
) {
    var pageText by remember { mutableStateOf(currentPage.toString()) }
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp)
                .clickable {},
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "انتقل إلى صفحة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = pageText,
                onValueChange = { pageText = it.filter { c -> c.isDigit() } },
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    textAlign = TextAlign.Center,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 12.dp),
            )

            Text(
                text = "من $totalPages صفحة",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onDismiss() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "إلغاء",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(primaryColor)
                        .clickable {
                            pageText.toIntOrNull()?.let { onGoToPage(it) }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "انتقال",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

private fun Int.toArabicNumerals(): String {
    val arabicDigits = charArrayOf(
        '\u0660', '\u0661', '\u0662', '\u0663', '\u0664',
        '\u0665', '\u0666', '\u0667', '\u0668', '\u0669'
    )
    return this.toString().map { arabicDigits[it - '0'] }.joinToString("")
}

private fun getSurahNameArabic(surahNo: Int): String {
    val names = arrayOf(
        "", "الفاتحة", "البقرة", "آل عمران", "النساء", "المائدة",
        "الأنعام", "الأعراف", "الأنفال", "التوبة", "يونس",
        "هود", "يوسف", "الرعد", "إبراهيم", "الحجر", "النحل",
        "الإسراء", "الكهف", "مريم", "طه", "الأنبياء", "الحج",
        "المؤمنون", "النور", "الفرقان", "الشعراء", "النمل",
        "القصص", "العنكبوت", "الروم", "لقمان", "السجدة", "الأحزاب",
        "سبأ", "فاطر", "يس", "الصافات", "ص", "الزمر",
        "غافر", "فصلت", "الشورى", "الزخرف", "الدخان", "الجاثية",
        "الأحقاف", "محمد", "الفتح", "الحجرات", "ق", "الذاريات",
        "الطور", "النجم", "القمر", "الرحمن", "الواقعة", "الحديد",
        "المجادلة", "الحشر", "الممتحنة", "الصف", "الجمعة",
        "المنافقون", "التغابن", "الطلاق", "التحريم", "الملك",
        "القلم", "الحاقة", "المعارج", "نوح", "الجن", "المزمل",
        "المدثر", "القيامة", "الإنسان", "المرسلات", "النبأ",
        "النازعات", "عبس", "التكوير", "الانفطار", "المطففين",
        "الانشقاق", "البروج", "الطارق", "الأعلى", "الغاشية",
        "الفجر", "البلد", "الشمس", "الليل", "الضحى", "الشرح",
        "التين", "العلق", "القدر", "البينة", "الزلزلة",
        "العاديات", "القارعة", "التكاثر", "العصر", "الهمزة",
        "الفيل", "قريش", "الماعون", "الكوثر", "الكافرون",
        "النصر", "المسد", "الإخلاص", "الفلق", "الناس"
    )
    return names.getOrElse(surahNo) { "" }
}

private fun stripHtml(text: String): String {
    return text.replace(Regex("<[^>]*>"), "").trim()
}
