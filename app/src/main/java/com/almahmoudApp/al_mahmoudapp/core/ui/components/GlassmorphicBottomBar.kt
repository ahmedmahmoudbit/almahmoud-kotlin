package com.almahmoudApp.al_mahmoudapp.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.navigation.AppDestination

sealed class BottomBarTab(
    val route: String,
    val titleRes: Int,
    val icon: ImageVector,
) {
    data object Home : BottomBarTab(
        route = AppDestination.Home.route,
        titleRes = R.string.nav_home,
        icon = Icons.Rounded.Home,
    )

    data object Quran : BottomBarTab(
        route = AppDestination.Quran.route,
        titleRes = R.string.nav_quran,
        icon = Icons.AutoMirrored.Rounded.MenuBook,
    )

    data object Settings : BottomBarTab(
        route = AppDestination.Settings.route,
        titleRes = R.string.nav_settings,
        icon = Icons.Rounded.Settings,
    )
}

internal fun DrawScope.drawTabGlow(color: Color, alpha: Float) {
    if (alpha <= 0f) return
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha * 0.28f),
                color.copy(alpha = alpha * 0.08f),
                Color.Transparent,
            ),
            radius = size.minDimension * 0.9f,
        ),
        radius = size.minDimension * 0.9f,
    )
}

@Composable
fun AppBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = remember {
        listOf(BottomBarTab.Home, BottomBarTab.Quran, BottomBarTab.Settings)
    }

    val selectedIndex by remember(currentRoute) {
        derivedStateOf {
            tabs.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
        }
    }

    NavigationBar(modifier = modifier) {
        tabs.forEachIndexed { index, tab ->
            NavigationBarItem(
                selected = index == selectedIndex,
                onClick = { onNavigate(tab.route) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = stringResource(tab.titleRes),
                    )
                },
                label = {
                    Text(text = stringResource(tab.titleRes))
                },
            )
        }
    }
}
