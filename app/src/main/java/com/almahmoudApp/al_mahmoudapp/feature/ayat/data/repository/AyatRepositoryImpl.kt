package com.almahmoudApp.al_mahmoudapp.feature.ayat.data.repository

import com.almahmoudApp.al_mahmoudapp.feature.ayat.data.datasource.AyatLocalDataSource
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatAudioItem
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatTopic
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AnasheedItem
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.repository.AyatRepository
import javax.inject.Inject

class AyatRepositoryImpl @Inject constructor(
    private val localDataSource: AyatLocalDataSource,
) : AyatRepository {
    override suspend fun getTopics(): List<AyatTopic> = localDataSource.getTopics()

    override suspend fun getTopic(topicId: Int): AyatTopic? = localDataSource.getTopic(topicId)

    override suspend fun getAudioItems(topicId: Int): List<AyatAudioItem> = localDataSource.getAudioItems(topicId)

    override suspend fun getAnasheedItems(): List<AnasheedItem> = localDataSource.getAnasheedItems()

    override suspend fun getBenefitsItems(): List<AyatAudioItem> = localDataSource.getBenefitsItems()
}
