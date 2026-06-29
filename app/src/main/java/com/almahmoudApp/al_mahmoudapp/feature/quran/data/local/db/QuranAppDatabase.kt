package com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.converter.QuranConverters
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.AyahDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.AyahWordDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.MushafDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.NavigationDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.dao.SurahDao
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.AyahEntity
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.AyahWordEntity
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.MushafEntity
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.MushafMapEntity
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.NavigationRangeEntity
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.ScriptEntity
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.SurahEntity
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.SurahLocalizationEntity

@Database(
    entities = [
        SurahEntity::class,
        SurahLocalizationEntity::class,
        AyahEntity::class,
        ScriptEntity::class,
        AyahWordEntity::class,
        NavigationRangeEntity::class,
        MushafEntity::class,
        MushafMapEntity::class,
    ],
    version = 10,
    exportSchema = false
)
@TypeConverters(QuranConverters::class)
abstract class QuranAppDatabase : RoomDatabase() {
    abstract fun surahDao(): SurahDao
    abstract fun ayahDao(): AyahDao
    abstract fun ayahWordDao(): AyahWordDao
    abstract fun mushafDao(): MushafDao
    abstract fun navigationDao(): NavigationDao
}
