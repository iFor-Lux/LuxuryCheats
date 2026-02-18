package com.luxury.cheats.features.update.logic

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

    @Suppress("TooGenericExceptionCaught")
    private fun fetchAndUpdateInfo() {
        viewModelScope.launch {
            val localVersion = com.luxury.cheats.BuildConfig.VERSION_NAME
            val storedInfo = preferencesService.accessUpdateInfo()

            loadInitialCache(localVersion, storedInfo)

            try {
                val update = updateService.getAppUpdate()
                processUpdateResult(update, localVersion)
            } catch (e: com.google.firebase.database.DatabaseException) {
                android.util.Log.e("UpdateViewModel", "Firebase error", e)
                handleUpdateError(localVersion, storedInfo)
            } catch (e: Exception) {
                android.util.Log.e("UpdateViewModel", "Unexpected error", e)
                handleUpdateError(localVersion, storedInfo)
            }
        }
    }

    private fun loadInitialCache(localVersion: String, storedInfo: Pair<String, String>?) {
        if (storedInfo != null && storedInfo.first == localVersion) {
            _uiState.update {
                it.copy(appVersion = localVersion, releaseDate = formatTimestamp(storedInfo.second))
            }
        }
    }

    private fun processUpdateResult(update: AppUpdate, localVersion: String) {
        if (update.version == localVersion) {
            preferencesService.accessUpdateInfo(version = localVersion, timestamp = update.timestamp)
        }

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
    }

    private fun handleUpdateError(localVersion: String, storedInfo: Pair<String, String>?) {
        _uiState.update {
            it.copy(
                appVersion = localVersion,
                releaseDate = it.releaseDate.ifEmpty {
                    formatTimestamp(storedInfo?.second ?: "2025-01-01")
                }
            )
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
