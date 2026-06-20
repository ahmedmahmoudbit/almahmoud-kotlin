package com.almahmoudApp.al_mahmoudapp.feature.tamilat.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.tamilat.domain.model.TamilatItem
import com.almahmoudApp.al_mahmoudapp.feature.tamilat.domain.repository.TamilatRepository
import javax.inject.Inject

class GetTamilatUseCase @Inject constructor(
    private val repository: TamilatRepository
) {
    suspend operator fun invoke(): Result<List<TamilatItem>> {
        return repository.getReflections()
    }
}
