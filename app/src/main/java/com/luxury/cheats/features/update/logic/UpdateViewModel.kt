package com.luxury.cheats.features.update.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luxury.cheats.core.util.VersionUtils
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
    private val preferencesService: com.luxury.cheats.services.storage.UserPreferencesService,
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
            val localVersion = com.luxury.cheats.BuildConfig.VERSION_NAME
            val storedInfo = preferencesService.accessUpdateInfo()

            // 1. CARGA INICIAL DESDE CACHÉ (Para que el usuario vea su fecha actual al instante)
            if (storedInfo != null && storedInfo.first == localVersion) {
                _uiState.update {
                    it.copy(
                        appVersion = localVersion,
                        releaseDate = formatTimestamp(storedInfo.second)
                    )
                }
                // NO RETORNAMOS, para poder consultar si hay una v2.0.1 o superior
            }

            // 2. CONSULTA A FIREBASE (Para saber si hay una versión nueva)
            try {
                val update = updateService.getAppUpdate()

                // Si la versión que bajamos de Firebase es la misma que la nuestra,
                // guardamos su fecha en el caché por si no la teníamos o cambió.
                if (update.version == localVersion) {
                    preferencesService.accessUpdateInfo(
                        version = localVersion,
                        timestamp = update.timestamp
                    )
                }

                android.util.Log.d("UpdateViewModel", "Processing Firebase Result -> Remote Version: ${update.version}")
                _uiState.update {
                    it.copy(
                        appUpdate = update,
                        appVersion = localVersion,
                        releaseDate = if (update.version == localVersion) {
                            formatTimestamp(update.timestamp)
                        } else {
                            it.releaseDate.ifEmpty { "2025-01-01" }
                        }
                    )
                }
                android.util.Log.d("UpdateViewModel", "Final State -> appVersion: $localVersion, hasUpdate: ${VersionUtils.isVersionNewer(update.version, localVersion)}")
            } catch (e: Exception) {
                android.util.Log.e("UpdateViewModel", "Error fetching update info", e)
                _uiState.update {
                    it.copy(
                        appVersion = localVersion,
                        releaseDate = it.releaseDate.ifEmpty {
                            formatTimestamp(storedInfo?.second ?: "2025-01-01")
                        }
                    )
                }
            }
        }
    }

    private fun formatTimestamp(isoTimestamp: String): String {
        return try {
            // Un formateo simple: 2026-01-02T12:00:00Z -> 2026-01-02
            if (isoTimestamp.length >= DATE_STRING_LENGTH) {
                isoTimestamp.substring(0, DATE_STRING_LENGTH)
            } else {
                isoTimestamp
            }
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
