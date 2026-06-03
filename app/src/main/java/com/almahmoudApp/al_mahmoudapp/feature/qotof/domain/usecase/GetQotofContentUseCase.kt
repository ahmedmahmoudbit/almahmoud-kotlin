package com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.model.QotofContent
import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.repository.QotofRepository
import javax.inject.Inject

class GetQotofContentUseCase @Inject constructor(
    private val repository: QotofRepository,
) {
    suspend operator fun invoke(): Result<QotofContent> {
        return repository.loadQotofContent()
    }
}
