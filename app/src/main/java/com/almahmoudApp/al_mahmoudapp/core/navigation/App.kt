package com.almahmoudApp.al_mahmoudapp.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.almahmoudApp.al_mahmoudapp.core.ui.components.AppBottomBar
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import dev.chrisbanes.haze.HazeState

@Composable
fun App(
    viewModel: AppViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
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
                    )
                }
            },
        ) { innerPadding ->
            AppNavHost(
                innerPadding = innerPadding,
                startDestination = startDestination,
                navController = navController,
                hazeState = hazeState,
            )
        }
    }
}

private val BOTTOM_BAR_ROUTES = setOf(
    AppDestination.Home.route,
    AppDestination.Quran.route,
    AppDestination.Settings.route,
)
