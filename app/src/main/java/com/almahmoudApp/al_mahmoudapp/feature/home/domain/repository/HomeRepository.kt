package com.almahmoudApp.al_mahmoudapp.feature.home.domain.repository

import com.almahmoudApp.al_mahmoudapp.feature.home.domain.model.HomeContent

interface HomeRepository {
    suspend fun loadHomeContent(): Result<HomeContent>
}
