package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading

import AmiriFont
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse

internal const val AYAH_TAG = "ayah"

/**
 * Renders the surah's verses as a single flowing, justified text block — the authentic
 * mushaf reading experience from the Flutter reference. Verses run continuously into
 * each other side-by-side; only a designed circular end-of-ayah medallion separates
 * them. Tapping a medallion selects its verse and opens its tafseer/maany details.
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
    val selectedKey = selectedVerse?.key

    val annotated = remember(verses, fontSize, selectedKey, accentColor, textColor) {
        buildMushafAnnotated(
            verses = verses,
            fontSize = fontSize,
            selectedKey = selectedKey,
            textColor = textColor,
            accentColor = accentColor,
        )
    }

    // Map each verse key -> verse so the inline content can resolve the tapped verse.
    // The medallion's alternate text carries the verse key, which the inline content
    // receives as `id` and uses to look up the verse.
    val verseByKey = remember(verses) { verses.associateBy { it.key } }
    val inlineContent = remember(fontSize, selectedKey, verseByKey, onVerseSelected) {
        mapOf(
            AYAH_TAG to InlineTextContent(
                placeholder = Placeholder(
                    width = 1.9.em,
                    height = 1.5.em,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                ),
                children = { id ->
                    val verse = verseByKey[id]
                    if (verse != null) {
                        QuranAyahMedallion(
                            verseNumber = verse.verseNumber,
                            size = (fontSize * 1.5f).sp,
                            selected = verse.key == selectedKey,
                            onClick = { onVerseSelected(verse) },
                        )
                    }
                },
            ),
        )
    }

    Text(
        text = annotated,
        style = TextStyle(
            color = textColor,
            fontFamily = AmiriFont,
            fontWeight = FontWeight.Normal,
            fontSize = fontSize.sp,
            lineHeight = (fontSize + 14f).sp,
        ),
        textAlign = TextAlign.Justify,
        inlineContent = inlineContent,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    )
}

private fun buildMushafAnnotated(
    verses: List<QuranVerse>,
    fontSize: Float,
    selectedKey: String?,
    textColor: Color,
    accentColor: Color,
): AnnotatedString = buildAnnotatedString {
    verses.forEach { verse ->
        val isSelected = verse.key == selectedKey
        if (isSelected) {
            withStyle(
                SpanStyle(
                    color = accentColor,
                    background = accentColor.copy(alpha = 0.12f),
                    fontSize = fontSize.sp,
                ),
            ) { append(verse.content) }
        } else {
            withStyle(SpanStyle(color = textColor, fontSize = fontSize.sp)) {
                append(verse.content)
            }
        }
        append(' ')
        // Alternate text = verse key (surah_verse); the inline content resolves the
        // number from it and routes taps to the matching verse.
        appendInlineContent(AYAH_TAG, verse.key)
        append(' ')
    }
}

internal val QuranVerse.key: String get() = "${surahNumber}_$verseNumber"
