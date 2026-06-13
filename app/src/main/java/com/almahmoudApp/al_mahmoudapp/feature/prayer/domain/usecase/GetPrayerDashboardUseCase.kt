package com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.model.PrayerDashboard
import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.repository.PrayerRepository
import javax.inject.Inject

class GetPrayerDashboardUseCase @Inject constructor(
    private val repository: PrayerRepository,
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<PrayerDashboard> {
        return repository.getPrayerDashboard(forceRefresh)
    }
}
