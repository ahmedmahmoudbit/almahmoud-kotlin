package com.almahmoudApp.al_mahmoudapp.core.di

import com.almahmoudApp.al_mahmoudapp.feature.prayer.data.remote.PrayerApiService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object PrayerNetworkModule {
    @Provides
    @Singleton
    fun providePrayerGson(): Gson = Gson()

    @Provides
    @Singleton
    fun providePrayerRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(PRAYER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePrayerApiService(retrofit: Retrofit): PrayerApiService {
        return retrofit.create(PrayerApiService::class.java)
    }

    private const val PRAYER_BASE_URL = "https://api.aladhan.com/"
}
