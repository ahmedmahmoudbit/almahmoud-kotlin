package com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerLocation
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.repository.PrayerRepository
import javax.inject.Inject

class SetPrayerManualLocationUseCase @Inject constructor(
    private val repository: PrayerRepository,
) {
    suspend operator fun invoke(location: PrayerLocation): Result<Unit> {
        return repository.setManualLocation(location)
    }
}
