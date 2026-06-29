package com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SurahWithLocalizations(
    @Embedded
    val surah: SurahEntity,

    @Relation(
        parentColumn = "surah_no",
        entityColumn = "surah_no"
    )
    val localizations: List<SurahLocalizationEntity>
) {
    fun getNameForLocale(languageCode: String): String {
        return localizations.firstOrNull {
            it.langCode == languageCode && !it.name.isNullOrBlank()
        }?.name.orEmpty()
    }

    fun getMeaningForLocale(languageCode: String): String {
        return localizations.firstOrNull {
            it.langCode == languageCode && !it.meaning.isNullOrBlank()
        }?.meaning.orEmpty()
    }

    fun getBestName(): String {
        val fallbackCodes = listOf("ar", "en")
        for (code in fallbackCodes) {
            val name = getNameForLocale(code)
            if (name.isNotBlank()) return name
        }
        return localizations.firstOrNull { !it.name.isNullOrBlank() }?.name.orEmpty()
    }
}
