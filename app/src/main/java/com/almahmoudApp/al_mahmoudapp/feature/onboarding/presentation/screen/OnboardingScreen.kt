package com.almahmoudApp.al_mahmoudapp.feature.onboarding.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.almahmoudApp.al_mahmoudapp.core.ui.components.EmptyView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.ErrorView
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.core.theme.Typography
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.presentation.components.OnboardingPageContent
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.presentation.state.OnboardingUiState
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.presentation.viewmodel.OnboardingViewModel

@Composable
fun OnboardingRoute(
    contentPadding: PaddingValues,
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            onCompleted()
        }
    }

    OnboardingScreen(
        state = state,
        contentPadding = contentPadding,
        onCompleteClick = viewModel::completeOnboarding,
        modifier = modifier,
    )
}

@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    contentPadding: PaddingValues,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MaterialTheme(
        colorScheme = OnboardingLightColorScheme,
        typography = Typography,
    ) {
        Box(modifier = modifier.fillMaxSize().background(Color.White)) {
            when {
                state.isLoading -> LoadingView()
                state.errorMessage != null -> ErrorView(message = state.errorMessage)
                state.pages.isEmpty() -> EmptyView()
                else -> OnboardingContent(
                    state = state,
                    contentPadding = contentPadding,
                    onCompleteClick = onCompleteClick,
                )
            }
        }
    }
}

@Composable
private fun OnboardingContent(
    state: OnboardingUiState,
    contentPadding: PaddingValues,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentPage by rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }
    val isLastPage = currentPage == state.pages.lastIndex
    var dragAmount by rememberSaveable { androidx.compose.runtime.mutableFloatStateOf(0f) }

    fun moveNext() {
        currentPage = (currentPage + 1).coerceAtMost(state.pages.lastIndex)
    }

    fun movePrevious() {
        currentPage = (currentPage - 1).coerceAtLeast(0)
    }

    AnimatedContent(
        targetState = currentPage,
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .pointerInput(state.pages.size) {
                detectHorizontalDragGestures(
                    onDragStart = { dragAmount = 0f },
                    onHorizontalDrag = { _, dragDelta ->
                        dragAmount += dragDelta
                    },
                    onDragEnd = {
                        when {
                            dragAmount <= -SwipeThreshold -> moveNext()
                            dragAmount >= SwipeThreshold -> movePrevious()
                        }
                        dragAmount = 0f
                    },
                    onDragCancel = { dragAmount = 0f },
                )
            },
        transitionSpec = {
            val direction = if (targetState > initialState) 1 else -1
            (
                slideInHorizontally(
                    animationSpec = tween(430),
                    initialOffsetX = { fullWidth -> fullWidth * direction / 2 },
                ) + fadeIn(animationSpec = tween(260))
                ) togetherWith (
                slideOutHorizontally(
                    animationSpec = tween(360),
                    targetOffsetX = { fullWidth -> -fullWidth * direction / 3 },
                ) + fadeOut(animationSpec = tween(220))
                )
        },
        label = "onboarding_page",
    ) { pageIndex ->
        Box(modifier = Modifier.fillMaxSize()) {
            OnboardingPageContent(
                page = state.pages[pageIndex],
                pageCount = state.pages.size,
                selectedPage = pageIndex,
                isLastPage = isLastPage,
                onNextClick = ::moveNext,
                onCompleteClick = onCompleteClick,
            )
        }
    }
}

private val OnboardingLightColorScheme = lightColorScheme(
    primary = Color(0xFF2FA084),
    onPrimary = Color.White,
    secondary = Color(0xFF5D675F),
    tertiary = Color(0xFF7B5735),
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Color(0xFFE0E7DD),
    onSurface = Color(0xFF191C1A),
)

private const val SwipeThreshold = 72f
