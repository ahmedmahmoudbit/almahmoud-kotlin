package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.mushaf.verse

import AmiriFont
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.MushafUiState
import com.almahmoudApp.al_mahmoudapp.feature.quran.util.getSurahNameArabic
import com.almahmoudApp.al_mahmoudapp.feature.quran.util.toArabicNumerals

/**
 * ModalBottomSheet that shows verse tafseer, meanings, reciters and playback controls.
 * All expensive computations (surah name, arabic numerals) are cached with remember.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerseDetailsSheet(
    surahNo: Int,
    verseNo: Int,
    uiState: MushafUiState,
    activeTabIndex: Int,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onPlayAudio: (String, String) -> Unit,
    onStopAudio: () -> Unit,
    onLoadAudio: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        VerseDetailsContent(
            surahNo = surahNo,
            verseNo = verseNo,
            uiState = uiState,
            activeTabIndex = activeTabIndex,
            onTabSelected = onTabSelected,
            onPlayAudio = onPlayAudio,
            onStopAudio = onStopAudio,
            onLoadAudio = onLoadAudio,
        )
    }
}

@Composable
private fun VerseDetailsContent(
    surahNo: Int,
    verseNo: Int,
    uiState: MushafUiState,
    activeTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onPlayAudio: (String, String) -> Unit,
    onStopAudio: () -> Unit,
    onLoadAudio: () -> Unit,
) {
    // Resolve once — stable as long as surahNo / verseNo don't change
    val surahName = remember(surahNo) { getSurahNameArabic(surahNo) }
    val arabicVerseNo = remember(verseNo) { verseNo.toArabicNumerals() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header row: title + action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "سورة $surahName - الآية $arabicVerseNo",
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
                    verseContent = uiState.selectedVerseContent,
                )

                AudioPlayButton(
                    state = uiState,
                    onPlayAudio = onPlayAudio,
                    onStopAudio = onStopAudio,
                    onLoadAudio = onLoadAudio,
                )
            }
        }

        // Reciters list — only shown when data is available
        if (uiState.availableReciters.isNotEmpty()) {
            RecitersSection(
                reciters = uiState.availableReciters,
                currentPlayingReciter = uiState.currentPlayingReciter,
                isAudioPlaying = uiState.isAudioPlaying,
                onReciterClick = { reciter -> onPlayAudio(reciter.url, reciter.name) },
            )
        }

        // Loading indicator for reciters
        if (uiState.isAudioLoading) {
            AudioLoadingIndicator()
        }

        // Tabs + body content
        VerseDetailsBody(
            uiState = uiState,
            activeTabIndex = activeTabIndex,
            onTabSelected = onTabSelected,
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}
