package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse

/**
 * The vertical reading surface. Stateless: all state (list state, font size, selection,
 * surah metadata) is hoisted to the screen. Renders the ornamental surah header once,
 * the basmala (when applicable), then the verses as a single flowing mushaf text block.
 */
@Composable
fun QuranContent(
    verses: List<QuranVerse>,
    surahName: String,
    page: Int,
    fontSize: Float,
    showBasmala: Boolean,
    selectedVerse: QuranVerse?,
    listState: LazyListState,
    onVerseSelected: (QuranVerse) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 32.dp),
    ) {
        item(key = "quran_header") {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Surah name in the ornamental frame, then the basmala as plain centered
                // text below it, then the verses flow directly underneath.
                QuranSurahHeader(
                    surahName = surahName,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (showBasmala) {
                    QuranBasmala()
                }
//                QuranMushafText(
//                    verses = verses,
//                    fontSize = fontSize,
//                    selectedVerse = selectedVerse,
//                    onVerseLongClick = onVerseSelected,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 4.dp),
//                )
            }
        }
    }
}
