package com.almahmoudApp.al_mahmoudapp.core.navigation
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.screen.AyatRoute
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.screen.AyatSoundRoute
import com.almahmoudApp.al_mahmoudapp.feature.home.presentation.screen.HomeRoute
import com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.screen.PrayerRoute
import com.almahmoudApp.al_mahmoudapp.feature.onboarding.presentation.screen.OnboardingRoute
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.screen.QuranActionRoute
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.screen.QuranAudioRoute
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.screen.QuranReadersRoute
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.screen.QuranTextScreen
import com.almahmoudApp.al_mahmoudapp.feature.qotof.presentation.screen.QotofRoute
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.screen.StoryDetailsRoute
import com.almahmoudApp.al_mahmoudapp.feature.stories.presentation.screen.StoriesRoute
import com.almahmoudApp.al_mahmoudapp.feature.settings.presentation.screen.SettingsRoute
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeFeatureKey
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.QuranRoute
import com.example.almahmoud.doaa.DoaaRoute
import com.almahmoudApp.al_mahmoudapp.feature.azkar.presentation.screen.AzkarListRoute
import com.almahmoudApp.al_mahmoudapp.feature.azkar.presentation.screen.AzkarDetailsRoute
import com.almahmoudApp.al_mahmoudapp.feature.tamilat.presentation.screen.TamilatRoute
import com.almahmoudApp.al_mahmoudapp.feature.images.presentation.screen.ImagesRoute
import dev.chrisbanes.haze.HazeState

