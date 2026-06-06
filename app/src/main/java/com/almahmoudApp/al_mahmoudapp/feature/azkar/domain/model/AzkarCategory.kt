package com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model

enum class AzkarCategory(val displayName: String, val assetPath: String) {
    MORNING("اذكار الصباح", "azkar/azkar_sun.txt"),
    EVENING("اذكار المساء", "azkar/azkar_night.txt"),
    AFTER_PRAYER("اذكار بعد الصلاة", "azkar/azkar_after_salah.txt"),
    HOME("اذكار المنزل", "azkar/azkar_home.txt"),
    BATHROOM("اذكار دخول الحمام", "azkar/azkar_bathroom.txt"),
    DEAD_PRAYER("دعاء للميت", "azkar/doaa_dead.txt"),
    SLEEP("اذكار النوم", "azkar/azkar_sleep.txt"),
}
