package com.almahmoudApp.al_mahmoudapp.feature.qotof.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.model.QotofContent
import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.model.QotofItem
import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.usecase.GetQotofContentUseCase
import com.almahmoudApp.al_mahmoudapp.feature.qotof.presentation.state.QotofUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class QotofViewModel @Inject constructor(
    private val getQotofContentUseCase: GetQotofContentUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(QotofUiState())
    val state: StateFlow<QotofUiState> = _state.asStateFlow()

    init {
        loadContent()
    }

    fun onQueryChange(query: String) {
        _state.update { current ->
            current.copy(
                query = query,
                filteredItems = filterItems(current.content, query),
                errorMessage = null,
            )
        }
    }

    fun onItemSelected(item: QotofItem) {
        _state.update {
            it.copy(selectedItem = item)
        }
    }

    fun dismissSelectedItem() {
        _state.update {
            it.copy(selectedItem = null)
        }
    }

    fun retry() {
        loadContent()
    }

    private fun loadContent() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getQotofContentUseCase()
                .onSuccess { content ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            content = content,
                            filteredItems = filterItems(content, it.query),
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage,
                        )
                    }
                }
        }
    }

    private fun filterItems(content: QotofContent?, query: String): List<QotofItem> {
        val items = content?.items.orEmpty()
        if (query.isBlank()) return items

        val normalizedQuery = query.trim()
        return items.filter { item ->
            item.title.contains(normalizedQuery, ignoreCase = true) ||
                item.body.contains(normalizedQuery, ignoreCase = true)
        }
    }
}
