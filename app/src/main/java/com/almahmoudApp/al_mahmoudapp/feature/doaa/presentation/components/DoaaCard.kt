package com.almahmoudApp.al_mahmoudapp.feature.doaa.presentation.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlass
import com.almahmoudApp.al_mahmoudapp.core.ui.liquid.LiquidGlassDefaults

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DoaaCard(
    text: String,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(onClick = {}, onLongClick = onLongClick),
    ) {
        LiquidGlass(
            modifier = Modifier.fillMaxWidth(),
            shape = LiquidGlassDefaults.CardShape,
            style = LiquidGlassDefaults.Soft,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }
}
