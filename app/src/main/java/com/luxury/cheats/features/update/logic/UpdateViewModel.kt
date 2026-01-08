package com.luxury.cheats.features.update.logic

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel que gestiona la lógica de la pantalla de actualización.
 */
class UpdateViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateState())
    val uiState: StateFlow<UpdateState> = _uiState.asStateFlow()

    /**
     * Procesa las acciones de la UI.
     */
    fun onAction(action: UpdateAction) {
        when (action) {
            UpdateAction.DownloadClicked -> {
                _uiState.value = _uiState.value.copy(showDownloadSheet = true)
            }
            UpdateAction.DismissDownloadSheet -> {
                _uiState.value = _uiState.value.copy(showDownloadSheet = false)
            }
        }
    }
}
