package com.almahmoudApp.al_mahmoudapp.core.ui.liquid

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Defaults and presets for the app-level Liquid Glass system.
 */
object LiquidGlassDefaults {
    val RoundedShape: Shape = RoundedCornerShape(28.dp)
    val PillShape: Shape = RoundedCornerShape(percent = 50)
    val CardShape: Shape = RoundedCornerShape(20.dp)

    @Stable
    fun border(
        width: androidx.compose.ui.unit.Dp = 1.dp,
        color: Color = Color.White.copy(alpha = 0.34f),
    ): LiquidGlassBorder = LiquidGlassBorder(width = width, color = color)

    @Stable
    fun glow(
        color: Color = Color.White,
        elevation: androidx.compose.ui.unit.Dp = 0.dp,
        alpha: Float = 0f,
    ): LiquidGlassGlow = LiquidGlassGlow(color = color, elevation = elevation, alpha = alpha)

    @Stable
    fun highlight(
        color: Color = Color.White,
        alpha: Float = 0.18f,
    ): LiquidGlassHighlight = LiquidGlassHighlight(color = color, alpha = alpha)

    /**
     * Balanced frosted-glass preset for text and controls.
     */
    val Frosted: LiquidGlassConfiguration
        @Composable get() = LiquidGlassConfiguration(
            shape = RoundedShape,
            refraction = 0.18f,
            curve = 0.22f,
            edge = 0.18f,
            tint = Color.White.copy(alpha = 0.18f),
            saturation = 1.15f,
            dispersion = 0.02f,
            contrast = 1.08f,
            frost = 14.dp,
            border = border(),
            elevation = 8.dp,
            glow = glow(alpha = 0.10f, elevation = 12.dp),
            highlight = highlight(),
        )

    /**
     * Stronger lens preset for decorative chips and floating controls.
     */
    val Crystal: LiquidGlassConfiguration
        @Composable get() = LiquidGlassConfiguration(
            shape = PillShape,
            refraction = 0.28f,
            curve = 0.32f,
            edge = 0.26f,
            tint = Color.White.copy(alpha = 0.10f),
            saturation = 1.24f,
            dispersion = 0.04f,
            contrast = 1.12f,
            frost = 8.dp,
            border = border(width = 1.dp, color = Color.White.copy(alpha = 0.42f)),
            elevation = 10.dp,
            glow = glow(alpha = 0.16f, elevation = 16.dp),
            highlight = highlight(alpha = 0.22f),
        )

    /**
     * Subtle preset for cards that should remain quiet and readable.
     */
    val Soft: LiquidGlassConfiguration
        @Composable get() = LiquidGlassConfiguration(
            shape = CardShape,
            refraction = 0.12f,
            curve = 0.16f,
            edge = 0.10f,
            tint = Color.White.copy(alpha = 0.24f),
            saturation = 1.04f,
            dispersion = 0f,
            contrast = 1f,
            frost = 18.dp,
            border = border(color = Color.White.copy(alpha = 0.26f)),
            elevation = 4.dp,
            glow = glow(),
            highlight = highlight(alpha = 0.12f),
        )
}
