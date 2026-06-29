package com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.db

import android.content.Context
import androidx.room.Room
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.AyahDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.AyahWordDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.MushafDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.NavigationDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.SurahDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QuranDatabaseModule {

    @Provides
    @Singleton
    fun provideQuranAppDatabase(
        @ApplicationContext context: Context,
    ): QuranAppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            QuranAppDatabase::class.java,
            "quranapp"
        )
            .createFromAsset("db/quranapp.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSurahDao(database: QuranAppDatabase): SurahDao = database.surahDao()

    @Provides
    fun provideAyahDao(database: QuranAppDatabase): AyahDao = database.ayahDao()

    @Provides
    fun provideAyahWordDao(database: QuranAppDatabase): AyahWordDao = database.ayahWordDao()

    @Provides
    fun provideMushafDao(database: QuranAppDatabase): MushafDao = database.mushafDao()

    @Provides
    fun provideNavigationDao(database: QuranAppDatabase): NavigationDao = database.navigationDao()
}
