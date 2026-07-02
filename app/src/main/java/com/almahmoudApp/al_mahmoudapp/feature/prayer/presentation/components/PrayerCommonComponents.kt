package com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.components

import AmiriFont
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.core.util.NumberLocalization

/** A muted color used as the gold accent across the prayer feature. */
val PrayerGold = Color(0xFFFFD54F)

/**
 * Header block for the prayer screen: shows the Hijri date, Gregorian date, and live current time
 * together without a container. The time is the dominant element; the dates sit compactly above it;
 * everything is white.
 */
@Composable
fun PrayerDateHeader(
    dayName: String,
    hijriDate: String,
    gregorianDate: String,
    currentTime: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (dayName.isNotBlank()) {
            Text(
                text = NumberLocalization.localize(dayName),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontFamily = AmiriFont),
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
        Text(
            text = NumberLocalization.localize(hijriDate),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, fontFamily = AmiriFont),
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Text(
            text = NumberLocalization.localize(gregorianDate),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
        )
        AnimatedContent(
            targetState = currentTime,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "clock",
            modifier = Modifier.padding(top = 4.dp),
        ) { time ->
            Text(
                text = time,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = AmiriFont,
                ),
                color = Color.White,
            )
        }
    }
}

/**
 * Single-line, auto-scrolling Quran verse display without any container background.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PrayerAyahLine(
    ayah: String,
    modifier: Modifier = Modifier,
) {
    if (ayah.isBlank()) return

    AnimatedContent(
        targetState = ayah,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "ayah",
        modifier = modifier.fillMaxWidth(),
    ) { verse ->
        Text(
            text = "﴿ $verse ﴾",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                fontFamily = AmiriFont,
                textDirection = TextDirection.Rtl,
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .basicMarquee(
                    iterations = Int.MAX_VALUE,
                    velocity = 45.dp,
                )
                .padding(vertical = 4.dp),
        )
    }
}
