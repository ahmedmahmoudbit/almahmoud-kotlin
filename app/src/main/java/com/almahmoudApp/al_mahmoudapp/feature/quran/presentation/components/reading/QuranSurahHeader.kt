package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading

import AmiriFont
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun QuranSurahHeader(
    surahName: String,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.08f),
                            primaryColor.copy(alpha = 0.04f),
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = primaryColor.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(24.dp),
                )
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        primaryColor.copy(alpha = 0.4f),
                                    )
                                )
                            )
                    )
                    Text(
                        text = "◆",
                        color = primaryColor,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.4f),
                                        Color.Transparent,
                                    )
                                )
                            )
                    )
                }
                
                Text(
                    text = surahName,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = AmiriFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        primaryColor.copy(alpha = 0.4f),
                                    )
                                )
                            )
                    )
                    Text(
                        text = "◆",
                        color = primaryColor,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.4f),
                                        Color.Transparent,
                                    )
                                )
                            )
                    )
                }
            }
        }
    }
}

/**
 * The Basmala as plain, centered, elegant Arabic text
 */
@Composable
fun QuranBasmala(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.size(2.dp))
        Text(
            text = stringResource(R.string.quran_basmala),
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = AmiriFont,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
