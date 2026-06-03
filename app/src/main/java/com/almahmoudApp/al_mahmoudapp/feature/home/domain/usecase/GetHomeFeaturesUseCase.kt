package com.almahmoudApp.al_mahmoudapp.feature.home.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeContent
import com.almahmoudApp.al_mahmoudapp.feature.home.domain.repository.HomeRepository
import javax.inject.Inject

class GetHomeFeaturesUseCase @Inject constructor(
    private val repository: HomeRepository,
) {
    suspend operator fun invoke(): Result<HomeContent> {
        return repository.loadHomeContent()
    }
}
