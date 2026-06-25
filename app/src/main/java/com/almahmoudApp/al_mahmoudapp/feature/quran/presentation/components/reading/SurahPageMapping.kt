package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading

/**
 * Static mapping of surah numbers to their starting page numbers.
 * This is used for navigation between surahs.
 */
object SurahPageMapping {
    val pageMap = mapOf(
        1 to 1, 2 to 2, 3 to 50, 4 to 77, 5 to 106, 6 to 128, 7 to 151,
        8 to 177, 9 to 187, 10 to 208, 11 to 221, 12 to 235, 13 to 249,
        14 to 255, 15 to 262, 16 to 267, 17 to 282, 18 to 293, 19 to 305,
        20 to 312, 21 to 322, 22 to 332, 23 to 342, 24 to 350, 25 to 359,
        26 to 367, 27 to 377, 28 to 385, 29 to 396, 30 to 404, 31 to 411,
        32 to 415, 33 to 418, 34 to 428, 35 to 434, 36 to 440, 37 to 446,
        38 to 453, 39 to 458, 40 to 467, 41 to 477, 42 to 483, 43 to 489,
        44 to 496, 45 to 499, 46 to 502, 47 to 507, 48 to 511, 49 to 515,
        50 to 518, 51 to 520, 52 to 523, 53 to 526, 54 to 528, 55 to 531,
        56 to 534, 57 to 537, 58 to 542, 59 to 545, 60 to 549, 61 to 551,
        62 to 553, 63 to 554, 64 to 556, 65 to 558, 66 to 560, 67 to 562,
        68 to 564, 69 to 566, 70 to 568, 71 to 570, 72 to 572, 73 to 574,
        74 to 575, 75 to 577, 76 to 578, 77 to 580, 78 to 582, 79 to 583,
        80 to 585, 81 to 586, 82 to 587, 83 to 587, 84 to 589, 85 to 590,
        86 to 591, 87 to 591, 88 to 592, 89 to 593, 90 to 594, 91 to 595,
        92 to 595, 93 to 596, 94 to 596, 95 to 597, 96 to 597, 97 to 598,
        98 to 598, 99 to 599, 100 to 599, 101 to 600, 102 to 600, 103 to 601,
        104 to 601, 105 to 601, 106 to 602, 107 to 602, 108 to 602, 109 to 603,
        110 to 603, 111 to 603, 112 to 604, 113 to 604, 114 to 604
    )

    /**
     * Mapping of surah numbers to Arabic names from quran_surahs.json
     */
    private val surahNamesMap = mapOf(
        1 to "الفاتحة", 2 to "البقرة", 3 to "آل عمران", 4 to "النساء", 5 to "المائدة",
        6 to "الأنعام", 7 to "الأعراف", 8 to "الأنفال", 9 to "التوبة", 10 to "يونس",
        11 to "هود", 12 to "يوسف", 13 to "الرعد", 14 to "إبراهيم", 15 to "الحجر",
        16 to "النحل", 17 to "الإسراء", 18 to "الكهف", 19 to "مريم", 20 to "طه",
        21 to "الأنبياء", 22 to "الحج", 23 to "المؤمنون", 24 to "النور", 25 to "الفرقان",
        26 to "الشعراء", 27 to "النمل", 28 to "القصص", 29 to "العنكبوت", 30 to "الروم",
        31 to "لقمان", 32 to "السجدة", 33 to "الأحزاب", 34 to "سبأ", 35 to "فاطر",
        36 to "يس", 37 to "الصافات", 38 to "ص", 39 to "الزمر", 40 to "غافر",
        41 to "فصلت", 42 to "الشورى", 43 to "الزخرف", 44 to "الدخان", 45 to "الجاثية",
        46 to "الأحقاف", 47 to "محمد", 48 to "الفتح", 49 to "الحجرات", 50 to "ق",
        51 to "الذاريات", 52 to "الطور", 53 to "النجم", 54 to "القمر", 55 to "الرحمن",
        56 to "الواقعة", 57 to "الحديد", 58 to "المجادلة", 59 to "الحشر", 60 to "الممتحنة",
        61 to "الصف", 62 to "الجمعة", 63 to "المنافقون", 64 to "التغابن", 65 to "الطلاق",
        66 to "التحريم", 67 to "الملك", 68 to "القلم", 69 to "الحاقة", 70 to "المعارج",
        71 to "نوح", 72 to "الجن", 73 to "المزمل", 74 to "المدثر", 75 to "القيامة",
        76 to "الإنسان", 77 to "المرسلات", 78 to "النبأ", 79 to "النازعات", 80 to "عبس",
        81 to "التكوير", 82 to "الانفطار", 83 to "المطففين", 84 to "الانشقاق", 85 to "البروج",
        86 to "الطارق", 87 to "الأعلى", 88 to "الغاشية", 89 to "الفجر", 90 to "البلد",
        91 to "الشمس", 92 to "الليل", 93 to "الضحى", 94 to "الشرح", 95 to "التين",
        96 to "العلق", 97 to "القدر", 98 to "البينة", 99 to "الزلزلة", 100 to "العاديات",
        101 to "القارعة", 102 to "التكاثر", 103 to "العصر", 104 to "الهمزة", 105 to "الفيل",
        106 to "قريش", 107 to "الماعون", 108 to "الكوثر", 109 to "الكافرون", 110 to "النصر",
        111 to "المسد", 112 to "الإخلاص", 113 to "الفلق", 114 to "الناس"
    )

    fun getPageForSurah(surahNumber: Int): Int {
        return pageMap[surahNumber] ?: 1
    }

    fun getSurahName(surahNumber: Int): String {
        return surahNamesMap[surahNumber] ?: ""
    }

    fun getNextSurahInfo(currentSurahNumber: Int): Triple<Int, Int, String>? {
        if (currentSurahNumber >= 114) return null
        val nextSurahNumber = currentSurahNumber + 1
        val nextPage = getPageForSurah(nextSurahNumber)
        val nextName = getSurahName(nextSurahNumber)
        return Triple(nextSurahNumber, nextPage, nextName)
    }
}
