package com.almahmoudApp.al_mahmoudapp.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.navigation.AppDestination
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassCard
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

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

    data object Status : BottomBarTab(
        route = AppDestination.Status.route,
        titleRes = R.string.nav_status,
        icon = Icons.Outlined.CollectionsBookmark,
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
                color.copy(alpha = alpha * 0.35f),
                color.copy(alpha = alpha * 0.10f),
                Color.Transparent,
            ),
            radius = size.minDimension * 0.8f,
        ),
        radius = size.minDimension * 0.8f,
    )
}

@Composable
fun AppBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val tabs = remember {
        listOf(BottomBarTab.Home, BottomBarTab.Quran, BottomBarTab.Status, BottomBarTab.Settings)
    }

    val selectedIndex = tabs.indexOfFirst { it.route == currentRoute }
    val backgroundColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .hazeChild(state = hazeState) {
                this.backgroundColor = backgroundColor
                alpha = 0.85f
            }
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.80f)
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val selected = index == selectedIndex

                val animatedColor by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.primary
                                  else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    label = "tabColor"
                )

                val labelSize by animateFloatAsState(
                    targetValue = if (selected) 11f else 10f,
                    label = "labelTextSize"
                )

                LiquidGlassCard(
                    onClick = { onNavigate(tab.route) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(3.dp),
                    cornerRadius = 14.dp,
                    refraction = 0.3f,
                    frost = if (selected) 6f else 3f,
                    dispersion = 0.1f,
                    glowAlpha = if (selected) 0.7f else 0.15f,
                    tintColor = animatedColor,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(tab.titleRes),
                            tint = animatedColor,
                            modifier = Modifier.size(24.dp),
                        )
                        Text(
                            text = stringResource(tab.titleRes),
                            color = animatedColor,
                            fontSize = labelSize.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.1.sp,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }
        }
    }
}
