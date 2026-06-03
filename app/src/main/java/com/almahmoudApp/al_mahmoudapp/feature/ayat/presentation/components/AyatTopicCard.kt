package com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlass
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassDefaults
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatTopic

@Composable
fun AyatTopicCard(
    topic: AyatTopic,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val glow by animateFloatAsState(targetValue = 1f, label = "topicGlow")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        LiquidGlass(
            modifier = Modifier.fillMaxWidth(),
            shape = LiquidGlassDefaults.CardShape,
            style = LiquidGlassDefaults.Frosted,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = topic.title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = topic.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f * glow),
                    modifier = Modifier.size(28.dp).alpha(0.9f),
                )
            }
        }
    }
}
