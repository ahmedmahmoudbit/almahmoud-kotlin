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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.QuranVerse

private const val VERSE_TAG = "verse"

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
            ayaColor = accentColor,
        )
    }
    ClickableText(
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
            textAlign = TextAlign.Center
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
    ayaColor: Color,
): AnnotatedString = buildAnnotatedString {
    val verseSize = (fontSize * 0.90f).sp
    val markerSize = ((fontSize - 5) * 1f).sp

    verses.forEachIndexed { index, verse ->
        val isSelected = verse.key == selectedKey
        val verseColor = if (isSelected) accentColor else textColor
        pushStringAnnotation(tag = VERSE_TAG, annotation = verse.key)
        withStyle(
            SpanStyle(
                color = verseColor,
                fontFamily = MeQuranFont,
                fontSize = verseSize,
                background = if (isSelected) accentColor.copy(alpha = 0.12f) else Color.Transparent,
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
