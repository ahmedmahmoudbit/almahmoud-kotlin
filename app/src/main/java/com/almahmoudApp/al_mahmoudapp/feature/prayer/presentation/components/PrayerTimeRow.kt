package com.almahmoudApp.al_mahmoudapp.feature.prayer.presentation.components

import AmiriFont
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.util.NumberLocalization
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerTimeItem
import com.almahmoudApp.al_mahmoudapp.feature.prayer.util.PrayerNames

/**
 * Row representing a single prayer with its adhan time and iqamah time.
 * The upcoming prayer is highlighted with a gold border.
 */
@Composable
fun PrayerTimeRow(
    item: PrayerTimeItem,
    isNext: Boolean,
    isPast: Boolean,
    isFriday: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val containerColor = if (isNext) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isNext) {
                    Modifier.border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(20.dp),
                    )
                } else {
                    Modifier
                },
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = PrayerNames.arabicName(item.name, isFriday = isFriday),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = AmiriFont,
                ),
                color = if (isNext) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.alphaIf(isPast && !isNext, 0.55f),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                TimeColumn(
                    label = stringResource(R.string.prayer_adhan),
                    time = item.time.cleanLocalizedTime(),
                    isPrimary = true,
                    isNext = isNext,
                    isPast = isPast,
                )
                Spacer(modifier = Modifier.width(18.dp))
                TimeColumn(
                    label = stringResource(R.string.prayer_iqamah),
                    time = item.iqamahTime.cleanLocalizedTime(),
                    isPrimary = false,
                    isNext = isNext,
                    isPast = isPast,
                )
            }
        }
    }
}

@Composable
private fun TimeColumn(
    label: String,
    time: String,
    isPrimary: Boolean,
    isNext: Boolean,
    isPast: Boolean,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontFamily = AmiriFont),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = time,
            style = if (isPrimary) {
                MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = AmiriFont,
                )
            } else {
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = AmiriFont,
                )
            },
            color = if (isNext && isPrimary) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.Center,
            modifier = Modifier.alphaIf(isPast, 0.7f),
        )
    }
}

/** Keeps only the "HH:mm" portion of a time string then localizes its digits. */
private fun String.cleanLocalizedTime(): String {
    val clean = split(" ").firstOrNull().orEmpty().ifBlank { this }
    return NumberLocalization.localize(clean)
}

/** Applies [alpha] when [condition] is true. */
private fun Modifier.alphaIf(condition: Boolean, alpha: Float): Modifier =
    if (condition) this.alpha(alpha) else this
