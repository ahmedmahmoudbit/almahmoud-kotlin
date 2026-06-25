package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading

import MeQuranFont
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse

private const val VERSE_TAG = "verse"

/**
 * Renders the surah's verses as a single flowing, justified text block. Verses run
 * continuously into each other; an ornamental end-of-ayah marker (U+FD3E number U+FD3F)
 * separates them in the `me_quran` font with a distinct color.
 *
 * Interaction: a **long-press** on a verse opens its tafseer/maany sheet via
 * [onVerseLongClick]; a **simple tap** toggles the controls bar via [onTap].
 */
@Composable
fun QuranMushafText(
    verses: List<QuranVerse>,
    fontSize: Float,
    selectedVerse: QuranVerse?,
    highlightedVerseNumber: Int = -1,
    onVerseLongClick: (QuranVerse) -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val accentColor = MaterialTheme.colorScheme.primary
    val selectedKey = selectedVerse?.key

    val annotated = remember(verses, fontSize, selectedKey, highlightedVerseNumber, accentColor, textColor) {
        buildMushafAnnotated(
            verses = verses,
            fontSize = fontSize,
            selectedKey = selectedKey,
            highlightedVerseNumber = highlightedVerseNumber,
            textColor = textColor,
            accentColor = accentColor,
            ayaColor = accentColor,
        )
    }

    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Text(
        text = annotated,
        style = TextStyle(
            color = textColor,
            fontFamily = MeQuranFont,
            fontWeight = FontWeight.Normal,
            fontSize = fontSize.sp,
            lineHeight = (fontSize * 1.7f).sp,
            lineHeightStyle = LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.None,
            ),
            textDirection = TextDirection.Rtl,
            textAlign = TextAlign.Center,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .pointerInput(annotated, verses) {
                detectTapGestures(
                    onTap = { onTap() },
                    onLongPress = { offset ->
                        val layout = layoutResult ?: return@detectTapGestures
                        val charOffset = layout.getOffsetForPosition(offset)
                        annotated.getStringAnnotations(VERSE_TAG, charOffset, charOffset)
                            .firstOrNull()
                            ?.let { annotation ->
                                val (surah, verse) = annotation.item.split('_').map { it.toInt() }
                                verses.firstOrNull {
                                    it.surahNumber == surah && it.verseNumber == verse
                                }?.let(onVerseLongClick)
                            }
                    },
                )
            },
        onTextLayout = { layoutResult = it },
    )
}

private fun buildMushafAnnotated(
    verses: List<QuranVerse>,
    fontSize: Float,
    selectedKey: String?,
    highlightedVerseNumber: Int,
    textColor: Color,
    accentColor: Color,
    ayaColor: Color,
): AnnotatedString = buildAnnotatedString {
    val verseSize = (fontSize * 0.90f).sp
    val markerSize = ((fontSize - 5) * 1f).sp

    verses.forEachIndexed { index, verse ->
        val isSelected = verse.key == selectedKey
        val isHighlighted = verse.verseNumber == highlightedVerseNumber
        val verseColor = when {
            isSelected -> accentColor
            isHighlighted -> Color(0xFFE65100)
            else -> textColor
        }
        val background = when {
            isSelected -> accentColor.copy(alpha = 0.12f)
            isHighlighted -> Color(0xFFFFA000).copy(alpha = 0.25f)
            else -> Color.Transparent
        }
        
        pushStringAnnotation(tag = VERSE_TAG, annotation = verse.key)
        withStyle(
            SpanStyle(
                color = verseColor,
                fontFamily = MeQuranFont,
                fontSize = verseSize,
                background = background,
            ),
        ) {
            if (index != 0) append("\u2002")
            append(verse.content)
        }
        pop()
        withStyle(
            SpanStyle(
                color = ayaColor,
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
