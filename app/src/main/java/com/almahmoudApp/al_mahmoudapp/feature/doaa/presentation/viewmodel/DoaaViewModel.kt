package com.almahmoudApp.al_mahmoudapp.feature.doaa.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.doaa.domain.usecase.GetDoaaUseCase
import com.almahmoudApp.al_mahmoudapp.feature.doaa.presentation.state.DoaaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoaaViewModel @Inject constructor(
    private val getDoaaUseCase: GetDoaaUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(DoaaUiState())
    val state: StateFlow<DoaaUiState> = _state.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = DoaaUiState(isLoading = true)
            getDoaaUseCase()
                .onSuccess { items ->
                    _state.value = DoaaUiState(isLoading = false, items = items)
                }
                .onFailure { throwable ->
                    _state.value = DoaaUiState(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load doaa",
                    )
                }
        }
    }
}
