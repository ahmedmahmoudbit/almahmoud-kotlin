package com.almahmoudApp.al_mahmoudapp.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassCard

enum class GlassButtonVariant {
    Primary,
    Secondary,
    Tonal,
    Plain,
}

@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: GlassButtonVariant = GlassButtonVariant.Primary,
    isLoading: Boolean = false,
    cornerRadius: Dp = 28.dp,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val tintColor = when (variant) {
        GlassButtonVariant.Primary -> MaterialTheme.colorScheme.primary
        GlassButtonVariant.Secondary -> MaterialTheme.colorScheme.secondary
        GlassButtonVariant.Tonal -> MaterialTheme.colorScheme.tertiary
        GlassButtonVariant.Plain -> Color.White
    }

    LiquidGlassCard(
        onClick = { if (enabled) onClick() },
        modifier = modifier,
        cornerRadius = cornerRadius,
        refraction = 0.45f,
        frost = 6f,
        dispersion = 0.2f,
        glowAlpha = if (enabled) 0.6f else 0.15f,
        tintColor = tintColor,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = tintColor,
                modifier = Modifier.padding(16.dp),
                strokeWidth = 3.dp,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center,
                content = content,
            )
        }
    }
}

@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: GlassButtonVariant = GlassButtonVariant.Primary,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val tintColor = when (variant) {
        GlassButtonVariant.Primary -> MaterialTheme.colorScheme.primary
        GlassButtonVariant.Secondary -> MaterialTheme.colorScheme.secondary
        GlassButtonVariant.Tonal -> MaterialTheme.colorScheme.tertiary
        GlassButtonVariant.Plain -> Color.White
    }

    LiquidGlassCard(
        onClick = { if (enabled) onClick() },
        modifier = modifier,
        cornerRadius = 999.dp,
        refraction = 0.55f,
        frost = 6f,
        dispersion = 0.2f,
        glowAlpha = if (enabled) 0.6f else 0.15f,
        tintColor = tintColor,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

@Composable
fun GlassTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    LiquidGlassCard(
        onClick = onClick,
        modifier = modifier,
        cornerRadius = 12.dp,
        refraction = 0.3f,
        frost = if (isSelected) 6f else 3f,
        dispersion = 0.1f,
        glowAlpha = if (isSelected) 0.7f else 0.15f,
        tintColor = if (isSelected) color else Color.White,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold
                           else FontWeight.Normal,
            ),
            color = if (isSelected) color
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}
