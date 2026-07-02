package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.mushaf

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.MushafUiState

/**
 * Renders the HorizontalPager with mushaf page content.
 * Pages are pre-loaded via MushafViewModel.preloadAdjacentPages() so
 * adjacent pages are cached in loadedPages and display immediately.
 */
@Composable
fun MushafPageContent(
    pagerState: PagerState,
    uiState: MushafUiState,
    selectedAyahId: Int?,
    onPageTap: () -> Unit,
    onAyahLongClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            beyondViewportPageCount = 2,
        ) { pageIndex ->
            val pageNum = pageIndex + 1
            key(pageNum) {
                MushafSinglePage(
                    pageNum = pageNum,
                    uiState = uiState,
                    selectedAyahId = selectedAyahId,
                    onPageTap = onPageTap,
                    onAyahLongClick = onAyahLongClick,
                )
            }
        }
    }
}

@Composable
private fun MushafSinglePage(
    pageNum: Int,
    uiState: MushafUiState,
    selectedAyahId: Int?,
    onPageTap: () -> Unit,
    onAyahLongClick: (Int) -> Unit,
) {
    val scrollState = rememberScrollState()
    val cachedPage = uiState.loadedPages[pageNum]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onPageTap() }
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center,
    ) {
        when {
            cachedPage != null -> {
                MushafPageView(
                    page = cachedPage,
                    fontSize = 22f,
                    selectedAyahId = selectedAyahId,
                    onAyahClick = { onPageTap() },
                    onAyahLongClick = onAyahLongClick,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 120.dp),
                )
            }

            else -> Box(modifier = Modifier)
        }
    }
}
