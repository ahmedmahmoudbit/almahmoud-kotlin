package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading

import AmiriFont
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.QuranTextUiState

/**
 * Bottom sheet content showing tafseer / maany for the selected verse, with two tabs.
 * Reads the selected verse from [state] and renders the appropriate tab body.
 */
@Composable
fun QuranVerseDetailsSheet(
    state: QuranTextUiState,
    activeTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    val selectedVerse = state.selectedVerse ?: return
    val details = state.selectedVerseDetails

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "سورة ${selectedVerse.surahNumber.toArabicNumerals()} - الآية ${selectedVerse.verseNumber.toArabicNumerals()}",
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = AmiriFont,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        ScrollableTabRow(
            selectedTabIndex = activeTabIndex,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTabIndex]),
                    color = MaterialTheme.colorScheme.primary,
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
