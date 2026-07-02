package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.mushaf.verse

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.MushafUiState

private val AudioGreenColor = Color(0xFF4CAF50)

@Composable
fun AudioPlayButton(
    state: MushafUiState,
    onPlayAudio: (String, String) -> Unit,
    onStopAudio: () -> Unit,
    onLoadAudio: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val borderColor = remember(state.isAudioPlaying) {
        if (state.isAudioPlaying) AudioGreenColor else primaryColor
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(width = 1.5.dp, color = borderColor, shape = CircleShape)
            .clickable {
                when {
                    state.isAudioPlaying -> onStopAudio()
                    state.availableReciters.isNotEmpty() -> {
                        val firstReciter = state.availableReciters.first()
                        onPlayAudio(firstReciter.url, firstReciter.name)
                    }
                    !state.isAudioLoading -> onLoadAudio()
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.isAudioLoading -> CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = primaryColor,
            )
            state.isAudioPlaying -> Icon(
                imageVector = Icons.Rounded.Stop,
                contentDescription = "إيقاف",
                tint = AudioGreenColor,
                modifier = Modifier.size(20.dp),
            )
            else -> Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "تشغيل",
                tint = primaryColor,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