@Composable
fun AppNavHost(
    innerPadding: PaddingValues,
    startDestination: String,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    hazeState: HazeState = remember { HazeState() }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(200)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(200)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(200)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(200)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(200)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(200)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(200)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(200)
            )
        }
    ) {
        composable(AppDestination.Onboarding.route) {
            OnboardingRoute(
                contentPadding = innerPadding,
                onCompleted = {
                    navController.navigate(AppDestination.Home.route) {
                        popUpTo(AppDestination.Onboarding.route) {
                            inclusive = true
                        }
                    }
                },
            )
        }
        composable(AppDestination.Home.route) {
            HomeRoute(
                contentPadding = innerPadding,
                hazeState = hazeState,
                onFeatureSelected = { key ->
                    when (key) {
                        HomeFeatureKey.SOUND -> navController.navigate(AppDestination.Ayat.route)
                        HomeFeatureKey.QOTOF -> navController.navigate(AppDestination.Qotof.route)
                        HomeFeatureKey.STORIES -> navController.navigate(AppDestination.Stories.route)
                        HomeFeatureKey.PRAYER -> navController.navigate(AppDestination.Prayer.route)
                        HomeFeatureKey.DOAA -> navController.navigate(AppDestination.Doaa.route)
                        HomeFeatureKey.AZKAR -> navController.navigate(AppDestination.AzkarList.route)
                        HomeFeatureKey.TAMILAT -> navController.navigate(AppDestination.Tamilat.route)
                        HomeFeatureKey.IMAGES -> navController.navigate(AppDestination.Images.route)
                        else -> Unit
                    }
                },
            )
        }
        composable(AppDestination.Prayer.route) {
            PrayerRoute(
                contentPadding = innerPadding,
                hazeState = hazeState,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppDestination.Ayat.route) {
            AyatRoute(
                contentPadding = innerPadding,
                onTopicSelected = { topicId ->
                    navController.navigate(AppDestination.AyatSound.createRoute(topicId))
                },
            )
        }
        composable(
            route = AppDestination.AyatSound.route,
            arguments = listOf(navArgument("topicId") { type = NavType.IntType }),
        ) { backStackEntry ->
            AyatSoundRoute(
                contentPadding = innerPadding,
                topicId = backStackEntry.arguments?.getInt("topicId") ?: 0,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppDestination.Qotof.route) {
            QotofRoute(
                contentPadding = innerPadding,
                hazeState = hazeState,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppDestination.Tamilat.route) {
            TamilatRoute(
                contentPadding = innerPadding,
                hazeState = hazeState,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppDestination.Images.route) {
            ImagesRoute(
                contentPadding = innerPadding,
                hazeState = hazeState,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppDestination.Stories.route) {
            StoriesRoute(
                contentPadding = innerPadding,
                onStorySelected = { index ->
                    navController.navigate(AppDestination.StoriesDetails.createRoute(index))
                },
            )
        }
        composable(
            route = AppDestination.StoriesDetails.route,
            arguments = listOf(navArgument("index") { type = NavType.IntType }),
        ) { backStackEntry ->
            StoryDetailsRoute(
                contentPadding = innerPadding,
                storyIndex = backStackEntry.arguments?.getInt("index") ?: 0,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppDestination.Doaa.route) {
            DoaaRoute(contentPadding = innerPadding)
        }
        composable(AppDestination.AzkarList.route) {
            AzkarListRoute(
                contentPadding = innerPadding,
                onCategorySelected = { category ->
                    navController.navigate(AppDestination.AzkarDetails.createRoute(category.ordinal))
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = AppDestination.AzkarDetails.route,
            arguments = listOf(navArgument("category") { type = NavType.IntType }),
        ) { backStackEntry ->
            AzkarDetailsRoute(
                contentPadding = innerPadding,
                categoryOrdinal = backStackEntry.arguments?.getInt("category") ?: 0,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppDestination.Quran.route) {
            QuranRoute(
                contentPadding = innerPadding,
                hazeState = hazeState,
                onSurahSelected = { surah ->
                    navController.navigate(
                        AppDestination.QuranAction.createRoute(
                            surahNumber = surah.number,
                            page = surah.pageNumber,
                            name = surah.nameArabic,
                        )
                    )
                },
            )
        }
        composable(
            route = AppDestination.QuranAction.route,
            arguments = listOf(
                navArgument("surahNumber") { type = NavType.IntType },
                navArgument("page") { type = NavType.IntType },
                navArgument("name") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            QuranActionRoute(
                contentPadding = innerPadding,
                hazeState = hazeState,
                surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1,
                page = backStackEntry.arguments?.getInt("page") ?: 1,
                surahName = backStackEntry.arguments?.getString("name").orEmpty(),
                onBack = { navController.popBackStack() },
                onRead = { surahNumber, page, name ->
                    navController.navigate(
                        AppDestination.QuranText.createRoute(
                            surahNumber = surahNumber,
                            page = page,
                            name = name,
                        )
                    )
                },
                onAudio = { surahNumber, page, name ->
                    navController.navigate(
                        AppDestination.QuranReaders.createRoute(
                            surahNumber = surahNumber,
                            page = page,
                            name = name,
                        )
                    )
                },
            )
        }
        composable(
            route = AppDestination.QuranReaders.route,
            arguments = listOf(
                navArgument("surahNumber") { type = NavType.IntType },
                navArgument("page") { type = NavType.IntType },
                navArgument("name") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            QuranReadersRoute(
                contentPadding = innerPadding,
                hazeState = hazeState,
                surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1,
                page = backStackEntry.arguments?.getInt("page") ?: 1,
                surahName = backStackEntry.arguments?.getString("name").orEmpty(),
                onBack = { navController.popBackStack() },
                onReaderSelected = { readerName, readerImage, audioBaseUrl, page, _ ->
                    navController.navigate(
                        AppDestination.QuranAudio.createRoute(
                            readerName = readerName,
                            readerImage = readerImage,
                            audioBaseUrl = audioBaseUrl,
                            page = page,
                        )
                    )
                },
            )
        }
        composable(
            route = AppDestination.QuranText.route,
            arguments = listOf(
                navArgument("surahNumber") { type = NavType.IntType },
                navArgument("page") { type = NavType.IntType },
                navArgument("name") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            QuranTextScreen(
                contentPadding = innerPadding,
                hazeState = hazeState,
                surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1,
                page = backStackEntry.arguments?.getInt("page") ?: 1,
                surahName = backStackEntry.arguments?.getString("name").orEmpty(),
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = AppDestination.QuranAudio.route,
            arguments = listOf(
                navArgument("readerName") { type = NavType.StringType },
                navArgument("readerImage") { type = NavType.StringType },
                navArgument("audioBaseUrl") { type = NavType.StringType },
                navArgument("page") { type = NavType.IntType },
            ),
        ) { backStackEntry ->
            QuranAudioRoute(
                contentPadding = innerPadding,
                hazeState = hazeState,
                readerName = backStackEntry.arguments?.getString("readerName").orEmpty(),
                readerImage = backStackEntry.arguments?.getString("readerImage").orEmpty(),
                audioBaseUrl = backStackEntry.arguments?.getString("audioBaseUrl").orEmpty(),
                page = backStackEntry.arguments?.getInt("page") ?: 1,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppDestination.Settings.route) {
            SettingsRoute(
                contentPadding = innerPadding,
                hazeState = hazeState
            )
        }
    }
}
