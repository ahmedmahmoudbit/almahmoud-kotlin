package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.mushaf.verse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.MushafUiState
import com.almahmoudApp.al_mahmoudapp.feature.quran.util.stripHtml

/**
 * Tab bar (التفسير / المعاني) and the associated content body.
 * stripHtml is called inside derivedStateOf so it only reruns when the
 * underlying text changes, not on every recomposition.
 */
@Composable
fun VerseDetailsBody(
    uiState: MushafUiState,
    activeTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val details = uiState.selectedVerseDetails

    // Cache expensive stripHtml results — only recalculated when text changes
    val tafseerText = remember(details?.tafseerText) {
        details?.tafseerText?.let { stripHtml(it) }.orEmpty()
    }
    val maanyText = remember(details?.maanyText) {
        details?.maanyText?.let { stripHtml(it) }.orEmpty()
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
        modifier = modifier,
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
        uiState.isVerseDetailsLoading -> LoadingView(modifier = Modifier.fillMaxWidth())
        details == null -> uiState.verseDetailsErrorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        activeTabIndex == 0 -> DetailsText(text = tafseerText)
        else -> DetailsText(text = maanyText)
    }
}

@Composable
private fun DetailsText(text: String) {
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
fun AudioLoadingIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "جاري تحميل القراء...",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
    }
}
