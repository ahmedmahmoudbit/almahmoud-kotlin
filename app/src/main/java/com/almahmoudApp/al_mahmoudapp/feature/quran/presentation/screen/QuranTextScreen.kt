package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.screen

import AmiriFont
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.EmptyView
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading.QuranContent
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading.QuranError
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading.QuranLoading
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading.QuranVerseDetailsSheet
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.QuranTextViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

private const val SURAH_AT_TAWBAH = 9
private const val SURAH_AL_FATIHAH = 1
private const val MIN_FONT_SIZE = 22
private const val MAX_FONT_SIZE = 42
private const val FONT_STEP = 2
private const val DEFAULT_FONT_SIZE = 28

/**
 * Quran reading screen — text-only (no audio). Owns only screen-level state: controls
 * visibility, font size, selected-verse details tab. All UI is delegated to dedicated
 * reading components under `components/reading`. Mirrors the inline mushaf reading
 * experience of the Flutter reference app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranTextScreen(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    surahNumber: Int,
    page: Int,
    surahName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QuranTextViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var controlsVisible by rememberSaveable { mutableStateOf(true) }
    var fontSize by rememberSaveable { mutableIntStateOf(DEFAULT_FONT_SIZE) }
    var activeTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Surface(
        modifier = modifier
            .fillMaxSize()
            .haze(state = hazeState),
        color = MaterialTheme.colorScheme.background,
    ) {
        // Lock the whole reading screen to RTL so Arabic text and layout are never
        // inverted by the device's default (LTR) layout direction.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> QuranLoading()
                    state.errorMessage != null -> QuranError(
                        message = state.errorMessage,
                        onRetry = viewModel::retry,
                    )
                    state.verses.isEmpty() -> EmptyView()
                    else -> ReadingBody(
                        contentPadding = contentPadding,
                        surahNumber = surahNumber,
                        page = page,
                        surahName = surahName,
                        fontSize = fontSize.toFloat(),
                        verses = state.verses,
                        selectedVerse = state.selectedVerse,
                        listState = listState,
                        controlsVisible = controlsVisible,
                        onBack = onBack,
                        onToggleFont = {
                            val next = fontSize + FONT_STEP
                            fontSize = if (next > MAX_FONT_SIZE) MIN_FONT_SIZE else next
                        },
                        onToggleControls = { controlsVisible = !controlsVisible },
                        onVerseSelected = { verse ->
                            controlsVisible = false
                            viewModel.onVerseSelected(verse)
                        },
                    )
                }

                if (state.selectedVerse != null) {
                    ModalBottomSheet(
                        onDismissRequest = viewModel::dismissVerseDetails,
                        sheetState = bottomSheetState,
                    ) {
                        QuranVerseDetailsSheet(
                            state = state,
                            activeTabIndex = activeTabIndex,
                            onTabSelected = { activeTabIndex = it },
                        )
                    }
                }

                if (!controlsVisible && state.verses.isNotEmpty() && state.selectedVerse == null) {
                    FloatingActionButton(
                        onClick = { controlsVisible = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                    ) {
                        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadingBody(
    contentPadding: PaddingValues,
    surahNumber: Int,
    page: Int,
    surahName: String,
    fontSize: Float,
    verses: List<com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse>,
    selectedVerse: com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse?,
    listState: androidx.compose.foundation.lazy.LazyListState,
    controlsVisible: Boolean,
    onBack: () -> Unit,
    onToggleFont: () -> Unit,
    onToggleControls: () -> Unit,
    onVerseSelected: (com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        AnimatedVisibility(
            visible = controlsVisible,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
        ) {
            QuranReadingTopBar(
                title = surahName.ifBlank { stringResource(R.string.quran_title) },
                onBack = onBack,
                onToggleFont = onToggleFont,
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        QuranContent(
            verses = verses.filterNot { surahNumber == SURAH_AL_FATIHAH && it.verseNumber == 1 },
            surahName = surahName,
            page = page,
            fontSize = fontSize,
            showBasmala = surahNumber != SURAH_AT_TAWBAH,
            selectedVerse = selectedVerse,
            listState = listState,
            onVerseSelected = onVerseSelected,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/** Minimal reading top bar: back, surah title, font-size toggle. */
@Composable
private fun QuranReadingTopBar(
    title: String,
    onBack: () -> Unit,
    onToggleFont: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TopBarButton(
            onClick = onBack,
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(R.string.quran_back),
        )
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = AmiriFont,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        TopBarButton(
            onClick = onToggleFont,
            icon = Icons.Rounded.FormatSize,
            contentDescription = stringResource(R.string.quran_control_font),
        )
    }
}

@Composable
private fun TopBarButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape),
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}
