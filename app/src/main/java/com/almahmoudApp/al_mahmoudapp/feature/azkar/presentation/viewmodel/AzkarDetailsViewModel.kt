package com.almahmoudApp.al_mahmoudapp.feature.azkar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.model.AzkarCategory
import com.almahmoudApp.al_mahmoudapp.feature.azkar.domain.usecase.GetAzkarUseCase
import com.almahmoudApp.al_mahmoudapp.feature.azkar.presentation.state.AzkarDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AzkarDetailsViewModel @Inject constructor(
    private val getAzkarUseCase: GetAzkarUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AzkarDetailsUiState())
    val state: StateFlow<AzkarDetailsUiState> = _state.asStateFlow()

    fun loadCategory(category: AzkarCategory) {
        if (_state.value.category == category && _state.value.items.isNotEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = AzkarDetailsUiState(isLoading = true, category = category)
            getAzkarUseCase(category)
                .onSuccess { items ->
                    _state.value = AzkarDetailsUiState(
                        isLoading = false,
                        category = category,
                        items = items,
                    )
                }
                .onFailure { throwable ->
                    _state.value = AzkarDetailsUiState(
                        isLoading = false,
                        category = category,
                        errorMessage = throwable.message ?: "فشل تحميل الأذكار",
                    )
                }
        }
    }
}
