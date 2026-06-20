package com.almahmoudApp.al_mahmoudapp.core.util

import java.util.Calendar

/**
 * Converts a Gregorian date to its approximate Hijri (tabular Islamic calendar) representation
 * without relying on java.time (which requires API 26+ or desugaring).
 *
 * Uses the standard Gregorian → Julian Day Number (Fliegel–Van Flandern) conversion followed by
 * the Julian Day → tabular Hijri algorithm (John Walker / Fourmilab). The result is typically
 * within ±1–2 days of the official Umm al-Qura calendar, which is acceptable for display purposes.
 */
object HijriDateConverter {

    private val HIJRI_MONTHS = arrayOf(
        "محرم", "صفر", "ربيع الأول", "ربيع الآخر",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوال", "ذو القعدة", "ذو الحجة",
    )

    private val HIJRI_MONTHS_EN = arrayOf(
        "Muharram", "Safar", "Rabi al-Awwal", "Rabi al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Shaaban",
        "Ramadan", "Shawwal", "Dhu al-Qidah", "Dhu al-Hijjah",
    )

    /** Result of a Hijri conversion. */
    data class HijriDate(
        val day: Int,
        val monthIndex: Int,
        val year: Int,
    ) {
        fun monthArabic(): String = HIJRI_MONTHS.getOrElse(monthIndex) { "" }
        fun monthEnglish(): String = HIJRI_MONTHS_EN.getOrElse(monthIndex) { "" }
    }

    fun fromCalendar(calendar: Calendar): HijriDate {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return fromGregorian(year, month, day)
    }

    fun fromGregorian(year: Int, month: Int, day: Int): HijriDate {
        val julianDay = toJulianDay(year, month, day)
        return julianDayToHijri(julianDay)
    }

    /** Fliegel–Van Flandern Gregorian → Julian Day Number. */
    private fun toJulianDay(year: Int, month: Int, day: Int): Long {
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        return day.toLong() + (153L * m + 2) / 5 + 365L * y + y / 4 - y / 100 + y / 400 - 32045
    }

    /** Julian Day Number → tabular Islamic (Hijri). */
    private fun julianDayToHijri(julianDay: Long): HijriDate {
        // Each Islamic year alternates between 354 and 355 days. Iterating back from the epoch-free
        // estimate gives a robust year, then the month/day are derived from the cycle position.
        var year = ((30L * (julianDay - ISLAMIC_EPOCH_JD) + 10646L) / 10631L).toInt()

        // Adjust the year forward if the computed start of that year is still ahead of the input day.
        while (julianDay >= islamicToJulianDay(year + 1, 1, 1)) {
            year++
        }
        // Adjust backward if we overshot.
        while (julianDay < islamicToJulianDay(year, 1, 1)) {
            year--
        }

        var month = 1
        while (month < 12 && julianDay >= islamicToJulianDay(year, month + 1, 1)) {
            month++
        }

        val day = (julianDay - islamicToJulianDay(year, month, 1) + 1).toInt().coerceIn(1, 30)

        return HijriDate(
            day = day,
            monthIndex = (month - 1).coerceIn(0, 11),
            year = year,
        )
    }

    /** Tabular Islamic year/month/day → Julian Day Number. */
    private fun islamicToJulianDay(year: Int, month: Int, day: Int): Long {
        return (day - 1).toLong() +
            29L * (month - 1) +
            (month / 2).toLong() +
            (year - 1) * 354L +
            (3L + 11L * year) / 30L +
            ISLAMIC_EPOCH_JD - 1L
    }

    /** Julian Day Number of 1 Muharram AH 1 (16 July 622 CE, tabular). */
    private const val ISLAMIC_EPOCH_JD = 1948440L
}
