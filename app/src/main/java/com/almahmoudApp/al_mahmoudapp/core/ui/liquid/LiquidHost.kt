package com.almahmoudApp.al_mahmoudapp.core.ui.liquid

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.rememberLiquidState

/**
 * Root provider for the Liquid Glass system.
 *
 * Place this once around a screen. Background elements that should be sampled by liquid glass must
 * opt in with [Modifier.liquidSource]. Floating glass elements can then use [LiquidGlass].
 */
@Composable
fun LiquidHost(
    modifier: Modifier = Modifier,
    enabled: Boolean = isLiquidGlassSupported(),
    style: LiquidGlassConfiguration = LiquidGlassDefaults.Frosted,
    content: @Composable () -> Unit,
) {
    val liquidState = rememberLiquidState()
    val useLiquid = remember(enabled) { enabled && isLiquidGlassSupported() }

    CompositionLocalProvider(
        LocalLiquidState provides liquidState,
        LocalLiquidGlassEnabled provides useLiquid,
        LocalLiquidGlassStyle provides style,
    ) {
        androidx.compose.foundation.layout.Box(modifier = modifier) {
            content()
        }
    }
}

/**
 * Marks a background element as a source that can be sampled by [LiquidGlass].
 *
 * Do not place [LiquidGlass] as a descendant of a composable using this modifier; keep them as
 * siblings inside [LiquidHost] to avoid recursive sampling.
 */
fun Modifier.liquidSource(
    liquidState: LiquidState? = null,
): Modifier {
    val state = liquidState ?: return this
    return liquefiable(state)
}

/**
 * Marks a background element using the [LiquidState] from [LiquidHost].
 */
@Composable
fun Modifier.liquidSource(): Modifier {
    return liquidSource(LocalLiquidState.current)
}

/**
 * RuntimeShader-backed liquid effects are available on Android 13+.
 */
fun isLiquidGlassSupported(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}
