package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading

import AmiriFont
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almahmoudApp.al_mahmoudapp.core.ui.components.LoadingView
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.state.QuranTextUiState
import com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.viewmodel.AudioReciterItem

/**
 * Bottom sheet content showing tafseer / maany for the selected verse, with audio player.
 */
@Composable
fun QuranVerseDetailsSheet(
    state: QuranTextUiState,
    surahName: String,
    activeTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onLoadAudio: () -> Unit,
    onPlayAudio: (String, String) -> Unit,
    onStopAudio: () -> Unit,
) {
    val selectedVerse = state.selectedVerse ?: return
    val details = state.selectedVerseDetails

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header with verse info and audio button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$surahName - الآية ${selectedVerse.verseNumber.toArabicNumerals()}",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = AmiriFont,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.weight(1f),
            )
            
            // Audio play button
            AudioPlayButton(
                state = state,
                onLoadAudio = onLoadAudio,
                onPlayAudio = onPlayAudio,
                onStopAudio = onStopAudio,
            )
        }
        
        // Reciters row (if loaded)
        if (state.availableReciters.isNotEmpty()) {
            RecitersRow(
                reciters = state.availableReciters,
                currentPlayingReciter = state.currentPlayingReciter,
                isAudioPlaying = state.isAudioPlaying,
                onReciterClick = { reciter ->
                    onPlayAudio(reciter.url, reciter.name)
                },
            )
        }
        
        // Audio loading indicator
        if (state.isAudioLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "جاري تحميل القراء...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }
        
        // Tabs
        ScrollableTabRow(
            selectedTabIndex = activeTabIndex,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTabIndex]),
                    color = MaterialTheme.colorScheme.primary,
                )
            },
        ) {
            Tab(
                selected = activeTabIndex == 0,
                onClick = { onTabSelected(0) },
                text = { Text("التفسير") },
            )
            Tab(
                selected = activeTabIndex == 1,
                onClick = { onTabSelected(1) },
                text = { Text("المعاني") },
            )
        }

        // Content
        when {
            state.isVerseDetailsLoading -> LoadingView(modifier = Modifier.fillMaxWidth())
            details == null -> state.verseDetailsErrorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            activeTabIndex == 0 -> DetailsBody(text = stripHtml(details.tafseerText))
            else -> DetailsBody(text = stripHtml(details.maanyText))
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun AudioPlayButton(
    state: QuranTextUiState,
    onLoadAudio: () -> Unit,
    onPlayAudio: (String, String) -> Unit,
    onStopAudio: () -> Unit,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(
                width = 1.5.dp,
                color = if (state.isAudioPlaying) Color(0xFF4CAF50) else primaryColor,
                shape = CircleShape,
            )
            .clickable {
                if (state.isAudioPlaying) {
                    onStopAudio()
                } else if (state.availableReciters.isEmpty()) {
                    onLoadAudio()
                } else {
                    // Play with first reciter
                    val firstReciter = state.availableReciters.first()
                    onPlayAudio(firstReciter.url, firstReciter.name)
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        if (state.isAudioLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = primaryColor,
            )
        } else if (state.isAudioPlaying) {
            Icon(
                imageVector = Icons.Rounded.Stop,
                contentDescription = "إيقاف",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(20.dp),
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.MusicNote,
                contentDescription = "تشغيل",
                tint = primaryColor,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun RecitersRow(
    reciters: List<AudioReciterItem>,
    currentPlayingReciter: String?,
    isAudioPlaying: Boolean,
    onReciterClick: (AudioReciterItem) -> Unit,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "اختر القارئ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        
        reciters.forEach { reciter ->
            val isPlaying = isAudioPlaying && currentPlayingReciter == reciter.name
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        width = if (isPlaying) 2.dp else 1.dp,
                        color = if (isPlaying) Color(0xFF4CAF50) else primaryColor.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp),
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
                        // Audio wave animation
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
                        color = if (isPlaying) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioWaveAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")
    
    val bar1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bar1",
    )
    val bar2 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bar2",
    )
    val bar3 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bar3",
    )
    
    Row(
        modifier = Modifier.height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp * bar1)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF4CAF50))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp * bar2)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF4CAF50))
        )
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp * bar3)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF4CAF50))
        )
    }
}

@Composable
private fun DetailsBody(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    )
}
