package com.almahmoudApp.al_mahmoudapp.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.almahmoudApp.al_mahmoudapp.core.theme.AlMahmoudTheme
import com.almahmoudApp.al_mahmoudapp.core.ui.components.AppBottomBar
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import dev.chrisbanes.haze.HazeState

@Composable
fun App(
    viewModel: AppViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    AlMahmoudTheme(themeMode = themeMode) {
        if (state.isLoading) {
            LoadingView()
        } else {
            val navController = rememberNavController()
            val startDestination = if (state.isOnboardingCompleted) {
                AppDestination.Home.route
            } else {
                AppDestination.Onboarding.route
            }

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val showBottomBar by remember(currentRoute) {
                derivedStateOf {
                    currentRoute in BOTTOM_BAR_ROUTES
                }
            }

            val hazeState = remember { HazeState() }
            var bottomBarHeightPx by remember { mutableIntStateOf(0) }
            val density = LocalDensity.current
            val bottomBarHeight = with(density) { bottomBarHeightPx.toDp() }

            Box(modifier = Modifier.fillMaxSize()) {
                AppNavHost(
                    innerPadding = PaddingValues(0.dp),
                    startDestination = startDestination,
                    navController = navController,
                    hazeState = hazeState,
                )

                if (showBottomBar) {
                    AppBottomBar(
                        currentRoute = currentRoute.orEmpty(),
                        hazeState = hazeState,
                        onNavigate = { route ->
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    popUpTo(AppDestination.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .onSizeChanged { size ->
                                bottomBarHeightPx = size.height
                            },
                    )
                }
            }
        }
    }
}

private val BOTTOM_BAR_ROUTES = setOf(
    AppDestination.Home.route,
    AppDestination.Quran.route,
    AppDestination.Status.route,
    AppDestination.Settings.route,
)
