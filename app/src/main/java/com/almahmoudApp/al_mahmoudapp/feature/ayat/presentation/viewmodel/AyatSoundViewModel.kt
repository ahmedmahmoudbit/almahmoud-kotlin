package com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.usecase.GetAyatAudioItemsUseCase
import com.almahmoudApp.al_mahmoudapp.feature.ayat.domain.usecase.GetAyatTopicUseCase
import com.almahmoudApp.al_mahmoudapp.feature.ayat.presentation.state.AyatSoundUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AyatSoundViewModel @Inject constructor(
    private val getAyatTopicUseCase: GetAyatTopicUseCase,
    private val getAyatAudioItemsUseCase: GetAyatAudioItemsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(AyatSoundUiState())
    val state: StateFlow<AyatSoundUiState> = _state.asStateFlow()

    fun load(topicId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = AyatSoundUiState(isLoading = true)
            val topicResult = getAyatTopicUseCase(topicId)
            val itemsResult = getAyatAudioItemsUseCase(topicId)
            if (topicResult.isSuccess && itemsResult.isSuccess) {
                _state.value = AyatSoundUiState(
                    isLoading = false,
                    topic = topicResult.getOrNull(),
                    items = itemsResult.getOrNull().orEmpty(),
                )
            } else {
                _state.value = AyatSoundUiState(
                    isLoading = false,
                    errorMessage = topicResult.exceptionOrNull()?.message
                        ?: itemsResult.exceptionOrNull()?.message
                        ?: "Failed to load audio items",
                )
            }
        }
    }
}
