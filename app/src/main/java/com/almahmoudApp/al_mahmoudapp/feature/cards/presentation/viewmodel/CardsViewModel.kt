package com.almahmoudApp.al_mahmoudapp.feature.cards.presentation.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.R
import com.almahmoudApp.al_mahmoudapp.feature.cards.domain.model.CardCategory
import com.almahmoudApp.al_mahmoudapp.feature.cards.presentation.state.CardsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(CardsUiState())
    val state: StateFlow<CardsUiState> = _state.asStateFlow()

    fun onCategoryChanged(category: CardCategory) {
        _state.update { it.copy(category = category, previewBitmap = null, isSuccess = false, statusMessage = null) }
    }

    fun onNameChanged(name: String) {
        _state.update { it.copy(name = name, isSuccess = false, statusMessage = null) }
    }

    fun onEidTemplateChanged(index: Int) {
        _state.update { it.copy(eidTemplateIndex = index, previewBitmap = null, isSuccess = false, statusMessage = null) }
    }

    fun onGenderChanged(isBoy: Boolean) {
        _state.update { it.copy(isBoy = isBoy, previewBitmap = null, isSuccess = false, statusMessage = null) }
    }

    fun onEidImageSelected(uri: Uri) {
        _state.update { it.copy(eidImageUri = uri, previewBitmap = null, isSuccess = false, statusMessage = null) }
    }

    fun onEidPopupDismissed() {
        _state.update { it.copy(showEidPopup = false) }
    }

    fun onEidScaleChanged(scale: Float) {
        _state.update { it.copy(eidScale = scale.coerceIn(0.5f, 3f)) }
    }

    fun onEidOffsetChanged(dx: Float, dy: Float) {
        _state.update { it.copy(eidOffsetX = it.eidOffsetX + dx, eidOffsetY = it.eidOffsetY + dy) }
    }

    fun openEidPopup() {
        val s = _state.value
        val uri = s.eidImageUri ?: return
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val userBitmap = context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                } ?: return@launch

                val frameResId = when (s.eidTemplateIndex) {
                    0 -> R.drawable.eid1
                    1 -> R.drawable.eid2
                    2 -> R.drawable.eid3
                    3 -> R.drawable.eid4
                    else -> R.drawable.eid5
                }
                val frameBitmap = BitmapFactory.decodeResource(context.resources, frameResId)

                _state.update {
                    it.copy(
                        showEidPopup = true,
                        eidUserBitmap = userBitmap,
                        eidFrameBitmap = frameBitmap,
                        eidScale = 1f,
                        eidOffsetX = 0f,
                        eidOffsetY = 0f,
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(statusMessage = "فشل تحميل الصورة") }
            }
        }
    }

    fun saveEidComposite() {
        val s = _state.value
        val userBitmap = s.eidUserBitmap ?: return
        val frameBitmap = s.eidFrameBitmap ?: return
        val name = s.name.trim()

        _state.update { it.copy(isDownloading = true) }

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = compositeEidImage(
                    userBitmap = userBitmap,
                    frameBitmap = frameBitmap,
                    scale = s.eidScale,
                    offsetX = s.eidOffsetX,
                    offsetY = s.eidOffsetY,
                    name = name,
                    templateIndex = s.eidTemplateIndex,
                )

                withContext(Dispatchers.IO) {
                    val fileName = "al_mahmoud_eid_${System.currentTimeMillis()}.jpg"
                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/المحمود")
                        }
                    }
                    val uri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                    ) ?: throw Exception("تعذر إنشاء ملف الصورة")
                    val outputStream = context.contentResolver.openOutputStream(uri)
                        ?: throw Exception("تعذر فتح مسار حفظ الصورة")
                    outputStream.use {
                        result.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    }
                }

                _state.update {
                    it.copy(
                        isDownloading = false,
                        isSuccess = true,
                        statusMessage = "تم حفظ بطاقة التهنئة بنجاح"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(isDownloading = false, statusMessage = "فشل: ${e.message}")
                }
            }
        }
    }

    private fun compositeEidImage(
        userBitmap: Bitmap,
        frameBitmap: Bitmap,
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        name: String,
        templateIndex: Int,
    ): Bitmap {
        val frameW = frameBitmap.width
        val frameH = frameBitmap.height

        val result = Bitmap.createBitmap(frameW, frameH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)

        val scaledW = (userBitmap.width * scale).toInt().coerceAtLeast(1)
        val scaledH = (userBitmap.height * scale).toInt().coerceAtLeast(1)
        val scaledUser = Bitmap.createScaledBitmap(userBitmap, scaledW, scaledH, true)

        val drawX = ((frameW - scaledW) / 2f + offsetX).toInt()
        val drawY = ((frameH - scaledH) / 2f + offsetY).toInt()
        canvas.drawBitmap(scaledUser, drawX.toFloat(), drawY.toFloat(), null)
        canvas.drawBitmap(frameBitmap, 0f, 0f, null)

        if (name.isNotEmpty()) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
            }
            val typeface = try {
                ResourcesCompat.getFont(context, R.font.amiri_bold)
            } catch (e: Exception) {
                Typeface.DEFAULT_BOLD
            }
            paint.typeface = typeface

            val textColor = when (templateIndex) {
                0, 1 -> Color.rgb(212, 175, 55)
                2 -> Color.WHITE
                else -> Color.BLACK
            }
            paint.color = textColor
            paint.textSize = 60f
            paint.setShadowLayer(8f, 0f, 2f, Color.BLACK)

            val textY = if (templateIndex == 0) frameH * 0.85f else frameH * 0.80f
            canvas.drawText(name, frameW / 2f, textY, paint)
        }

        return result
    }

    fun generatePreview() {
        val s = _state.value
        if (s.category != CardCategory.EID_FITR) {
            val name = s.name.trim()
            if (name.isEmpty()) {
                _state.update { it.copy(statusMessage = "الرجاء إدخال الاسم أولاً") }
                return
            }
        }

        _state.update { it.copy(isGenerating = true, statusMessage = null) }

        viewModelScope.launch(Dispatchers.Default) {
            try {
                val category = _state.value.category
                val name = _state.value.name.trim()
                val isBoy = _state.value.isBoy
                val eidIndex = _state.value.eidTemplateIndex
                val eidImageUri = _state.value.eidImageUri

                val originalBitmap = if (category == CardCategory.EID_FITR && eidImageUri != null) {
                    val inputStream = context.contentResolver.openInputStream(eidImageUri)
                    BitmapFactory.decodeStream(inputStream)
                        ?: throw Exception("تعذر تحميل الصورة المرفوعة")
                } else {
                    val drawableRes = when (category) {
                        CardCategory.WAFAYAT -> R.drawable.die
                        CardCategory.SADAKA -> R.drawable.sadaka
                        CardCategory.MAWALEED -> if (isBoy) R.drawable.boy else R.drawable.girl
                        CardCategory.EID_FITR -> R.drawable.eid1
                    }
                    val options = BitmapFactory.Options().apply { inMutable = true }
                    BitmapFactory.decodeResource(context.resources, drawableRes, options)
                        ?: throw Exception("تعذر تحميل قالب البطاقة")
                }

                val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutableBitmap)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    textAlign = Paint.Align.CENTER
                    isFakeBoldText = true
                }

                // Load custom font amiri_bold
                val typeface = try {
                    ResourcesCompat.getFont(context, R.font.amiri_bold)
                } catch (e: Exception) {
                    Typeface.DEFAULT_BOLD
                }
                paint.typeface = typeface

                val width = mutableBitmap.width.toFloat()
                val height = mutableBitmap.height.toFloat()
                val x = width / 2f

                // Layout properties depending on category
                val (y, fontSize, color, hasShadow) = when (category) {
                    CardCategory.WAFAYAT -> {
                        Quad(height * 0.75f, 75f, Color.WHITE, true)
                    }
                    CardCategory.SADAKA -> {
                        Quad(height * 0.11f, 35f, Color.BLACK, false)
                    }
                    CardCategory.MAWALEED -> {
                        Quad(height * 0.55f, 50f, Color.BLACK, false)
                    }
                    CardCategory.EID_FITR -> {
                        // Custom styling for Eid: golden yellow or white text near bottom
                        val textY = if (eidIndex == 0) height * 0.85f else height * 0.80f
                        val textColor = when (eidIndex) {
                            0, 1 -> Color.rgb(212, 175, 55) // Gold
                            2 -> Color.WHITE
                            else -> Color.BLACK
                        }
                        Quad(textY, 60f, textColor, true)
                    }
                }

                paint.textSize = fontSize
                paint.color = color
                if (hasShadow) {
                    paint.setShadowLayer(8f, 0f, 2f, Color.BLACK)
                } else {
                    paint.clearShadowLayer()
                }

                // Draw name (wrapping if long)
                if (name.isNotEmpty()) {
                    val maxWidth = width * 0.85f
                    val textWidth = paint.measureText(name)

                    if (textWidth > maxWidth) {
                        val words = name.split(" ")
                        if (words.size > 1) {
                            val line1 = StringBuilder()
                            val line2 = StringBuilder()
                            val half = words.size / 2
                            for (i in words.indices) {
                                if (i < half) {
                                    if (line1.isNotEmpty()) line1.append(" ")
                                    line1.append(words[i])
                                } else {
                                    if (line2.isNotEmpty()) line2.append(" ")
                                    line2.append(words[i])
                                }
                            }

                            var currentSize = fontSize
                            paint.textSize = currentSize
                            while ((paint.measureText(line1.toString()) > maxWidth || paint.measureText(line2.toString()) > maxWidth) && currentSize > 25f) {
                                currentSize -= 4f
                                paint.textSize = currentSize
                            }

                            val lineSpacing = paint.textSize * 1.2f
                            canvas.drawText(line1.toString(), x, y - (lineSpacing / 2), paint)
                            canvas.drawText(line2.toString(), x, y + (lineSpacing / 2), paint)
                        } else {
                            var currentSize = fontSize
                            paint.textSize = currentSize
                            while (paint.measureText(name) > maxWidth && currentSize > 25f) {
                                currentSize -= 4f
                                paint.textSize = currentSize
                            }
                            canvas.drawText(name, x, y, paint)
                        }
                    } else {
                        canvas.drawText(name, x, y, paint)
                    }
                }

                _state.update {
                    it.copy(
                        previewBitmap = mutableBitmap,
                        isGenerating = false,
                        statusMessage = "تم إنشاء المعاينة بنجاح"
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(
                        isGenerating = false,
                        statusMessage = "فشل في إنشاء المعاينة: ${e.message}"
                    )
                }
            }
        }
    }

    fun saveToGallery() {
        val bitmap = _state.value.previewBitmap
        if (bitmap == null) {
            _state.update { it.copy(statusMessage = "الرجاء إنشاء المعاينة أولاً") }
            return
        }

        _state.update { it.copy(isDownloading = true, isSuccess = false, statusMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fileName = "al_mahmoud_card_${System.currentTimeMillis()}.jpg"
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/المحمود")
                    }
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: throw Exception("تعذر إنشاء ملف الصورة")

                val outputStream: OutputStream = context.contentResolver.openOutputStream(uri)
                    ?: throw Exception("تعذر فتح مسار حفظ الصورة")

                outputStream.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }

                _state.update {
                    it.copy(
                        isDownloading = false,
                        isSuccess = true,
                        statusMessage = "تم حفظ الصورة بنجاح في الاستوديو"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(
                        isDownloading = false,
                        isSuccess = false,
                        statusMessage = "فشل في حفظ الصورة: ${e.message}"
                    )
                }
            }
        }
    }

    // Helper holder
    private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
