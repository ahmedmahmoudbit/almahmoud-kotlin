package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.quran.data.repository.QuranDatabaseRepository
import javax.inject.Inject

class GetMushafPageCountUseCase @Inject constructor(
    private val repository: QuranDatabaseRepository,
) {
    suspend operator fun invoke(): Result<Int> = runCatching {
        repository.getNumberOfPages()
    }
}
