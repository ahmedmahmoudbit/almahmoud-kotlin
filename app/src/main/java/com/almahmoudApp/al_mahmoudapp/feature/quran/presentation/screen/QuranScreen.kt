package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.EmptyView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlass
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassDefaults
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.liquidSource
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranReader
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranSurah
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.QuranUiState
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.QuranViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

@Composable
fun QuranRoute(
    contentPadding: PaddingValues,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    viewModel: QuranViewModel = hiltViewModel(),
    onSurahSelected: (QuranSurah) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    QuranScreen(
        state = state,
        contentPadding = contentPadding,
        hazeState = hazeState,
        modifier = modifier,
        onQueryChange = viewModel::onQueryChange,
        onSurahSelected = onSurahSelected,
    )
}

@Composable
fun QuranScreen(
    state: QuranUiState,
    contentPadding: PaddingValues,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit,
    onSurahSelected: (QuranSurah) -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .haze(state = hazeState),
        color = MaterialTheme.colorScheme.background,
    ) {
        when {
            state.isLoading -> LoadingView()
            state.errorMessage != null -> ErrorView(message = state.errorMessage)
            state.content == null -> EmptyView()
            else -> QuranContent(
                state = state,
                contentPadding = contentPadding,
                onQueryChange = onQueryChange,
                onSurahSelected = onSurahSelected,
            )
        }
    }
}

@Composable
private fun QuranContent(
    state: QuranUiState,
    contentPadding: PaddingValues,
    onQueryChange: (String) -> Unit,
    onSurahSelected: (QuranSurah) -> Unit,
) {
    LiquidHost(modifier = Modifier.fillMaxSize()) {
        QuranBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            QuranHeader()
            Spacer(modifier = Modifier.height(14.dp))
            QuranSearchBar(
                query = state.query,
                onQueryChange = onQueryChange,
            )
            Spacer(modifier = Modifier.height(14.dp))
            QuranSurahGrid(
                surahs = state.filteredSurahs,
                onSurahSelected = onSurahSelected,
            )
        }
    }
}

@Composable
private fun QuranBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .liquidSource()
            .background(MaterialTheme.colorScheme.background),
    )
    AsyncImage(
        model = R.drawable.home_mosque_skyline,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .liquidSource()
            .alpha(0.18f),
    )
}

@Composable
private fun QuranHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.MenuBook,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(42.dp),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.quran_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.quran_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun QuranSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
        },
        placeholder = {
            Text(text = stringResource(R.string.quran_search_surah))
        },
        singleLine = true,
        shape = RoundedCornerShape(22.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun QuranSurahGrid(
    surahs: List<QuranSurah>,
    onSurahSelected: (QuranSurah) -> Unit,
) {
    if (surahs.isEmpty()) {
        EmptyView()
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(surahs) { surah ->
            QuranSurahCard(
                surah = surah,
                onClick = { onSurahSelected(surah) },
            )
        }
    }
}

@Composable
private fun QuranSurahCard(
    surah: QuranSurah,
    onClick: () -> Unit,
) {
    LiquidGlass(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = LiquidGlassDefaults.CardShape,
        style = LiquidGlassDefaults.Soft,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = surah.number.toString(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = surah.nameArabic,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                    )
                    if (surah.nameEnglish.isNotBlank() && surah.nameEnglish != surah.nameArabic) {
                        Text(
                            text = surah.nameEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.End,
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuranInfoChip(text = "${surah.versesCount} ${stringResource(R.string.quran_verses)}")
                QuranInfoChip(text = "${stringResource(R.string.quran_page)} ${surah.pageNumber}")
            }
        }
    }
}

@Composable
private fun QuranInfoChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
