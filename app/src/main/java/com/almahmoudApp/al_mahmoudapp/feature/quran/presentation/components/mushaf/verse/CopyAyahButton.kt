package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.mushaf.verse

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.almahmoudApp.al_mahmoudapp.feature.quran.util.getSurahNameArabic

@Composable
fun CopyAyahButton(
    surahNo: Int,
    verseNo: Int,
    verseContent: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val shape = remember { RoundedCornerShape(8.dp) }
    // Resolve surah name once — getSurahNameArabic is a pure function
    val surahName = remember(surahNo) { getSurahNameArabic(surahNo) }

    Box(
        modifier = modifier
            .size(36.dp)
            .clip(shape)
            .border(width = 1.5.dp, color = primaryColor.copy(alpha = 0.5f), shape = shape)
            .clickable {
                verseContent?.let { content ->
                    copyAyahToClipboard(context, content, verseNo, surahName)
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.ContentCopy,
            contentDescription = "نسخ الآية",
            tint = primaryColor,
            modifier = Modifier.size(20.dp),
        )
    }
}

private fun copyAyahToClipboard(
    context: Context,
    content: String,
    verseNo: Int,
    surahName: String,
) {
    val copyText = "{ $content } ( $verseNo ) [ $surahName ]"
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("ayah", copyText))
    Toast.makeText(context, "تم نسخ الآية", Toast.LENGTH_SHORT).show()
}
