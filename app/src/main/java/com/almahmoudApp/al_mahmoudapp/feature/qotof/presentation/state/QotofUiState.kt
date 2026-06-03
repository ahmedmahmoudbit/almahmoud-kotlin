package com.almahmoudApp.al_mahmoudapp.feature.qotof.presentation.state

import androidx.compose.runtime.Immutable
import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.model.QotofContent
import com.almahmoudApp.al_mahmoudapp.feature.qotof.domain.model.QotofItem

@Immutable
data class QotofUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val content: QotofContent? = null,
    val filteredItems: List<QotofItem> = emptyList(),
    val query: String = "",
    val selectedItem: QotofItem? = null,
)
