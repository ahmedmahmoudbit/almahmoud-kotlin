package com.almahmoudApp.al_mahmoudapp.feature.doaa.domain.usecase

import com.almahmoudApp.al_mahmoudapp.feature.doaa.domain.repository.DoaaRepository
import javax.inject.Inject

class GetDoaaUseCase @Inject constructor(
    private val repository: DoaaRepository,
) {
    suspend operator fun invoke(): Result<List<String>> = runCatching {
        repository.getDoaa()
    }
}
