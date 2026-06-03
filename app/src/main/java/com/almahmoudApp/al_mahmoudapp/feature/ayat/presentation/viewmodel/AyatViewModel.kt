package com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.usecase.GetAyatTopicsUseCase
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.state.AyatUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AyatViewModel @Inject constructor(
    private val getAyatTopicsUseCase: GetAyatTopicsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(AyatUiState())
    val state: StateFlow<AyatUiState> = _state.asStateFlow()

    init {
        loadTopics()
    }

    private fun loadTopics() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            getAyatTopicsUseCase()
                .onSuccess { topics ->
                    _state.value = AyatUiState(isLoading = false, topics = topics)
                }
                .onFailure { throwable ->
                    _state.value = AyatUiState(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to load ayat topics",
                    )
                }
        }
    }
}
