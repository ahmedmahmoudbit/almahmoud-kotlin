package com.almahmoudApp.al_mahmoudapp.feature.prayer.data.repository

import com.almahmoudApp.al_mahmoudapp.feature.prayer.data.datasource.PrayerAyahDataSource
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.repository.PrayerAyahRepository
import javax.inject.Inject
import kotlin.random.Random

class PrayerAyahRepositoryImpl @Inject constructor(
    private val dataSource: PrayerAyahDataSource,
) : PrayerAyahRepository {
    private val ayat by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { dataSource.ayat() }

    override fun randomAyah(): String {
        val list = ayat
        if (list.isEmpty()) return ""
        return list[Random.nextInt(list.size)]
    }

    override fun allAyat(): List<String> = ayat
}
