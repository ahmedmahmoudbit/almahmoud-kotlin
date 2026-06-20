package com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.prayer.domain.repository.PrayerAyahRepository
import javax.inject.Inject

class GetRandomPrayerAyahUseCase @Inject constructor(
    private val repository: PrayerAyahRepository,
) {
    operator fun invoke(): String = repository.randomAyah()
}
