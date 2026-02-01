package com.luxury.cheats.features.update.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la lógica de la pantalla de actualización.
 */
class UpdateViewModel(
    private val updateService: com.luxury.cheats.features.update.service.UpdateService,
    private val preferencesService: com.luxury.cheats.services.UserPreferencesService
) : androidx.lifecycle.ViewModel() {

    /** Constantes para formateo de fechas. */
    companion object {
        private const val DATE_STRING_LENGTH = 10
    }

    private val _uiState = MutableStateFlow(UpdateState())
    val uiState: StateFlow<UpdateState> = _uiState.asStateFlow()

    init {
        fetchAndUpdateInfo()
    }

    private fun fetchAndUpdateInfo() {
        viewModelScope.launch {
            try {
                val update = updateService.getAppUpdate()
                val localVersion = com.luxury.cheats.BuildConfig.VERSION_NAME
                
                // Consultar información guardada
                val storedInfo = preferencesService.accessUpdateInfo()
                
                // Si la versión de Firebase coincide con nuestra versión local,
                // guardamos esa fecha como la fecha de "este" release local.
                if (update.version == localVersion && storedInfo?.first != localVersion) {
                    preferencesService.accessUpdateInfo(
                        version = localVersion,
                        timestamp = update.timestamp
                    )
                }
                
                // Cargar la info guardada para la versión local actual
                val finalInfo = preferencesService.accessUpdateInfo()
                
                _uiState.update { 
                    it.copy(
                        appUpdate = update,
                        appVersion = localVersion, // MOSTRAR SIEMPRE LA LOCAL ARRIBA
                        releaseDate = formatTimestamp(finalInfo?.second ?: "2025-01-01")
                    )
                }
            } catch (e: com.google.firebase.database.DatabaseException) {
                android.util.Log.e("UpdateViewModel", "Database error fetching update info", e)
                val finalInfo = preferencesService.accessUpdateInfo()
                _uiState.update { 
                    it.copy(
                        appVersion = com.luxury.cheats.BuildConfig.VERSION_NAME,
                        releaseDate = formatTimestamp(finalInfo?.second ?: "2025-01-01")
                    )
                }
            } catch (@Suppress("TooGenericExceptionCaught") e: RuntimeException) {
                android.util.Log.e("UpdateViewModel", "Unexpected runtime error in fetchAndUpdateInfo", e)
                _uiState.update { it.copy(appVersion = "Error: ${com.luxury.cheats.BuildConfig.VERSION_NAME}") }
            }
        }
    }

    private fun formatTimestamp(isoTimestamp: String): String {
        return try {
            // Un formateo simple: 2026-01-02T12:00:00Z -> 2026-01-02
            if (isoTimestamp.length >= DATE_STRING_LENGTH) {
                isoTimestamp.substring(0, DATE_STRING_LENGTH)
            } else isoTimestamp
        } catch (ignored: IndexOutOfBoundsException) {
            isoTimestamp
        }
    }

    /**
     * Procesa las acciones de la UI.
     */
    fun onAction(action: UpdateAction) {
        when (action) {
            UpdateAction.DownloadClicked -> {
                _uiState.update { it.copy(showDownloadSheet = true) }
            }
            UpdateAction.DismissDownloadSheet -> {
                _uiState.update { it.copy(showDownloadSheet = false) }
            }
        }
    }
}
