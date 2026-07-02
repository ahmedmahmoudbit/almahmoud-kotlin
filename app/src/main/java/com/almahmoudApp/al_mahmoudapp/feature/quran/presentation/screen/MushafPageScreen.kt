package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.screen

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.mushaf.MushafPageContent
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.mushaf.MushafTopBar
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.mushaf.PageInputDialog
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.mushaf.verse.VerseDetailsSheet
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
    initialPage: Int = 0,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MushafViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // UI-only state
    var controlsVisible by remember { mutableStateOf(true) }
    var showPageInput by remember { mutableStateOf(false) }
    var selectedAyahId by remember { mutableStateOf<Int?>(null) }
    var showVerseDetails by remember { mutableStateOf(false) }
    var selectedSurahNo by remember { mutableIntStateOf(0) }
    var selectedVerseNo by remember { mutableIntStateOf(0) }
    var activeTabIndex by remember { mutableIntStateOf(0) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // MediaPlayer lifecycle — kept here intentionally so it is tied to the
    // Composable's lifecycle and automatically cleaned up via DisposableEffect.
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Release player when screen leaves composition
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
            viewModel.stopAudio()
        }
    }

    // React to audio URL changes from ViewModel
    LaunchedEffect(state.currentAudioUrl) {
        val url = state.currentAudioUrl
        if (url != null && state.isAudioPlaying) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(url)
                    setOnPreparedListener { it.start() }
                    setOnCompletionListener { viewModel.onAudioCompleted() }
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

    // Sync pager → ViewModel
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collectLatest { page ->
            viewModel.loadPage(page + 1)
            selectedAyahId = null
        }
    }

    // Navigate to specific surah or page on first load
    LaunchedEffect(surahNumber) {
        if (surahNumber != null && surahNumber > 0) {
            viewModel.navigateToSurah(surahNumber)
        } else if (initialPage > 0) {
            viewModel.goToPage(initialPage)
        }
    }

    // Sync ViewModel page → pager (e.g. after goToPage())
    LaunchedEffect(state.currentPage) {
        if (state.currentPage > 0 && pagerState.currentPage != state.currentPage - 1) {
            pagerState.scrollToPage(state.currentPage - 1)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.background,
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {

                // Main pager content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                ) {
                    MushafPageContent(
                        pagerState = pagerState,
                        uiState = state,
                        selectedAyahId = selectedAyahId,
                        onPageTap = { controlsVisible = !controlsVisible },
                        onAyahLongClick = { ayahId ->
                            val (surahNo, verseNo) = QuranConstants
                                .getChapterAndVerseFromAyahId(ayahId)
                            selectedAyahId = ayahId
                            selectedSurahNo = surahNo
                            selectedVerseNo = verseNo
                            showVerseDetails = true
                            viewModel.loadVerseDetails(surahNo, verseNo)
                            viewModel.loadVerseContent(surahNo, verseNo)
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                // Animated top bar
                MushafTopBar(
                    currentPage = pagerState.currentPage + 1,
                    totalPages = state.totalPages,
                    visible = controlsVisible,
                    onBack = onBack,
                    onPageClick = { showPageInput = true },
                    modifier = Modifier.align(Alignment.TopCenter),
                )

                // Page jump dialog
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

                // Verse details bottom sheet
                if (showVerseDetails) {
                    VerseDetailsSheet(
                        surahNo = selectedSurahNo,
                        verseNo = selectedVerseNo,
                        uiState = state,
                        activeTabIndex = activeTabIndex,
                        sheetState = bottomSheetState,
                        onDismiss = {
                            showVerseDetails = false
                            viewModel.stopAudio()
                        },
                        onTabSelected = { activeTabIndex = it },
                        onPlayAudio = { url, name -> viewModel.playAudio(url, name) },
                        onStopAudio = { viewModel.stopAudio() },
                        onLoadAudio = {
                            viewModel.loadVerseAudio(selectedSurahNo, selectedVerseNo)
                        },
                    )
                }
            }
        }
    }
}
