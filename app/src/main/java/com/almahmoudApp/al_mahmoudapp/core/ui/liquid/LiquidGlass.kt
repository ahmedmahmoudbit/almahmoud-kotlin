package com.almahmoudApp.al_mahmoudapp.core.ui.liquid

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import io.github.fletchmckee.liquid.liquid

/**
 * Reusable Liquid Glass surface that can wrap any Compose content.
 *
 * The same API works on all supported app devices. On Android 13+ it delegates to the liquid
 * RuntimeShader effect. On older devices it falls back to a readable frosted [Surface].
 */
@Composable
fun LiquidGlass(
    modifier: Modifier = Modifier,
    shape: Shape = LiquidGlassDefaults.RoundedShape,
    style: LiquidGlassConfiguration = LocalLiquidGlassStyle.current ?: LiquidGlassDefaults.Frosted,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    val resolvedStyle = remember(style, shape) {
        style.copy(shape = shape)
    }
    val liquidState = LocalLiquidState.current
    val liquidEnabled = LocalLiquidGlassEnabled.current && liquidState != null

    if (liquidEnabled) {
        LiquidShaderSurface(
            modifier = modifier,
            style = resolvedStyle,
            contentPadding = contentPadding,
            content = content,
        )
    } else {
        LiquidFallbackSurface(
            modifier = modifier,
            style = resolvedStyle,
            contentPadding = contentPadding,
            content = content,
        )
    }
}

@Composable
private fun LiquidShaderSurface(
    modifier: Modifier,
    style: LiquidGlassConfiguration,
    contentPadding: PaddingValues,
    content: @Composable BoxScope.() -> Unit,
) {
    val state = LocalLiquidState.current ?: return LiquidFallbackSurface(
        modifier = modifier,
        style = style,
        contentPadding = contentPadding,
        content = content,
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = style.glow.elevation,
                shape = style.shape,
                ambientColor = style.glow.color.copy(alpha = style.glow.alpha),
                spotColor = style.glow.color.copy(alpha = style.glow.alpha),
            )
            .liquid(state) {
                refraction = style.refraction
                curve = style.curve
                edge = style.edge
                tint = style.tint
                saturation = style.saturation
                dispersion = style.dispersion
                contrast = style.contrast
                frost = style.frost
                shape = style.shape
            }
            .clip(style.shape)
            .background(style.highlight.color.copy(alpha = style.highlight.alpha))
            .border(style.border.asBorderStroke(), style.shape)
            .padding(contentPadding),
        content = content,
    )
}

@Composable
private fun LiquidFallbackSurface(
    modifier: Modifier,
    style: LiquidGlassConfiguration,
    contentPadding: PaddingValues,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = modifier
            .shadow(
                elevation = style.elevation,
                shape = style.shape,
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.10f),
            )
            .clip(style.shape)
            .blur(style.frost.coerceAtLeast(0.dp) / 8),
        shape = style.shape,
        color = style.tint.takeUnless { it == Color.Unspecified } ?: Color.White.copy(alpha = 0.18f),
        border = style.border.asBorderStroke(),
    ) {
        Box(
            modifier = Modifier
                .background(style.highlight.color.copy(alpha = style.highlight.alpha))
                .padding(contentPadding),
            content = content,
        )
    }
}

private fun LiquidGlassBorder.asBorderStroke(): BorderStroke {
    return BorderStroke(width = width, color = color)
}
