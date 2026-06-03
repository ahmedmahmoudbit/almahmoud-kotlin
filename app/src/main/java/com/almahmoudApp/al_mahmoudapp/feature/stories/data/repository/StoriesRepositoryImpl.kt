package com.almahmoudApp.al_mahmoudapp.feature.stories.data.repository

import com.almahmoudApp.al_mahmoudapp.feature.stories.data.datasource.StoriesLocalDataSource
import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.model.StoryItem
import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.repository.StoriesRepository
import javax.inject.Inject

class StoriesRepositoryImpl @Inject constructor(
    private val localDataSource: StoriesLocalDataSource,
) : StoriesRepository {
    override suspend fun getStories(): List<StoryItem> = localDataSource.getStories()

    override suspend fun getStory(index: Int): StoryItem? = localDataSource.getStory(index)
}
