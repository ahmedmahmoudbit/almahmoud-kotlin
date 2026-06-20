package com.almahmoudApp.al_mahmoudapp.feature.tamilat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.tamilat.domain.usecase.GetTamilatUseCase
import com.almahmoudApp.al_mahmoudapp.feature.tamilat.presentation.state.TamilatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TamilatViewModel @Inject constructor(
    private val getTamilatUseCase: GetTamilatUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TamilatUiState())
    val state: StateFlow<TamilatUiState> = _state.asStateFlow()

    init {
        loadReflections()
    }

    fun loadReflections() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getTamilatUseCase()
                .onSuccess { list ->
                    _state.update { it.copy(reflections = list.shuffled(), isLoading = false) }
                }
                .onFailure { error ->
                    _state.update { it.copy(errorMessage = error.localizedMessage, isLoading = false) }
                }
        }
    }
}
