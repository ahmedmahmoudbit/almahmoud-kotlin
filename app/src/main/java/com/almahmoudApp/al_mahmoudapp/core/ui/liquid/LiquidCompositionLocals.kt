package com.almahmoudApp.al_mahmoudapp.core.ui.liquid

import androidx.compose.runtime.staticCompositionLocalOf
import io.github.fletchmckee.liquid.LiquidState

/**
 * Current [LiquidState] created by [LiquidHost].
 */
val LocalLiquidState = staticCompositionLocalOf<LiquidState?> { null }

/**
 * Whether the current host should use the shader-backed liquid effect.
 */
val LocalLiquidGlassEnabled = staticCompositionLocalOf { false }

/**
 * Default style used by [LiquidGlass] in the current subtree.
 */
val LocalLiquidGlassStyle = staticCompositionLocalOf<LiquidGlassConfiguration?> { null }
