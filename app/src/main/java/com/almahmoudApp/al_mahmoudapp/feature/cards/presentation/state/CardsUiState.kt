package com.almahmoudApp.al_mahmoudapp.feature.cards.presentation.state

import android.graphics.Bitmap
import com.almahmoudApp.al_mahmoudapp.feature.cards.domain.model.CardCategory

data class CardsUiState(
    val category: CardCategory = CardCategory.WAFAYAT,
    val name: String = "",
    val eidTemplateIndex: Int = 0,
    val isBoy: Boolean = true,
    val previewBitmap: Bitmap? = null,
    val isGenerating: Boolean = false,
    val isDownloading: Boolean = false,
    val statusMessage: String? = null,
    val isSuccess: Boolean = false
)
