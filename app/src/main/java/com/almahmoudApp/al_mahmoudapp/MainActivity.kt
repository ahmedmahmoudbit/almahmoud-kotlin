package com.almahmoudApp.al_mahmoudapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.almahmoudApp.al_mahmoudapp.core.navigation.App
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val langCode = getSavedLanguage(newBase)
        val locale = Locale.forLanguageTag(langCode)
        Locale.setDefault(locale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }

    private fun getSavedLanguage(context: Context): String {
        return try {
            val prefs = context.getSharedPreferences("almahmoud_prefs", Context.MODE_PRIVATE)
            when (prefs.getString("app_language", "ARABIC")) {
                "ENGLISH" -> "en"
                else -> "ar"
            }
        } catch (_: Exception) {
            "ar"
        }
    }
}
