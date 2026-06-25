package com.almahmoudApp.al_mahmoudapp.feature.quran.presentation.components.reading

/**
 * Utility object for Arabic text processing, specifically for removing
 * diacritics (tashkeel) to enable search without tashkeel.
 */
object ArabicTextUtils {
    
    // Arabic diacritics (tashkeel) characters - comprehensive list
    private val diacriticsRegex = Regex(
        "[\u0610-\u061A\u064B-\u065F\u0670\u06D6-\u06DC\u06DF-\u06E4\u06E7\u06E8\u06EA-\u06ED\u08D4-\u08E1\u08E3-\u08FF]"
    )
    
    /**
     * Remove all diacritics (tashkeel) from Arabic text.
     */
    fun removeDiacritics(text: String): String {
        return text.replace(diacriticsRegex, "")
    }
    
    /**
     * Normalize Arabic text for search purposes.
     * Removes diacritics and normalizes some characters.
     */
    fun normalizeForSearch(text: String): String {
        var normalized = removeDiacritics(text)
        
        // Normalize Alef variations
        normalized = normalized.replace('\u0622', '\u0627') // آ (Alef with Madda above) -> ا
        normalized = normalized.replace('\u0623', '\u0627') // أ (Alef with Hamza above) -> ا
        normalized = normalized.replace('\u0625', '\u0627') // إ (Alef with Hamza below) -> ا
        normalized = normalized.replace('\u0671', '\u0627') // ٱ (Alef Wasla) -> ا
        
        // Normalize Hamza variations
        normalized = normalized.replace('\u0624', '\u0648') // ؤ (Waw with Hamza above) -> و
        normalized = normalized.replace('\u0626', '\u064A') // ئ (Yeh with Hamza above) -> ي
        
        // Normalize Teh Marbuta
        normalized = normalized.replace('\u0629', '\u0647') // ة (Teh Marbuta) -> ه
        
        // Normalize Yeh
        normalized = normalized.replace('\u0649', '\u064A') // ى (Alef Maksura) -> ي
        
        // Remove tatweel (kashida)
        normalized = normalized.replace('\u0640', ' ')
        
        // Remove extra spaces
        normalized = normalized.replace("\\s+".toRegex(), " ").trim()
        
        return normalized
    }
    
    /**
     * Check if a verse text contains the search query.
     * Uses normalized text for matching (without diacritics).
     */
    fun matchesSearch(verseText: String, searchQuery: String): Boolean {
        if (searchQuery.isBlank()) return true
        
        val normalizedVerse = normalizeForSearch(verseText)
        val normalizedQuery = normalizeForSearch(searchQuery)
        
        return normalizedVerse.contains(normalizedQuery, ignoreCase = true)
    }
}
