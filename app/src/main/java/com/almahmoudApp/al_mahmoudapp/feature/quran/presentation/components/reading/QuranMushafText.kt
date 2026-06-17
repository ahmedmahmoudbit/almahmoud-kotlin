package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading

import AmiriFont
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse

private const val VERSE_TAG = "verse"

/**
 * Renders the surah's verses as a single flowing, centered, justified text block —
 * the authentic mushaf reading experience from the Flutter reference. Each verse runs
 * inline into the next, separated only by an ornamental numbered end-of-verse marker
 * (۝ n). Tapping a verse highlights it and reports the selection via [onVerseSelected].
 */
@Composable
fun QuranMushafText(
    verses: List<QuranVerse>,
    fontSize: Float,
    selectedVerse: QuranVerse?,
    onVerseSelected: (QuranVerse) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textColor = Color(0xFF1A1A1A)
    val selectedColor = Color(0xFF8E1B1B)
    val markerColor = Color(0xFF8E1B1B)
    val annotated = remember(verses, selectedVerse) {
        buildMushafAnnotated(
            verses = verses,
            fontSize = fontSize,
            selectedVerseKey = selectedVerse?.key,
            textColor = textColor,
            selectedColor = selectedColor,
            markerColor = markerColor,
        )
    }

    ClickableText(
        text = annotated,
        style = TextStyle(
            color = textColor,
            fontFamily = AmiriFont,
            fontWeight = FontWeight.Normal,
            fontSize = fontSize.sp,
            lineHeight = (fontSize + 14f).sp,
            textAlign = TextAlign.Justify,
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
    selectedVerseKey: String?,
    textColor: Color,
    selectedColor: Color,
    markerColor: Color,
): AnnotatedString = buildAnnotatedString {
    verses.forEachIndexed { _, verse ->
        val key = verse.key
        val isSelected = key == selectedVerseKey
        val verseColor = if (isSelected) selectedColor else textColor

        // Verse text as a tagged, tappable span.
        pushStringAnnotation(tag = VERSE_TAG, annotation = key)
        withStyle(
            SpanStyle(
                color = verseColor,
                fontSize = fontSize.sp,
                background = if (isSelected) selectedColor.copy(alpha = 0.12f) else Color.Transparent,
            ),
        ) {
            append(verse.content)
        }
        pop()
        append(' ')

        // Ornamental end-of-verse marker (۝ n), tinted to stand out.
        withStyle(
            SpanStyle(
                color = markerColor,
                fontSize = (fontSize * 0.7f).sp,
                fontWeight = FontWeight.Bold,
            ),
        ) {
            append(verseEndMarker(verse.verseNumber))
        }
        append(' ')
    }
}

private val QuranVerse.key: String get() = "${surahNumber}_$verseNumber"
