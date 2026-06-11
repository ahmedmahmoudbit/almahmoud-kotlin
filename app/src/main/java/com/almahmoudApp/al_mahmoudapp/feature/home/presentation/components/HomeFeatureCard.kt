package com.almahmoudApp.al_mahmoudapp.feature.home.presentation.components

import LiquidGlassCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeFeature
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeFeatureKey

@Composable
fun HomeFeatureCard(
    feature: HomeFeature,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LiquidGlassCard(
        onClick = onClick,
        modifier = modifier
            .width(104.dp)
            .height(104.dp),
        cornerRadius = 24.dp,
        refraction = 0.55f,
        frost = 8f,
        dispersion = 0.35f,
        glowAlpha = 0.55f,
    ) {
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
                    )
                ),
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}


private fun HomeFeatureKey.titleRes(): Int {
    return when (this) {
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
}

private fun HomeFeatureKey.icon(): ImageVector {
    return when (this) {
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
}
