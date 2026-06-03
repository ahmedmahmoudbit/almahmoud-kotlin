package com.almahmoudApp.al_mahmoudapp.core.navigation

import android.net.Uri

sealed class AppDestination(val route: String) {
    data object Onboarding : AppDestination("onboarding")
    data object Home : AppDestination("home")
    data object Quran : AppDestination("quran")
    data object Ayat : AppDestination("ayat")
    data object Qotof : AppDestination("qotof")
    data object Stories : AppDestination("stories")
    data object StoriesDetails : AppDestination("stories/details?index={index}") {
        fun createRoute(index: Int): String = "stories/details?index=$index"
    }
    data object Doaa : AppDestination("doaa")
    data object Settings : AppDestination("settings")
    data object AyatSound : AppDestination("ayat/sound?topicId={topicId}") {
        fun createRoute(topicId: Int): String = "ayat/sound?topicId=$topicId"
    }

    data object QuranAction : AppDestination("quran/action?surahNumber={surahNumber}&page={page}&name={name}") {
        fun createRoute(surahNumber: Int, page: Int, name: String): String {
            return "quran/action?surahNumber=$surahNumber&page=$page&name=${Uri.encode(name)}"
        }
    }

    data object QuranReaders : AppDestination("quran/readers?surahNumber={surahNumber}&page={page}&name={name}") {
        fun createRoute(surahNumber: Int, page: Int, name: String): String {
            return "quran/readers?surahNumber=$surahNumber&page=$page&name=${Uri.encode(name)}"
        }
    }

    data object QuranText : AppDestination("quran/text?surahNumber={surahNumber}&page={page}&name={name}") {
        fun createRoute(surahNumber: Int, page: Int, name: String): String {
            return "quran/text?surahNumber=$surahNumber&page=$page&name=${Uri.encode(name)}"
        }
    }

    data object QuranAudio : AppDestination("quran/audio?readerName={readerName}&readerImage={readerImage}&audioBaseUrl={audioBaseUrl}&page={page}") {
        fun createRoute(
            readerName: String,
            readerImage: String,
            audioBaseUrl: String,
            page: Int,
        ): String {
            return buildString {
                append("quran/audio?")
                append("readerName=${Uri.encode(readerName)}")
                append("&readerImage=${Uri.encode(readerImage)}")
                append("&audioBaseUrl=${Uri.encode(audioBaseUrl)}")
                append("&page=$page")
            }
        }
    }
}
