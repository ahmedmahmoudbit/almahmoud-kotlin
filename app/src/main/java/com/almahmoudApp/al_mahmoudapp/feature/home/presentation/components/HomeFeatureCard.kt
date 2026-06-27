package com.almahmoudApp.al_mahmoudapp.feature.home.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Collections
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassCard
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeFeature
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeFeatureKey

/**
 * Home feature card. When an image resource is provided via [featureImage] for the given key,
 * the card displays that image filling the entire card with a bottom scrim on which the title sits.
 * Otherwise it falls back to the icon-based layout.
 *
 * The card no longer enforces a fixed size; pass the desired [modifier] to control dimensions so the
 * card can participate in a staggered grid.
 */
@Composable
fun HomeFeatureCard(
    feature: HomeFeature,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val imageRes = feature.key.featureImage()

    LiquidGlassCard(
        onClick = onClick,
        modifier = modifier,
        cornerRadius = 24.dp,
        refraction = 0.55f,
        frost = 8f,
        dispersion = 0.35f,
        glowAlpha = 0.55f,
    ) {
        if (imageRes != null) {
            FeatureImageContent(imageRes = imageRes, feature = feature)
        } else {
            FeatureIconContent(feature = feature)
        }
    }
}

/**
 * Image-based layout: image fills the card, a bottom fade (themed) makes the title readable.
 */
@Composable
private fun FeatureImageContent(
    @DrawableRes imageRes: Int,
    feature: HomeFeature,
) {
    // The scrim fades from transparent at the top to the themed overlay color at the bottom so the
    // title stays readable on both light and dark themes.
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val scrimColor = if (isDark) Color.Black else Color.White
    val titleColor = if (isDark) Color.White else Color.Black

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // Bottom fade for the label readability.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            scrimColor.copy(alpha = 0.70f),
                            scrimColor,
                        ),
                    ),
                ),
        )

        Text(
            text = stringResource(feature.key.titleRes()),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = titleColor,
            ),
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp, vertical = 8.dp),
        )
    }
}

/**
 * Fallback icon-based layout (current design) used when no image is configured.
 */
@Composable
private fun FeatureIconContent(feature: HomeFeature) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = feature.key.icon(),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = Color.White,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(feature.key.titleRes()),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.25f),
                    offset = Offset(0f, 1f),
                    blurRadius = 3f,
                ),
            ),
            maxLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun HomeFeatureKey.titleRes(): Int = when (this) {
    HomeFeatureKey.SOUND -> R.string.home_feature_sound
    HomeFeatureKey.AYAT -> R.string.home_feature_ayat
    HomeFeatureKey.IMAGES -> R.string.home_feature_images
    HomeFeatureKey.TAMILAT -> R.string.home_feature_tamilat
    HomeFeatureKey.QOTOF -> R.string.home_feature_qotof
    HomeFeatureKey.STORIES -> R.string.home_feature_stories
    HomeFeatureKey.PRAYER -> R.string.home_feature_prayer
    HomeFeatureKey.DOAA -> R.string.home_feature_doaa
    HomeFeatureKey.AZKAR -> R.string.home_feature_azkar
    HomeFeatureKey.TASBEEH -> R.string.home_feature_tasbeeh
    HomeFeatureKey.STATUS -> R.string.home_feature_status
    HomeFeatureKey.APPS -> R.string.home_feature_apps
    HomeFeatureKey.SUGGESTIONS -> R.string.home_feature_suggestions
    HomeFeatureKey.CARDS -> R.string.home_feature_cards
}

private fun HomeFeatureKey.icon(): ImageVector = when (this) {
    HomeFeatureKey.SOUND -> Icons.Outlined.MusicNote
    HomeFeatureKey.AYAT -> Icons.AutoMirrored.Outlined.MenuBook
    HomeFeatureKey.IMAGES -> Icons.Outlined.Image
    HomeFeatureKey.TAMILAT -> Icons.Outlined.RecordVoiceOver
    HomeFeatureKey.QOTOF -> Icons.Outlined.FormatQuote
    HomeFeatureKey.STORIES -> Icons.Outlined.AutoStories
    HomeFeatureKey.PRAYER -> Icons.Outlined.Mosque
    HomeFeatureKey.DOAA -> Icons.Outlined.Favorite
    HomeFeatureKey.AZKAR -> Icons.Outlined.AccessTime
    HomeFeatureKey.TASBEEH -> Icons.Outlined.TouchApp
    HomeFeatureKey.STATUS -> Icons.Outlined.Collections
    HomeFeatureKey.APPS -> Icons.Outlined.Apps
    HomeFeatureKey.SUGGESTIONS -> Icons.Outlined.Lightbulb
    HomeFeatureKey.CARDS -> Icons.Outlined.CardGiftcard
}

/**
 * Returns the drawable resource id for a feature's background image, or null to fall back to the
 * icon. Cards 1–11 map to the first sections; remaining sections fall back to their icon.
 */
@DrawableRes
private fun HomeFeatureKey.featureImage(): Int? = when (this) {
    HomeFeatureKey.SOUND -> R.drawable.card1
    HomeFeatureKey.AYAT -> R.drawable.card2
    HomeFeatureKey.IMAGES -> R.drawable.card3
    HomeFeatureKey.TAMILAT -> R.drawable.card4
    HomeFeatureKey.QOTOF -> R.drawable.card5
    HomeFeatureKey.STORIES -> R.drawable.card6
    HomeFeatureKey.PRAYER -> R.drawable.card7
    HomeFeatureKey.DOAA -> R.drawable.card8
    HomeFeatureKey.AZKAR -> R.drawable.card9
    HomeFeatureKey.TASBEEH -> R.drawable.card10
    HomeFeatureKey.STATUS -> R.drawable.card11
    HomeFeatureKey.APPS -> null
    HomeFeatureKey.SUGGESTIONS -> null
    HomeFeatureKey.CARDS -> null
}
