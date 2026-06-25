package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading

import AmiriFont
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.automirrored.rounded.ArrowLeft
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almahmoudApp.al_mahmoudapp.R

/**
 * Mapping of surah numbers to Arabic names from quran_surahs.json
 */
private val surahNamesMap = mapOf(
    1 to "الفاتحة", 2 to "البقرة", 3 to "آل عمران", 4 to "النساء", 5 to "المائدة",
    6 to "الأنعام", 7 to "الأعراف", 8 to "الأنفال", 9 to "التوبة", 10 to "يونس",
    11 to "هود", 12 to "يوسف", 13 to "الرعد", 14 to "إبراهيم", 15 to "الحجر",
    16 to "النحل", 17 to "الإسراء", 18 to "الكهف", 19 to "مريم", 20 to "طه",
    21 to "الأنبياء", 22 to "الحج", 23 to "المؤمنون", 24 to "النور", 25 to "الفرقان",
    26 to "الشعراء", 27 to "النمل", 28 to "القصص", 29 to "العنكبوت", 30 to "الروم",
    31 to "لقمان", 32 to "السجدة", 33 to "الأحزاب", 34 to "سبأ", 35 to "فاطر",
    36 to "يس", 37 to "الصافات", 38 to "ص", 39 to "الزمر", 40 to "غافر",
    41 to "فصلت", 42 to "الشورى", 43 to "الزخرف", 44 to "الدخان", 45 to "الجاثية",
    46 to "الأحقاف", 47 to "محمد", 48 to "الفتح", 49 to "الحجرات", 50 to "ق",
    51 to "الذاريات", 52 to "الطور", 53 to "النجم", 54 to "القمر", 55 to "الرحمن",
    56 to "الواقعة", 57 to "الحديد", 58 to "المجادلة", 59 to "الحشر", 60 to "الممتحنة",
    61 to "الصف", 62 to "الجمعة", 63 to "المنافقون", 64 to "التغابن", 65 to "الطلاق",
    66 to "التحريم", 67 to "الملك", 68 to "القلم", 69 to "الحاقة", 70 to "المعارج",
    71 to "نوح", 72 to "الجن", 73 to "المزمل", 74 to "المدثر", 75 to "القيامة",
    76 to "الإنسان", 77 to "المرسلات", 78 to "النبأ", 79 to "النازعات", 80 to "عبس",
    81 to "التكوير", 82 to "الانفطار", 83 to "المطففين", 84 to "الانشقاق", 85 to "البروج",
    86 to "الطارق", 87 to "الأعلى", 88 to "الغاشية", 89 to "الفجر", 90 to "البلد",
    91 to "الشمس", 92 to "الليل", 93 to "الضحى", 94 to "الشرح", 95 to "التين",
    96 to "العلق", 97 to "القدر", 98 to "البينة", 99 to "الزلزلة", 100 to "العاديات",
    101 to "القارعة", 102 to "التكاثر", 103 to "العصر", 104 to "الهمزة", 105 to "الفيل",
    106 to "قريش", 107 to "الماعون", 108 to "الكوثر", 109 to "الكافرون", 110 to "النصر",
    111 to "المسد", 112 to "الإخلاص", 113 to "الفلق", 114 to "الناس"
)

/**
 * Footer at the end of each surah showing the next surah name.
 * Only shown if there is a next surah (surahNumber < 114).
 */
@Composable
fun NextSurahFooter(
    currentSurahNumber: Int,
    onNextSurahClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (currentSurahNumber >= 114) return
    val nextSurahNumber = currentSurahNumber + 1
    val nextSurahName = surahNamesMap[nextSurahNumber] ?: ""
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onNextSurahClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(R.string.quran_next_surah),
                color = primaryColor.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.width(10.dp))

            Icon(
                imageVector = Icons.Rounded.FastRewind,
                contentDescription = null,
                tint = primaryColor.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp),
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = nextSurahName,
                color = primaryColor,
                fontFamily = AmiriFont,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
            )

        }
    }
}
