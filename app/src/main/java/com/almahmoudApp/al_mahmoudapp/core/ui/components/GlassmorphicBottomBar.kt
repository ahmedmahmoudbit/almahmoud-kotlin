package com.almahmoudApp.al_mahmoudapp.core.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.navigation.AppDestination
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeChild

sealed class BottomBarTab(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector,
    val color: Color,
) {
    data object Home : BottomBarTab(
        route = AppDestination.Home.route,
        titleRes = R.string.nav_home,
        icon = Icons.Rounded.Home,
        color = Color(0xFF00C853),
    )

    data object Quran : BottomBarTab(
        route = AppDestination.Quran.route,
        titleRes = R.string.nav_quran,
        icon = Icons.AutoMirrored.Rounded.MenuBook,
        color = Color(0xFFFFB300),
    )

    data object Settings : BottomBarTab(
        route = AppDestination.Settings.route,
        titleRes = R.string.nav_settings,
        icon = Icons.Rounded.Settings,
        color = Color(0xFF0288D1),
    )
}

@Composable
fun GlassmorphicBottomBar(
    navController: NavController,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val tabs = remember {
        listOf(BottomBarTab.Home, BottomBarTab.Quran, BottomBarTab.Settings)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val selectedTabIndex = remember(currentRoute) {
        val index = tabs.indexOfFirst { it.route == currentRoute }
        if (index != -1) index else 0
    }

    Box(
        modifier = modifier
            .padding(bottom = 16.dp, start = 24.dp, end = 24.dp)
            .fillMaxWidth()
            .height(64.dp)
            .hazeChild(
                state = hazeState,
                shape = RoundedCornerShape(32.dp),
                style = HazeStyle(
                    blurRadius = 30.dp,
                    tint = HazeTint(Color.Black.copy(alpha = 0.2f)),
                    backgroundColor = MaterialTheme.colorScheme.background,
                ),
            )
            .border(
                width = Dp.Hairline,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.5f),
                        Color.White.copy(alpha = 0.1f),
                    ),
                ),
                shape = RoundedCornerShape(32.dp),
            ),
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            ),
            LocalContentColor provides MaterialTheme.colorScheme.onSurface,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = index == selectedTabIndex
                    val alpha by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.55f,
                        label = "bottom_bar_alpha",
                    )
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.05f else 0.95f,
                        visibilityThreshold = 0.000001f,
                        animationSpec = spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                        ),
                        label = "bottom_bar_scale",
                    )

                    Column(
                        modifier = Modifier
                            .scale(scale)
                            .alpha(alpha)
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable {
                                if (currentRoute != tab.route) {
                                    navController.navigate(tab.route) {
                                        popUpTo(AppDestination.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(tab.titleRes),
                            tint = if (isSelected) tab.color else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(tab.titleRes),
                            color = if (isSelected) tab.color else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .fillMaxWidth(0.34f)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) tab.color else Color.Transparent,
                                ),
                        )
                    }
                }
            }
        }
    }
}
