package com.almahmoudApp.al_mahmoudapp.feature.images.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.images.domain.model.IslamicImage
import com.almahmoudApp.al_mahmoudapp.feature.images.domain.repository.ImagesRepository
import javax.inject.Inject

class GetIslamicImagesUseCase @Inject constructor(
    private val repository: ImagesRepository
) {
    suspend operator fun invoke(): Result<List<IslamicImage>> {
        return repository.getIslamicImages()
    }
}
