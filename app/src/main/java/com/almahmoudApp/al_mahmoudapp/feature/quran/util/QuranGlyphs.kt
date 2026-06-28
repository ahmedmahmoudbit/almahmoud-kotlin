package com.almahmoudApp.al_mahmoudapp.feature.quran.util

object QuranGlyphs {
    object Special {
        const val BISMILLAH = "\uFDFD"
        const val MECCAN = "\uE073"
        const val MEDINAN = "\uE075"
        const val SEJDA = "\u06E9"
        const val TITLE_FRAME = "\uE000"
    }

    /**
     * Normalizes verse numbers to Arabic-Indic digits (٠-٩).
     * Handles multiple numeral systems:
     * - ASCII digits (0-9)
     * - Arabic-Indic digits (٠-٩) U+0660-U+0669
     * - Extended Arabic-Indic digits (۰-۹) U+06F0-U+06F9
     */
    fun normalizeAyahNumber(text: String): String {
        return text.map { char ->
            when {
                char in '0'..'9' -> {
                    val digit = char - '0'
                    (0x0660 + digit).toChar()
                }
                char in '\u06F0'..'\u06F9' -> {
                    val digit = char - '\u06F0'
                    (0x0660 + digit).toChar()
                }
                else -> char
            }
        }.joinToString("")
    }

    /**
     * Checks if text represents a verse number (contains only Arabic-Indic digits).
     */
    fun isAyahNumber(text: String): Boolean {
        return text.isNotBlank() && text.all { it in '\u0660'..'\u0669' }
    }

    /**
     * Wraps verse numbers with proper directional marks to prevent reversal.
     * Uses LRM (U+200E) to ensure proper LTR display of numbers.
     */
    fun formatAyahNumberForDisplay(text: String): String {
        val lrm = '\u200E'  // Left-to-Right Mark
        return "$lrm$text$lrm"
    }
}
