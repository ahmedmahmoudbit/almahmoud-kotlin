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
        val currentState = _state.value
        val target = if (currentState.isCustom) currentState.customTarget else TasbeehDhikr.MaxCount
        if (currentState.count >= target) return

        _state.update {
            val newCount = it.count + 1
            val isFinished = newCount >= target

            val newDhikr = if (it.isCustom) {
                it.currentDhikr
            } else {
                val dhikrIndex = (newCount / TasbeehDhikr.MilestoneStep)
                    .coerceAtMost(TasbeehDhikr.milestones.lastIndex)
                TasbeehDhikr.milestones[dhikrIndex]
            }

            it.copy(
                count = newCount,
                currentDhikr = newDhikr,
                isFinished = isFinished,
                congratulationMessage = if (isFinished) "لقد قمت بإنجاز هائل" else ""
            )
        }
    }

    fun setCustomDhikr(dhikr: String, target: Int) {
        _state.value = TasbeehUiState(
            currentDhikr = dhikr,
            customTarget = target,
            isCustom = true,
        )
    }

    fun resetCount() {
        _state.value = TasbeehUiState()
    }
}
