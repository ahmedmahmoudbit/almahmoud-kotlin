package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading

import AmiriFont
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The ornamental end-of-ayah medallion shown inline between verses — the circular
 * decorated marker seen in the reference mushaf. A tinted disc with a ring and four
 * short rays, holding the Arabic verse number. Theme-aware (light/dark). Tap it to
 * select the verse and open its details.
 */
@Composable
fun QuranAyahMedallion(
    verseNumber: Int,
    size: TextUnit,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = MaterialTheme.colorScheme.primary
    val fill = if (selected) accent.copy(alpha = 0.22f) else accent.copy(alpha = 0.10f)
    val ring = accent.copy(alpha = if (selected) 0.9f else 0.55f)
    val numberColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .size(size.value.dp)
            .clip(CircleShape)
            .background(fill)
            .border(width = 1.2.dp, color = ring, shape = CircleShape)
            .drawBehind {
                val center = Offset(this.size.width / 2f, this.size.height / 2f)
                val inner = this.size.minDimension * 0.40f
                val outer = this.size.minDimension * 0.50f
                val stroke = StrokeCap.Round
                repeat(RayCount) { i ->
                    val angle = (Math.PI / 2.0 * i).toFloat()
                    val cos = kotlin.math.cos(angle)
                    val sin = kotlin.math.sin(angle)
                    drawLine(
                        color = ring,
                        start = Offset(center.x + cos * inner, center.y + sin * inner),
                        end = Offset(center.x + cos * outer, center.y + sin * outer),
                        strokeWidth = 1.dp.toPx(),
                        cap = stroke,
                    )
                }
            }
            .padding(horizontal = 2.dp)
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = verseNumber.toArabicNumerals(),
            color = numberColor,
            fontFamily = AmiriFont,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.42f).sp,
        )
    }
}

private const val RayCount = 4
