package com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.verticalScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidHost
import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.model.StoryItem
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.components.StoryCard
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.state.StoriesUiState
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.state.StoryDetailsUiState
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.viewmodel.StoryDetailsViewModel
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.viewmodel.StoriesViewModel
import LiquidGlassCard
import kotlinx.coroutines.delay

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
        onStorySelected = { story ->
            val index = viewModel.getOriginalIndex(story)
            onStorySelected(index)
        },
        onToggleFavoriteFilter = viewModel::toggleFavoriteFilter,
        onToggleSearchActive = viewModel::toggleSearchActive,
        onSearchQueryChange = viewModel::setSearchQuery,
        onToggleFavoriteStory = viewModel::toggleFavoriteStory
    )
}

@Composable
fun StoriesScreen(
    state: StoriesUiState,
    contentPadding: PaddingValues,
    onStorySelected: (StoryItem) -> Unit,
    onToggleFavoriteFilter: () -> Unit,
    onToggleSearchActive: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleFavoriteStory: (StoryItem) -> Unit,
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
                // Customized Header with Glass Actions (Search and Favorite Filtering)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.stories_title),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Glass Search Trigger Button
                        LiquidGlassCard(
                            onClick = onToggleSearchActive,
                            modifier = Modifier.size(42.dp),
                            cornerRadius = 12.dp
                        ) {
                            Icon(
                                imageVector = if (state.isSearchActive) Icons.Filled.Search else Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = if (state.isSearchActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Glass Favorites Filter Toggle Button
                        LiquidGlassCard(
                            onClick = onToggleFavoriteFilter,
                            modifier = Modifier.size(42.dp),
                            cornerRadius = 12.dp
                        ) {
                            Icon(
                                imageVector = if (state.showOnlyFavorites) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorites Only",
                                tint = if (state.showOnlyFavorites) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Animated Collapsible Search Bar
                AnimatedVisibility(
                    visible = state.isSearchActive,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        placeholder = { Text("بحث عن قصة...") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                when {
                    state.isLoading -> LoadingView()
                    state.errorMessage != null -> ErrorView(message = state.errorMessage)
                    else -> {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 24.dp),
                            verticalItemSpacing = 12.dp,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Featured Story Section ("اخترنا لك" header and featured card span full line)
                            // Display only when search is empty and favorites filter is disabled
                            if (state.featuredStory != null && !state.showOnlyFavorites && state.searchQuery.isBlank()) {
                                item(span = StaggeredGridItemSpan.FullLine) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                    ) {
                                        Text(
                                            text = "اخترنا لك",
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        val isFeaturedFav = state.favoriteStories.contains(state.featuredStory.title)
                                        StoryCard(
                                            story = state.featuredStory,
                                            isFavorite = isFeaturedFav,
                                            onFavoriteClick = { onToggleFavoriteStory(state.featuredStory) },
                                            onClick = { onStorySelected(state.featuredStory) },
                                            height = 230.dp
                                        )
                                    }
                                }

                                item(span = StaggeredGridItemSpan.FullLine) {
                                    Text(
                                        text = "باقي القصص",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                    )
                                }
                            }

                            // Dynamic Pinterest-style masonry grid items
                            itemsIndexed(state.stories) { index, story ->
                                val isStoryFav = state.favoriteStories.contains(story.title)
                                // Programmatically vary card heights for staggered effect
                                val height = if (index % 3 == 0) 230.dp else if (index % 3 == 1) 180.dp else 260.dp
                                StoryCard(
                                    story = story,
                                    isFavorite = isStoryFav,
                                    onFavoriteClick = { onToggleFavoriteStory(story) },
                                    onClick = { onStorySelected(story) },
                                    height = height
                                )
                            }
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
        onFavoriteClick = viewModel::toggleFavorite,
        onSpeedSelected = viewModel::setAutoScrollSpeed,
    )
}

@Composable
fun StoryDetailsScreen(
    state: StoryDetailsUiState,
    contentPadding: PaddingValues,
    onBack: () -> Unit,
    onFavoriteClick: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showSpeedMenu by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Smooth auto-scroll implementation
    if (state.autoScrollSpeed > 0f) {
        LaunchedEffect(state.autoScrollSpeed) {
            while (true) {
                scrollState.scrollBy(state.autoScrollSpeed)
                delay(16) // ~60fps smooth scrolling
            }
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LiquidHost(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> LoadingView()
                state.errorMessage != null -> ErrorView(message = state.errorMessage)
                state.story == null -> ErrorView(message = stringResource(R.string.error_view_message))
                else -> Box(modifier = Modifier.fillMaxSize()) {
                    // Main Scrollable Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // Header section with grayscale desaturated and blurred hero image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            val colorMatrix = remember {
                                ColorMatrix().apply { setToSaturation(0f) }
                            }
                            AsyncImage(
                                model = state.story.imageUrl,
                                contentDescription = state.story.title,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .blur(16.dp)
                                    .alpha(0.6f),
                                contentScale = ContentScale.Crop,
                                colorFilter = ColorFilter.colorMatrix(colorMatrix)
                            )
                            // Elegant overlay gradient to blend bottom of hero image to background
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                                MaterialTheme.colorScheme.background
                                            )
                                        )
                                    )
                            )
                        }

                        // Overlapping Foreground Circular Image with Border & Spacer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-50).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = state.story.imageUrl,
                                contentDescription = state.story.title,
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                        }

                        // Text Body Content Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-30).dp)
                                .padding(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = state.story.title,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 40.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = state.story.body,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 28.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            Spacer(modifier = Modifier.height(120.dp)) // Extra spacing at the bottom for comfortable reading and floating bar
                        }
                    }

                    // Floating Glass Controls (Back button on the start, Actions on the end)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp + contentPadding.calculateTopPadding(), start = 16.dp, end = 16.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back Button
                        LiquidGlassCard(
                            onClick = onBack,
                            modifier = Modifier.size(40.dp),
                            cornerRadius = 20.dp
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Right actions: Favorite & Auto-Scroll
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Auto-Scroll Speed Selector Button
                            Box {
                                LiquidGlassCard(
                                    onClick = { showSpeedMenu = true },
                                    modifier = Modifier.size(40.dp),
                                    cornerRadius = 20.dp
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.PlayArrow,
                                        contentDescription = "Auto Scroll",
                                        tint = if (state.autoScrollSpeed > 0f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                DropdownMenu(
                                    expanded = showSpeedMenu,
                                    onDismissRequest = { showSpeedMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("إيقاف التمرير") },
                                        onClick = {
                                            onSpeedSelected(0f)
                                            showSpeedMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("سرعة بطيئة (0.5x)") },
                                        onClick = {
                                            onSpeedSelected(0.5f)
                                            showSpeedMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("سرعة متوسطة (1.0x)") },
                                        onClick = {
                                            onSpeedSelected(1.0f)
                                            showSpeedMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("سرعة سريعة (2.0x)") },
                                        onClick = {
                                            onSpeedSelected(2.0f)
                                            showSpeedMenu = false
                                        }
                                    )
                                }
                            }

                            // Favorite Button
                            LiquidGlassCard(
                                onClick = onFavoriteClick,
                                modifier = Modifier.size(40.dp),
                                cornerRadius = 20.dp
                            ) {
                                Icon(
                                    imageVector = if (state.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (state.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
