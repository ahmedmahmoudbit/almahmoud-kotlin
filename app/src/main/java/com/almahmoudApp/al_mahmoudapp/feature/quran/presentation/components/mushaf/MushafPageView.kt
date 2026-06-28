package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.mushaf

import AmiriFont
import QuranCommonFont
import SurahIconFont
import UthmanicHafsFont
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.MushafLine
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.MushafLineType
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.MushafPage
import com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model.MushafWord
import com.almahmoudApp.al_mahmoudapp.feature.quran.util.QuranGlyphs

private const val MUSHAF_LINE_HEIGHT_MULT = 2.0f
private const val MUSHAF_CENTERED_GAP_FRACTION = 0.22f

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MushafPageView(
    page: MushafPage,
    fontSize: Float,
    selectedAyahId: Int? = null,
    onAyahClick: ((Int) -> Unit)? = null,
    onAyahLongClick: ((Int, Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface
    val lineColor = primaryColor.copy(alpha = 0.15f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        page.lines.forEachIndexed { index, line ->
            MushafLineView(
                line = line,
                fontSize = fontSize,
                textColor = textColor,
                primaryColor = primaryColor,
                selectedAyahId = selectedAyahId,
                onAyahClick = onAyahClick,
                onAyahLongClick = onAyahLongClick,
                modifier = Modifier.fillMaxWidth(),
            )
            if (line.lineType == MushafLineType.AYAH && index < page.lines.size - 1) {
                val nextLine = page.lines.getOrNull(index + 1)
                if (nextLine?.lineType == MushafLineType.AYAH) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .height(0.5.dp)
                            .background(lineColor)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MushafLineView(
    line: MushafLine,
    fontSize: Float,
    textColor: Color,
    primaryColor: Color,
    selectedAyahId: Int?,
    onAyahClick: ((Int) -> Unit)?,
    onAyahLongClick: ((Int, Int) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    when (line.lineType) {
        MushafLineType.SURAH_NAME -> {
            SurahNameLine(
                surahNo = line.surahNo,
                modifier = modifier,
            )
        }
        MushafLineType.BASMALLAH -> {
            BasmalaLine(
                fontSize = fontSize,
                primaryColor = primaryColor,
                modifier = modifier,
            )
        }
        MushafLineType.AYAH -> {
            AyahLine(
                words = line.words,
                fontSize = fontSize,
                textColor = textColor,
                primaryColor = primaryColor,
                isCentered = line.isCentered,
                selectedAyahId = selectedAyahId,
                onAyahClick = onAyahClick,
                onAyahLongClick = onAyahLongClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun SurahNameLine(
    surahNo: Int?,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurface
    val surahIcon = remember(surahNo) {
        surahNo?.let { getSurahIcon(it) } ?: ""
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "\uE000",
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                fontFamily = QuranCommonFont,
                fontSize = 46.sp,
                textAlign = TextAlign.Center,
                color = primaryColor,
            ),
        )

        Text(
            text = surahIcon,
            modifier = Modifier.padding(top = 8.dp),
            style = TextStyle(
                fontFamily = SurahIconFont,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                color = textColor,
            ),
        )
    }
}

private fun getSurahIcon(surahNo: Int): String {
    val prefix = "\uE903"
    val icons = arrayOf(
        "", "\uE904", "\uE905", "\uE906", "\uE907", "\uE908",
        "\uE90B", "\uE90C", "\uE90D", "\uE90E", "\uE90F",
        "\uE910", "\uE911", "\uE912", "\uE913", "\uE914",
        "\uE915", "\uE916", "\uE917", "\uE918", "\uE919",
        "\uE91A", "\uE91B", "\uE91C", "\uE91D", "\uE91E",
        "\uE91F", "\uE920", "\uE921", "\uE922", "\uE923",
        "\uE924", "\uE925", "\uE926", "\uE92E", "\uE92F",
        "\uE930", "\uE931", "\uE909", "\uE90A", "\uE927",
        "\uE928", "\uE929", "\uE92A", "\uE92B", "\uE92C",
        "\uE92D", "\uE932", "\uE902", "\uE933", "\uE934",
        "\uE935", "\uE936", "\uE937", "\uE938", "\uE939",
        "\uE93A", "\uE93B", "\uE93C", "\uE900", "\uE901",
        "\uE941", "\uE942", "\uE943", "\uE944", "\uE945",
        "\uE946", "\uE947", "\uE948", "\uE949", "\uE94A",
        "\uE94B", "\uE94C", "\uE94D", "\uE94E", "\uE94F",
        "\uE950", "\uE951", "\uE952", "\uE93D", "\uE93E",
        "\uE93F", "\uE940", "\uE953", "\uE954", "\uE955",
        "\uE956", "\uE957", "\uE958", "\uE959", "\uE95A",
        "\uE95B", "\uE95C", "\uE95D", "\uE95E", "\uE95F",
        "\uE960", "\uE961", "\uE962", "\uE963", "\uE964",
        "\uE965", "\uE966", "\uE967", "\uE968", "\uE969",
        "\uE96A", "\uE96B", "\uE96C", "\uE96D", "\uE96E",
        "\uE96F", "\uE970", "\uE971", "\uE972"
    )
    return icons.getOrElse(surahNo) { "" } + prefix
}

@Composable
private fun BasmalaLine(
    fontSize: Float,
    primaryColor: Color,
    modifier: Modifier = Modifier,
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    Text(
        text = "\uFDFD",
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 20.dp),
        style = TextStyle(
            fontFamily = QuranCommonFont,
            fontSize = 36.sp,
            textAlign = TextAlign.Center,
            color = textColor,
        ),
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AyahLine(
    words: List<MushafWord>,
    fontSize: Float,
    textColor: Color,
    primaryColor: Color,
    isCentered: Boolean,
    selectedAyahId: Int?,
    onAyahClick: ((Int) -> Unit)?,
    onAyahLongClick: ((Int, Int) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    if (words.isEmpty()) return

    val firstWord = words.first()
    val ayahId = firstWord.ayahId
    val (surahNo, verseNo) = com.almahmoudApp.al_mahmoudapp.feature.quran.util.QuranConstants.getChapterAndVerseFromAyahId(ayahId)
    val isSelected = selectedAyahId == ayahId

    val arrangement = if (isCentered) {
        val gapDp = (fontSize * MUSHAF_CENTERED_GAP_FRACTION).dp
        Arrangement.spacedBy(gapDp, Alignment.CenterHorizontally)
    } else {
        Arrangement.SpaceBetween
    }

    val lineHeight = (fontSize * MUSHAF_LINE_HEIGHT_MULT).sp

    val bgColor = if (isSelected) {
        primaryColor.copy(alpha = 0.1f)
    } else {
        Color.Transparent
    }

    Row(
        modifier = modifier
            .padding(vertical = 1.dp)
            .background(bgColor)
            .combinedClickable(
                onClick = { onAyahClick?.invoke(ayahId) },
                onLongClick = { onAyahLongClick?.invoke(surahNo, verseNo) },
            ),
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        words.forEach { word ->
            val isAyahNumber = remember(word.text) {
                QuranGlyphs.isAyahNumber(word.text)
            }

            val displayText = if (isAyahNumber) {
                QuranGlyphs.formatAyahNumberForDisplay(word.text)
            } else {
                word.text
            }

            Text(
                text = displayText,
                style = TextStyle(
                    fontFamily = UthmanicHafsFont,
                    fontSize = fontSize.sp,
                    lineHeight = lineHeight,
                    color = textColor,
                    textDirection = TextDirection.Rtl,
                ),
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}

private fun getSurahNameArabic(surahNo: Int): String {
    val names = arrayOf(
        "", "الفاتحة", "البقرة", "آل عمران", "النساء", "المائدة",
        "الأنعام", "الأعراف", "الأنفال", "التوبة", "يونس",
        "هود", "يوسف", "الرعد", "إبراهيم", "الحجر", "النحل",
        "الإسراء", "الكهف", "مريم", "طه", "الأنبياء", "الحج",
        "المؤمنون", "النور", "الفرقان", "الشعراء", "النمل",
        "القصص", "العنكبوت", "الروم", "لقمان", "الأحزاب",
        "سبأ", "فاطر", "يس", "الصافات", "ص", "الزمر",
        "غافر", "فصلت", "الشورى", "الزخرف", "الدخان", "الجاثية",
        "الأحقاف", "محمد", "الفتح", "الحجرات", "ق", "الذاريات",
        "الطور", "النجم", "القمر", "الرحمن", "الواقعة", "الحديد",
        "المجادلة", "الحشر", "الممتحنة", "الصف", "الجمعة",
        "المنافقون", "التغابن", "الطلاق", "التحريم", "الملك",
        "القلم", "الحاقة", "المعارج", "نوح", "الجن", "المزمل",
        "المدثر", "القيامة", "الإنسان", "المرسلات", "النبأ",
        "النازعات", "عبس", "التكوير", "الانفطار", "المطففين",
        "الانشقاق", "البروج", "الطارق", "الأعلى", "الغاشية",
        "الفجر", "البلد", "الشمس", "الليل", "الضحى", "الشرح",
        "التين", "العلق", "القدر", "البينة", "الزلزلة",
        "العاديات", "القارعة", "التكاثر", "العصر", "الهمزة",
        "الفيل", "قريش", "الماعون", "الكوثر", "الكافرون",
        "النصر", "المسد", "الإخلاص", "الفلق", "الناس"
    )
    return names.getOrElse(surahNo) { "" }
}
