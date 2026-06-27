package com.almahmoudApp.al_mahmoudapp.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
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
        listOf(BottomBarTab.Home, BottomBarTab.Quran, BottomBarTab.Settings)
    }

    val selectedIndex by remember(currentRoute) {
        derivedStateOf {
            tabs.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .height(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .hazeChild(state = hazeState)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.80f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val selected = index == selectedIndex

                // Color and scale animations for icons and labels
                val animatedColor by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.primary 
                                  else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    label = "tabColor"
                )

                val iconScale by animateFloatAsState(
                    targetValue = if (selected) 1.2f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "iconScale"
                )

                val glowAlpha by animateFloatAsState(
                    targetValue = if (selected) 1.0f else 0.0f,
                    animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
                    label = "glowAlpha"
                )

                val labelSize by animateFloatAsState(
                    targetValue = if (selected) 11f else 10f,
                    label = "labelTextSize"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onNavigate(tab.route) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.drawBehind {
                            drawTabGlow(animatedColor, glowAlpha)
                        }
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(tab.titleRes),
                            tint = animatedColor,
                            modifier = Modifier
                                .size(26.dp)
                                .scale(iconScale)
                        )
                        Box(
                            modifier = Modifier.height(18.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Text(
                                text = stringResource(tab.titleRes),
                                color = animatedColor,
                                fontSize = labelSize.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                letterSpacing = 0.1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
