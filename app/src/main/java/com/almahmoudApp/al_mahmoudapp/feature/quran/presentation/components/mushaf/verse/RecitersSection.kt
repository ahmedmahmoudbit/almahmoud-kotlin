package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.mushaf.verse

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.AudioReciterItem

private val ReciterGreenColor = Color(0xFF4CAF50)
private val ItemShape = RoundedCornerShape(8.dp)

/**
 * Shows the list of available reciters.
 * Uses LazyColumn instead of forEach to avoid composing off-screen items
 * when there are many reciters.
 */
@Composable
fun RecitersSection(
    reciters: List<AudioReciterItem>,
    currentPlayingReciter: String?,
    isAudioPlaying: Boolean,
    onReciterClick: (AudioReciterItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "اختر القارئ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            // Disable LazyColumn's own scrolling; the parent BottomSheet scrolls
            userScrollEnabled = true,
        ) {
            items(
                items = reciters,
                key = { it.id },
            ) { reciter ->
                ReciterItem(
                    reciter = reciter,
                    isPlaying = isAudioPlaying && currentPlayingReciter == reciter.name,
                    primaryColor = primaryColor,
                    onReciterClick = onReciterClick,
                )
            }
        }
    }
}

@Composable
private fun ReciterItem(
    reciter: AudioReciterItem,
    isPlaying: Boolean,
    primaryColor: Color,
    onReciterClick: (AudioReciterItem) -> Unit,
) {
    val borderColor = remember(isPlaying) {
        if (isPlaying) ReciterGreenColor else primaryColor.copy(alpha = 0.3f)
    }
    val textColor = remember(isPlaying) {
        if (isPlaying) ReciterGreenColor else null // null = use onSurface below
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ItemShape)
            .border(
                width = if (isPlaying) 2.dp else 1.dp,
                color = borderColor,
                shape = ItemShape,
            )
            .clickable { onReciterClick(reciter) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (isPlaying) {
                AudioWaveAnimation()
            } else {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(20.dp),
                )
            }

            Text(
                text = reciter.name,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor ?: MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
