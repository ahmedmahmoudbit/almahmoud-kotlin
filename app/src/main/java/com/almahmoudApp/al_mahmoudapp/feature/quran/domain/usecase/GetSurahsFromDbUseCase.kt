package com.almahmoudApp.al_mahmoudapp.feature.quran.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.quran.data.local.entity.SurahWithLocalizations
import com.almahmoudApp.al_mahmoudapp.feature.quran.data.repository.QuranDatabaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSurahsFromDbUseCase @Inject constructor(
    private val repository: QuranDatabaseRepository,
) {
    operator fun invoke(): Flow<List<SurahWithLocalizations>> {
        return repository.getAllSurahsWithLocalizations()
    }
}
