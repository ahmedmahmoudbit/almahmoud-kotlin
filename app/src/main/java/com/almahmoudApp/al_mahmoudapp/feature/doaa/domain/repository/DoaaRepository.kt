package com.almahmoudApp.al_mahmoudapp.feature.doaa.domain.repository

interface DoaaRepository {
    suspend fun getDoaa(): List<String>
}
