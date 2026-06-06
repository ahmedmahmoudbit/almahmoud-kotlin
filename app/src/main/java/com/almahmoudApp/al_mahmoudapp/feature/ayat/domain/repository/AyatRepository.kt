package com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.repository

import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatAudioItem
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AyatTopic
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.model.AnasheedItem

interface AyatRepository {
    suspend fun getTopics(): List<AyatTopic>
    suspend fun getTopic(topicId: Int): AyatTopic?
    suspend fun getAudioItems(topicId: Int): List<AyatAudioItem>
    suspend fun getAnasheedItems(): List<AnasheedItem>
    suspend fun getBenefitsItems(): List<AyatAudioItem>
}
