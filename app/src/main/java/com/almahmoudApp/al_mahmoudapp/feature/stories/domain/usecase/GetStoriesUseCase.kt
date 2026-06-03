package com.almahmoudApp.al_mahmoudapp.feature.stories.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.model.StoryItem
import com.almahmoudApp.al_mahmoudapp.feature.stories.domain.repository.StoriesRepository
import javax.inject.Inject

class GetStoriesUseCase @Inject constructor(
    private val repository: StoriesRepository,
) {
    suspend operator fun invoke(): Result<List<StoryItem>> = runCatching {
        repository.getStories()
    }
}
