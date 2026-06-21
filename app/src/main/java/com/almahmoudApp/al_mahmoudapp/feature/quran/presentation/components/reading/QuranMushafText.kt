package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading

import MeQuranFont
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse

private const val VERSE_TAG = "verse"

/**
 * Renders the surah's verses as a single flowing, justified text block — the authentic
 * mushaf reading experience. Verses run continuously into each other side-by-side; an
 * ornamental end-of-ayah marker (U+FD3E number U+FD3F) separates them, rendered in the
 * `me_quran` font with a distinct accent color. Tapping a verse selects it and opens
 * its tafseer/maany details.
 *
 * Layout notes:
 *  - Forced RTL text direction so Arabic never renders inverted.
 *  - The marker uses a distinct accent color to stand out from verse text.
 *  - Verse body font is slightly reduced; the marker number is slightly enlarged.
 *  - A wide no-break gap is placed BEFORE each marker; none after it.
 *  - Line height is generous (1.7x) with no trimming, so verses breathe vertically.
 */
@Composable
fun QuranMushafText(
    verses: List<QuranVerse>,
    fontSize: Float,
    selectedVerse: QuranVerse?,
    onVerseSelected: (QuranVerse) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val accentColor = MaterialTheme.colorScheme.primary
    val markerColor = MaterialTheme.colorScheme.tertiary
    val selectedKey = selectedVerse?.key

    val annotated = remember(verses, fontSize, selectedKey, accentColor, textColor, markerColor) {
        buildMushafAnnotated(
            verses = verses,
            fontSize = fontSize,
            selectedKey = selectedKey,
            textColor = textColor,
            accentColor = accentColor,
            markerColor = markerColor,
        )
    }

    ClickableText(
        text = annotated,
        style = TextStyle(
            color = textColor,
            fontFamily = MeQuranFont,
            fontWeight = FontWeight.Normal,
            fontSize = fontSize.sp,
            // Generous, non-trimmed line height so vertically adjacent verses don't touch.
            lineHeight = (fontSize * 1.7f).sp,
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.None,
            ),
            textDirection = TextDirection.Rtl,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = { offset ->
            annotated.getStringAnnotations(VERSE_TAG, offset, offset)
                .firstOrNull()
                ?.let { annotation ->
                    val (surah, verse) = annotation.item.split('_').map { it.toInt() }
                    verses.firstOrNull { it.surahNumber == surah && it.verseNumber == verse }
                        ?.let(onVerseSelected)
                }
        },
    )
}

private fun buildMushafAnnotated(
    verses: List<QuranVerse>,
    fontSize: Float,
    selectedKey: String?,
    textColor: Color,
    accentColor: Color,
    markerColor: Color,
): AnnotatedString = buildAnnotatedString {
    // Verse body slightly reduced; marker number slightly enlarged.
    val verseSize = (fontSize * 0.92f).sp
    val markerSize = (fontSize * 1.15f).sp

    verses.forEach { verse ->
        val isSelected = verse.key == selectedKey
        val verseColor = if (isSelected) accentColor else textColor

        // Verse body as a tagged, tappable span.
        pushStringAnnotation(tag = VERSE_TAG, annotation = verse.key)
        withStyle(
            SpanStyle(
                color = verseColor,
                fontFamily = MeQuranFont,
                fontSize = verseSize,
                background = if (isSelected) accentColor.copy(alpha = 0.12f) else Color.Transparent,
            ),
        ) {
            append(verse.content)
        }
        pop()

        // Wide no-break gap BEFORE the marker separates it clearly from the verse text;
        // the marker itself follows in a distinct accent color, then the next verse
        // starts immediately with no gap after the marker.
        withStyle(
            SpanStyle(
                color = markerColor,
                fontFamily = MeQuranFont,
                fontSize = markerSize,
                fontWeight = FontWeight.Bold,
            ),
        ) {
            append("\u00A0\u00A0")
            append(verseNumberMarker(verse.verseNumber))
        }
    }
}

private val QuranVerse.key: String get() = "${surahNumber}_$verseNumber"
