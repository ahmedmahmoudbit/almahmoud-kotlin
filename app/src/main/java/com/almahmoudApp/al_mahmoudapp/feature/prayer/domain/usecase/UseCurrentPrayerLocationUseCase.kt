package com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.repository.PrayerRepository
import javax.inject.Inject

class UseCurrentPrayerLocationUseCase @Inject constructor(
    private val repository: PrayerRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.useCurrentLocation()
    }
}
