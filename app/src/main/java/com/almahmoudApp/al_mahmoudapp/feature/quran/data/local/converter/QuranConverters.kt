package com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.converter

import androidx.room.TypeConverter
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.MushafLineType
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.NavigationType
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.RevelationType

class QuranConverters {
    @TypeConverter
    fun fromRevelationType(value: RevelationType?): String? = value?.name

    @TypeConverter
    fun toRevelationType(value: String?): RevelationType? =
        value?.let { RevelationType.valueOf(it) }

    @TypeConverter
    fun fromNavigationType(value: NavigationType?): String? = value?.name

    @TypeConverter
    fun toNavigationType(value: String?): NavigationType? =
        value?.let { NavigationType.valueOf(it) }

    @TypeConverter
    fun fromMushafLineType(value: MushafLineType?): String? = value?.name

    @TypeConverter
    fun toMushafLineType(value: String?): MushafLineType? =
        value?.let { MushafLineType.valueOf(it) }
}
