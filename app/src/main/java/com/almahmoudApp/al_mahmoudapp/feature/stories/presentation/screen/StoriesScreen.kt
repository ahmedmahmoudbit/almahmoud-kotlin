package com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.foundation.verticalScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.components.StoryCard
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.state.StoriesUiState
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.state.StoryDetailsUiState
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.viewmodel.StoryDetailsViewModel
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.viewmodel.StoriesViewModel

@Composable
fun StoriesRoute(
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: StoriesViewModel = hiltViewModel(),
    onStorySelected: (Int) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    StoriesScreen(
        state = state,
        contentPadding = contentPadding,
        modifier = modifier,
        onStorySelected = onStorySelected,
    )
}

@Composable
fun StoriesScreen(
    state: StoriesUiState,
    contentPadding: PaddingValues,
    onStorySelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = stringResource(R.string.stories_title),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(modifier = Modifier.height(12.dp))
                when {
                    state.isLoading -> LoadingView()
                    state.errorMessage != null -> ErrorView(message = state.errorMessage)
                    else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(state.stories.withIndex().toList(), key = { it.index }) { indexedStory ->
                            StoryCard(
                                story = indexedStory.value,
                                onClick = { onStorySelected(indexedStory.index) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoryDetailsRoute(
    contentPadding: PaddingValues,
    storyIndex: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: StoryDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(storyIndex) {
        viewModel.load(storyIndex)
    }
    StoryDetailsScreen(
        state = state,
        contentPadding = contentPadding,
        modifier = modifier,
        onBack = onBack,
    )
}

@Composable
fun StoryDetailsScreen(
    state: StoryDetailsUiState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> LoadingView()
                state.errorMessage != null -> ErrorView(message = state.errorMessage)
                state.story == null -> ErrorView(message = stringResource(R.string.error_view_message))
                else -> Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = state.story.imageUrl,
                        contentDescription = state.story.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(28.dp)
                            .alpha(0.25f),
                        contentScale = ContentScale.Crop,
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(contentPadding)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = null,
                            )
                        }
                        Text(
                            text = stringResource(R.string.stories_details_title),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AsyncImage(
                            model = state.story.imageUrl,
                            contentDescription = state.story.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.story.title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = state.story.body,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}
