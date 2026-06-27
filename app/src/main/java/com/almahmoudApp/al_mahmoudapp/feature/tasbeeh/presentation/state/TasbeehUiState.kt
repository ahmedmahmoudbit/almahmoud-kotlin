package com.almahmoudApp.al_mahmoudapp.feature.tasbeeh.presentation.state

data class TasbeehUiState(
    val count: Int = 0,
    val currentDhikr: String = "سبحان الله",
    val isFinished: Boolean = false,
    val congratulationMessage: String = ""
)
