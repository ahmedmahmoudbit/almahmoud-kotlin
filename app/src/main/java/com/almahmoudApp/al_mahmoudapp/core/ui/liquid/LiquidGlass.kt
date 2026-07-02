package com.almahmoudApp.al_mahmoudapp.core.ui.liquid


import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import com.almahmoudApp.al_mahmoudapp.core.ui.components.drawTabGlow

private const val LIQUID_GLASS_SHADER = """
uniform shader composable;
uniform float2 size;
uniform float refraction;
uniform float frost;
uniform float dispersion;
uniform float time;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / size;
    
    // تأثير الانحناء الزجاجي — يشوّه الإحداثيات مثل عدسة محدبة
    float2 center = float2(0.5, 0.5);
    float2 delta  = uv - center;
    float  dist   = length(delta);
    float  lens   = 1.0 - dist * dist * refraction * 0.6;
    float2 distortedUV = center + delta * lens;

    // تأثير التشتت — يفصل قنوات RGB قليلاً
    float shift = dispersion * 0.008;
    half r = composable.eval(distortedUV * size + float2(shift, 0.0) * size).r;
    half g = composable.eval(distortedUV * size).g;
    half b = composable.eval(distortedUV * size - float2(shift, 0.0) * size).b;
    half4 color = half4(r, g, b, 1.0);

    // Frosted Glass — تمزج اللون مع الأبيض الضبابي
    float frosted = frost * 0.015;
    color = mix(color, half4(1.0, 1.0, 1.0, 1.0), frosted);

    // رفع التشبع قليلاً مثل iOS
    float gray  = dot(color.rgb, half3(0.299, 0.587, 0.114));
    color.rgb   = mix(half3(gray), color.rgb, 1.25);

    return color;
}
"""

// ─────────────────────────────────────────────
// الـ Component الرئيسي
// ─────────────────────────────────────────────
@Composable
fun LiquidGlassCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    refraction: Float = 0.55f,
    frost: Float = 6f,
    dispersion: Float = 0.4f,
    glowAlpha: Float = 0.55f,
    tintColor: Color = Color.White,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    val density = LocalDensity.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        LiquidGlassShader(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            cornerRadius = cornerRadius,
            refraction = refraction,
            frost = frost,
            dispersion = dispersion,
            glowAlpha = glowAlpha,
            tintColor = tintColor,
            content = content,
        )
    } else {
        LiquidGlassFallback(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            cornerRadius = cornerRadius,
            glowAlpha = glowAlpha,
            tintColor = tintColor,
            content = content,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun LiquidGlassShader(
    onClick: () -> Unit,
    modifier: Modifier,
    shape: RoundedCornerShape,
    cornerRadius: Dp,
    refraction: Float,
    frost: Float,
    dispersion: Float,
    glowAlpha: Float,
    tintColor: Color,
    content: @Composable BoxScope.() -> Unit,
) {
    var cardSize by remember { mutableStateOf(IntSize.Zero) }
    val shader = remember { RuntimeShader(LIQUID_GLASS_SHADER) }

    Box(modifier = modifier.onSizeChanged { cardSize = it }) {

        // ── مرساة الحجم: نسخة غير مرئية من المحتوى تُبلغ عن الحجم الطبيعي ──
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .alpha(0f),
            contentAlignment = Alignment.Center,
            content = content,
        )

        // ── الطبقة الخلفية: Blur حقيقي للخلفية ──
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .graphicsLayer {
                    renderEffect = RenderEffect
                        .createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
                        .asComposeRenderEffect()
                    alpha = 0.99f
                }
                .background(
                    Brush.verticalGradient(
                        listOf(
                            tintColor.copy(alpha = 0.32f),
                            tintColor.copy(alpha = 0.20f)
                        )
                    )
                )
        )

        // ── الطبقة الأمامية: Shader + المحتوى ──
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .drawWithContent {
                    drawContent()
                }
                .drawWithContent {
                    drawContent()
                    drawLiquidGlassOverlay(
                        size = size,
                        cornerRadius = cornerRadius.toPx(),
                        glowAlpha = glowAlpha,
                        tintColor = tintColor,
                    )
                }
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}


@Composable
private fun LiquidGlassFallback(
    onClick: () -> Unit,
    modifier: Modifier,
    shape: RoundedCornerShape,
    cornerRadius: Dp,
    glowAlpha: Float,
    tintColor: Color,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(shape)
            .graphicsLayer {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    renderEffect = RenderEffect
                        .createBlurEffect(18f, 18f, Shader.TileMode.CLAMP)
                        .asComposeRenderEffect()
                }
            }
            .drawWithContent {
                drawContent()

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            tintColor.copy(alpha = 0.22f),
                            tintColor.copy(alpha = 0.08f),
                        )
                    )
                )

                drawLiquidGlassOverlay(
                    size = size,
                    cornerRadius = cornerRadius.toPx(),
                    glowAlpha = glowAlpha,
                    tintColor = tintColor,
                )
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

// ─────────────────────────────────────────────
// رسم الحواف المضيئة والانعكاس الداخلي
// يُستخدم في كلا الوضعين (Shader + Fallback)
// ─────────────────────────────────────────────
private fun DrawScope.drawLiquidGlassOverlay(
    size: Size,
    cornerRadius: Float,
    glowAlpha: Float,
    tintColor: Color = Color.White,
) {
    val strokeWidth = 1.2.dp.toPx()
    val halfStroke = strokeWidth / 2f
    val inset = halfStroke

    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                tintColor.copy(alpha = glowAlpha),
                tintColor.copy(alpha = glowAlpha * 0.3f),
                tintColor.copy(alpha = glowAlpha * 0.1f),
                tintColor.copy(alpha = glowAlpha * 0.5f),
            ),
            start = Offset(0f, 0f),
            end   = Offset(size.width, size.height),
        ),
        topLeft     = Offset(inset, inset),
        size        = Size(size.width - strokeWidth, size.height - strokeWidth),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius - inset),
        style       = Stroke(width = strokeWidth),
    )

    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                tintColor.copy(alpha = 0.35f),
                tintColor.copy(alpha = 0.0f),
            ),
            startY = 0f,
            endY   = size.height * 0.45f,
        ),
        topLeft     = Offset(inset * 2, inset * 2),
        size        = Size(size.width - inset * 4, size.height * 0.42f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius - inset * 2),
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                tintColor.copy(alpha = 0.5f),
                tintColor.copy(alpha = 0.0f),
            ),
            center = Offset(size.width * 0.2f, size.height * 0.12f),
            radius = size.width * 0.28f,
        ),
        center = Offset(size.width * 0.2f, size.height * 0.12f),
        radius = size.width * 0.28f,
    )

    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Black.copy(alpha = 0.06f),
            ),
            startY = size.height * 0.6f,
            endY   = size.height,
        ),
        topLeft     = Offset(inset, inset),
        size        = Size(size.width - strokeWidth, size.height - strokeWidth),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius - inset),
    )
}