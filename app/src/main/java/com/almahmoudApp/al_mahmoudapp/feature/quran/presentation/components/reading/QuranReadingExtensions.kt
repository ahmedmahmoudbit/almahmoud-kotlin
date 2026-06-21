package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading

import androidx.core.text.HtmlCompat

/** Converts ASCII digits of a number into Arabic-Indic numerals (٠١٢…٩). */
fun Int.toArabicNumerals(): String = toString().map(::digitToArabic).joinToString("")

private fun digitToArabic(digit: Char): Char = when (digit) {
    '0' -> '٠'
    '1' -> '١'
    '2' -> '٢'
    '3' -> '٣'
    '4' -> '٤'
    '5' -> '٥'
    '6' -> '٦'
    '7' -> '٧'
    '8' -> '٨'
    '9' -> '٩'
    else -> digit
}

/**
 * The ornamental end-of-ayah marker, built from Arabic ornamental parentheses enclosing
 * the Arabic verse number: U+FD3E (﴾) + number + U+FD3F (﴿). Intended to be rendered in
 * the `me_quran` font. Example output: ﴾٧﴿
 */
fun verseNumberMarker(verseNumber: Int): String =
    "\uFD3E${verseNumber.toArabicNumerals()}\uFD3F"

/** Strips HTML tags from tafseer/maany payloads so they render as plain text. */
fun stripHtml(text: String): String =
    HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()
