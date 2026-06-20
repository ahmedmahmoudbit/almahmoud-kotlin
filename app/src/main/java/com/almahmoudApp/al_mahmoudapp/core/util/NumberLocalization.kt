package com.almahmoudApp.al_mahmoudapp.core.util

import java.util.Locale

/**
 * Helpers to render Western (Latin) digits as Arabic-Indic digits (٠١٢…٩) when the active
 * locale is Arabic, leaving them untouched otherwise. Used so that prayer/clock times share the
 * app language in both RTL and LTR.
 */
object NumberLocalization {

    private val arabicIndicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    /** True when the active locale's language is Arabic. */
    fun isArabic(): Boolean = Locale.getDefault().language.equals("ar", ignoreCase = true)

    /**
     * Converts any Latin digits found in [text] to Arabic-Indic digits when the current locale is
     * Arabic; otherwise returns the text unchanged.
     */
    fun localize(text: String): String {
        if (!isArabic() || text.isEmpty()) return text
        val out = CharArray(text.length)
        for (i in text.indices) {
            val c = text[i]
            out[i] = if (c in '0'..'9') arabicIndicDigits[c - '0'] else c
        }
        return String(out)
    }
}
