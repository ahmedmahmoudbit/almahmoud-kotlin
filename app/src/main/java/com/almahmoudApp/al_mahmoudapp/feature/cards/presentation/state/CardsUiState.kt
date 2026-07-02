package com.almahmoudApp.al_mahmoudapp.feature.cards.presentation.state

import android.graphics.Bitmap
import android.net.Uri
import com.almahmoudApp.al_mahmoudapp.feature.cards.domain.model.CardCategory

data class CardsUiState(
    val category: CardCategory = CardCategory.WAFAYAT,
    val name: String = "",
    val eidTemplateIndex: Int = 0,
    val isBoy: Boolean = true,
    val eidImageUri: Uri? = null,
    val previewBitmap: Bitmap? = null,
    val isGenerating: Boolean = false,
    val isDownloading: Boolean = false,
    val statusMessage: String? = null,
    val isSuccess: Boolean = false,
    val showEidPopup: Boolean = false,
    val eidUserBitmap: Bitmap? = null,
    val eidFrameBitmap: Bitmap? = null,
    val eidScale: Float = 1f,
    val eidOffsetX: Float = 0f,
    val eidOffsetY: Float = 0f,
)
