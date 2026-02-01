package com.luxury.cheats.features.download.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luxury.cheats.features.download.service.DownloadService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar la lógica de descarga de cheats.
 */

class DownloadViewModel(
    private val downloadService: DownloadService
) : ViewModel() {

    /** Constantes de simulación y progreso. */
    companion object {
        private const val SIMULATION_STEPS = 100
        private const val SIMULATION_DELAY_MS = 4L
        private const val PROGRESS_FACTOR = 100f
    }

    private val _uiState = MutableStateFlow(DownloadState())
    val uiState: StateFlow<DownloadState> = _uiState.asStateFlow()

    /**
     * Procesa las acciones enviadas desde la UI.
     * @param action La acción a ejecutar.
     */
    fun onAction(action: DownloadAction) {
        when (action) {
            is DownloadAction.StartSetup -> setupDownload(
                cheatName = action.cheatName,
                directUrl = action.directUrl,
                directPath = action.directPath,
                preloadedWeight = action.preloadedWeight
            )
            DownloadAction.StartDownload -> { /* No-op: download starts automatically */ }
            DownloadAction.Reset -> _uiState.update { DownloadState() }
        }
    }

    private fun setupDownload(cheatName: String, directUrl: String?, directPath: String?, preloadedWeight: String) {
        _uiState.update { 
            it.copy(
                status = DownloadStatus.DOWNLOADING,
                fileName = cheatName,
                progress = 0f,
                fileWeight = preloadedWeight.ifEmpty { "" } // Usar peso pre-cargado si está disponible
            ) 
        }

        // Simulación ultra rápida e independiente para feedback instantáneo
        viewModelScope.launch {
            for (i in 1..SIMULATION_STEPS) {
                kotlinx.coroutines.delay(SIMULATION_DELAY_MS)
                _uiState.update { it.copy(progress = i / PROGRESS_FACTOR) }
            }
            _uiState.update { it.copy(status = DownloadStatus.COMPLETED) }
        }

        // Solo calcular peso si no fue pre-cargado
        if (preloadedWeight.isEmpty()) {
            viewModelScope.launch {
                try {
                    val (url, path) = if (directUrl != null) {
                        directUrl to (directPath ?: "")
                    } else {
                        val info = downloadService.getDownloadInfo(cheatName)
                        info.url to info.path
                    }
                    
                    if (url.isNotEmpty()) {
                        _uiState.update { it.copy(downloadUrl = url, downloadPath = path) }
                        val weight = downloadService.getFileSize(url)
                        _uiState.update { it.copy(fileWeight = weight) }
                    } else {
                        _uiState.update { it.copy(fileWeight = "URL vacía") }
                    }
                } catch (e: java.io.IOException) {
                    _uiState.update { it.copy(fileWeight = "Error de red: ${e.message}") }
                } catch (@Suppress("TooGenericExceptionCaught") e: RuntimeException) {
                    android.util.Log.e("DownloadViewModel", "Unexpected runtime error getting file size", e)
                    _uiState.update { it.copy(fileWeight = "Error desconocido") }
                }
            }
        }
    }
}
