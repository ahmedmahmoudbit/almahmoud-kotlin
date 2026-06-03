package com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.repository

import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.model.QotofContent

interface QotofRepository {
    suspend fun loadQotofContent(): Result<QotofContent>
}
