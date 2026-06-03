package com.almahmoudApp.al_mahmoudapp.feature.stories.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.model.StoryItem
import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.repository.StoriesRepository
import javax.inject.Inject

class GetStoryUseCase @Inject constructor(
    private val repository: StoriesRepository,
) {
    suspend operator fun invoke(index: Int): Result<StoryItem> = runCatching {
        repository.getStory(index) ?: error("Story not found")
    }
}
