package com.almahmoudApp.al_mahmoudapp.core.ui.components
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import androidx.compose.ui.unit.LayoutDirection

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

public fun DrawScope.drawTabGlow(color: Color, alpha: Float) {
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
private fun TabIndicator(
    isSelected: Boolean,
    color: Color,
) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 22.dp else 0.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioMediumBouncy,
        ),
        label = "indicator_width",
    )
    val indicatorAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(300),
        label = "indicator_alpha",
    )

    Box(
        modifier = Modifier
            .height(2.5.dp)
            .width(width)
            .clip(CircleShape)
            .alpha(indicatorAlpha)
            .background(color),
    )
}

@Composable
private fun RowScope.GlassTabItem(
    tab: BottomBarTab,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    var rippleTrigger by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.45f,
        animationSpec = tween(300),
        label = "tab_alpha",
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 0.93f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy,
        ),
        label = "tab_scale",
    )
    val glowAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(400),
        label = "glow_alpha",
    )
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) tab.color else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "icon_color",
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clip(RoundedCornerShape(28.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                rippleTrigger = !rippleTrigger
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawTabGlow(tab.color, glowAlpha)
                },
        )

        Column(
            modifier = Modifier
                .scale(scale)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.height(1.dp))
            Icon(
                imageVector = tab.icon,
                contentDescription = stringResource(tab.titleRes),
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(tab.titleRes),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = iconColor,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
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
        tabs.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)
    }

    Box(
        modifier = modifier
            .padding(bottom = 16.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .height(68.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeChild(
                    state = hazeState,
                    shape = RoundedCornerShape(34.dp),
                    style = HazeStyle(
                        blurRadius = 32.dp,
                        tint = HazeTint(Color.Black.copy(alpha = 0.18f)),
                        backgroundColor = MaterialTheme.colorScheme.background,
                    ),
                )
                .border(
                    width = Dp.Hairline,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.55f),
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.25f),
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    ),
                    shape = RoundedCornerShape(34.dp),
                )
                .drawWithContent {
                    drawContent()
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.18f),
                                Color.Transparent,
                            ),
                            endY = size.height * 0.4f,
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(34.dp.toPx()),
                    )
                    val isRtl = layoutDirection == LayoutDirection.Rtl

                    val displayIndex =
                        if (isRtl) (tabs.lastIndex - selectedTabIndex)
                        else selectedTabIndex

                    val tabWidth = size.width / 3f
                    val glowCenterX = (displayIndex + 0.5f) * tabWidth
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                tabs.getOrNull(selectedTabIndex)?.color?.copy(alpha = 0.20f)
                                    ?: Color.Transparent,
                                Color.Transparent,
                            ),
                            center = Offset(glowCenterX, size.height * 0.5f),
                            radius = tabWidth * 0.7f,
                        ),
                        radius = tabWidth * 0.7f,
                        center = Offset(glowCenterX, size.height * 0.5f),
                    )
                },
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEachIndexed { index, tab ->
                    GlassTabItem(
                        tab = tab,
                        isSelected = index == selectedTabIndex,
                        onClick = {
                            if (currentRoute != tab.route) {
                                navController.navigate(tab.route) {
                                    popUpTo(AppDestination.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}
