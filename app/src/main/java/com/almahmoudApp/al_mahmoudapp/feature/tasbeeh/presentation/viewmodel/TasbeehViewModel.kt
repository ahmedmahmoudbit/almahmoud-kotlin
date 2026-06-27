package com.almahmoudApp.al_mahmoudapp.feature.tasbeeh.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.almahmoudApp.al_mahmoudapp.feature.tasbeeh.domain.model.TasbeehDhikr
import com.almahmoudApp.al_mahmoudapp.feature.tasbeeh.presentation.state.TasbeehUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TasbeehViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(TasbeehUiState())
    val state: StateFlow<TasbeehUiState> = _state.asStateFlow()

    fun incrementCount() {
        if (_state.value.count >= TasbeehDhikr.MaxCount) return

        _state.update { currentState ->
            val newCount = currentState.count + 1
            val isFinished = newCount >= TasbeehDhikr.MaxCount
            
            // Determine Dhikr based on count
            val dhikrIndex = (newCount / TasbeehDhikr.MilestoneStep).coerceAtMost(TasbeehDhikr.milestones.lastIndex)
            val dhikr = TasbeehDhikr.milestones[dhikrIndex]

            currentState.copy(
                count = newCount,
                currentDhikr = dhikr,
                isFinished = isFinished,
                congratulationMessage = if (isFinished) "لقد قمت بإنجاز هائل" else ""
            )
        }
    }

    fun resetCount() {
        _state.value = TasbeehUiState()
    }
}
