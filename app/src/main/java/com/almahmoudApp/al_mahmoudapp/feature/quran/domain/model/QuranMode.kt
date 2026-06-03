package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.model

import androidx.annotation.StringRes
import com.almahmoudApp.al_mahmoudapp.R

enum class QuranMode(
    @param:StringRes val titleRes: Int,
) {
    TEXT(R.string.quran_tab_text),
    AUDIO(R.string.quran_tab_audio),
}
