package com.almahmoudApp.al_mahmoudapp.core.ui.liquid

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

/**
 * Visual configuration for [LiquidGlass].
 *
 * The same configuration drives both the real RuntimeShader-backed liquid effect and the fallback
 * Surface rendering used on devices where the effect is unavailable or intentionally disabled.
 */
@Immutable
data class LiquidGlassConfiguration(
    val shape: Shape,
    val refraction: Float,
    val curve: Float,
    val edge: Float,
    val tint: Color,
    val saturation: Float,
    val dispersion: Float,
    val contrast: Float,
    val frost: Dp,
    val border: LiquidGlassBorder,
    val elevation: Dp,
    val glow: LiquidGlassGlow,
    val highlight: LiquidGlassHighlight,
)

/**
 * Border treatment shared by liquid and fallback rendering.
 */
@Immutable
data class LiquidGlassBorder(
    val width: Dp,
    val color: Color,
)

/**
 * Optional soft glow drawn behind a glass surface.
 */
@Immutable
data class LiquidGlassGlow(
    val color: Color,
    val elevation: Dp,
    val alpha: Float,
)

/**
 * Optional highlight overlay drawn inside the glass surface.
 */
@Immutable
data class LiquidGlassHighlight(
    val color: Color,
    val alpha: Float,
)
