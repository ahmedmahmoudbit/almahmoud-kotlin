package com.almahmoudApp.al_mahmoudapp.feature.stories.domain.repository

import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.model.StoryItem

interface StoriesRepository {
    suspend fun getStories(): List<StoryItem>
    suspend fun getStory(index: Int): StoryItem?
}
