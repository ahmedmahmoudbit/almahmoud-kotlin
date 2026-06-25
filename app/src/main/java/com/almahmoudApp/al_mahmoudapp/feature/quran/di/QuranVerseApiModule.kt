package com.almahmoudApp.al_mahmoudapp.feature.quran.di

import com.almahmoudApp.al_mahmoudapp.feature.quran.data.api.QuranVerseApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QuranVerseApiModule {
    
    private const val BASE_URL = "https://quranapi.pages.dev/"

    @Provides
    @Singleton
    fun provideQuranVerseApiService(): QuranVerseApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuranVerseApiService::class.java)
    }
}
