package com.luxury.cheats.features.download.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luxury.cheats.features.download.service.DownloadService
import com.luxury.cheats.services.security.SecurityRepository
import com.luxury.cheats.services.shizuku.ShizukuService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para gestionar la lógica de descarga de cheats.
 */

@HiltViewModel
class DownloadViewModel
    @Inject
    constructor(
        private val downloadService: DownloadService,
        private val shizukuService: ShizukuService,
        private val securityRepository: SecurityRepository,
        @ApplicationContext private val context: android.content.Context,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(DownloadState())
        val uiState: StateFlow<DownloadState> = _uiState.asStateFlow()

        /**
         * Procesa las acciones enviadas desde la UI.
         * @param action La acción a ejecutar.
         */
        fun onAction(action: DownloadAction) {
            when (action) {
                is DownloadAction.StartSetup ->
                    setupDownload(
                        cheatName = action.cheatName,
                        directUrl = action.directUrl,
                        directPath = action.directPath,
                        preloadedWeight = action.preloadedWeight,
                    )
                DownloadAction.StartDownload -> startRealDownload()
                DownloadAction.Reset -> _uiState.update { DownloadState() }
            }
        }

        private fun setupDownload(
            cheatName: String,
            directUrl: String?,
            directPath: String?,
            preloadedWeight: String,
        ) {
            _uiState.update {
                it.copy(
                    status = DownloadStatus.IDLE,
                    fileName = cheatName,
                    progress = 0f,
                    fileWeight = preloadedWeight.ifEmpty { "Calculando..." },
                )
            }

            viewModelScope.launch {
                try {
                    val (url, path) = resolveDownloadParams(cheatName, directUrl, directPath)

                    if (url.isNotEmpty()) {
                        _uiState.update { it.copy(downloadUrl = url, downloadPath = path) }
                        resolveFileWeight(url, preloadedWeight)
                        startRealDownload()
                    } else {
                        handleParamsError()
                    }
                } catch (e: java.io.IOException) {
                    val errorMsg = "Error de red: ${e.message}"
                    _uiState.update { it.copy(fileWeight = errorMsg, status = DownloadStatus.ERROR) }
                } catch (e: IllegalStateException) {
                    val errorMsg = "Error de estado: ${e.message}"
                    _uiState.update { it.copy(fileWeight = errorMsg, status = DownloadStatus.ERROR) }
                }
            }
        }

        private suspend fun resolveDownloadParams(
            cheatName: String,
            directUrl: String?,
            directPath: String?,
        ): Pair<String, String> {
            return if (directUrl != null) {
                directUrl to (directPath ?: "")
            } else {
                val info = downloadService.getDownloadInfo(cheatName)
                info.url to info.path
            }
        }

        private suspend fun resolveFileWeight(
            url: String,
            preloadedWeight: String,
        ) {
            if (preloadedWeight.isEmpty()) {
                val weight = downloadService.getFileSize(url)
                _uiState.update { it.copy(fileWeight = weight) }
            }
        }

        private fun handleParamsError() {
            _uiState.update {
                it.copy(
                    fileWeight = "Error: URL no encontrada",
                    status = DownloadStatus.ERROR,
                )
            }
        }

        private fun startRealDownload() {
            val currentState = _uiState.value
            if (currentState.downloadUrl.isEmpty()) return

            _uiState.update { it.copy(status = DownloadStatus.DOWNLOADING, progress = 0f) }

            viewModelScope.launch {
                try {
                    val fileName = currentState.downloadUrl.substringAfterLast("/")
                    val tempDir = context.getExternalFilesDir(null) ?: context.cacheDir
                    val cacheFile = java.io.File(tempDir, fileName)

                    if (cacheFile.exists()) cacheFile.delete()

                    downloadService.downloadFile(currentState.downloadUrl, cacheFile).collect { progress ->
                        _uiState.update { it.copy(progress = progress) }
                    }

                    handleDownloadResult(cacheFile, currentState.downloadPath, fileName)
                } catch (e: java.io.IOException) {
                    val msg = "Error de descarga: ${e.message}"
                    _uiState.update { it.copy(status = DownloadStatus.ERROR, fileWeight = msg) }
                } catch (e: IllegalStateException) {
                    val msg = "Error de sistema: ${e.message}"
                    _uiState.update { it.copy(status = DownloadStatus.ERROR, fileWeight = msg) }
                }
            }
        }

        private fun handleDownloadResult(
            cacheFile: java.io.File,
            destPath: String,
            fileName: String,
        ) {
            if (!cacheFile.exists() || cacheFile.length() == 0L) {
                _uiState.update {
                    it.copy(status = DownloadStatus.ERROR, fileWeight = "Fallo: El archivo no se descargó")
                }
                return
            }

            if (destPath.isNotEmpty()) {
                _uiState.update { it.copy(fileWeight = "Solicitando Shizuku...", progress = 1f) }
                prepareShizukuAndMove(cacheFile.absolutePath, destPath, fileName)
            } else {
                _uiState.update {
                    it.copy(status = DownloadStatus.COMPLETED, fileWeight = "Guardado en: ${cacheFile.name}")
                }
            }
        }

        private fun prepareShizukuAndMove(
            sourcePath: String,
            destPath: String,
            fileName: String,
        ) {
            if (!shizukuService.isShizukuAvailable()) {
                _uiState.update { it.copy(status = DownloadStatus.ERROR, fileWeight = "Shizuku no está activo") }
                return
            }

            if (!shizukuService.hasPermission()) {
                shizukuService.requestPermission { granted ->
                    if (granted) {
                        moveFileWithShizuku(sourcePath, destPath, fileName)
                    } else {
                        _uiState.update {
                            it.copy(status = DownloadStatus.ERROR, fileWeight = "Permiso Shizuku Requerido")
                        }
                    }
                }
            } else {
                moveFileWithShizuku(sourcePath, destPath, fileName)
            }
        }

        private fun moveFileWithShizuku(
            sourcePath: String,
            destPathDir: String,
            fileName: String,
        ) {
            viewModelScope.launch {
                try {
                    val fullDestPath = buildDestPath(destPathDir, fileName)

                    // 1. Asegurar directorio y configurar permisos locales
                    ensureDirectoryExists(destPathDir)
                    setLocalFileReadable(sourcePath)

                    // 2. Ejecutar copia con Shizuku
                    val copyCommand = "cp \"$sourcePath\" \"$fullDestPath\""
                    val result = shizukuService.executeCommand(copyCommand)

                    if (result is ShizukuService.StringResult.Success) {
                        verifyAndFinalizeMove(fullDestPath, fileName, sourcePath)
                    } else if (result is ShizukuService.StringResult.Error) {
                        _uiState.update {
                            it.copy(status = DownloadStatus.ERROR, fileWeight = "Fallo: ${result.message}")
                        }
                    }
                } catch (e: java.io.IOException) {
                    val msg = "Error de archivo: ${e.message}"
                    _uiState.update { it.copy(status = DownloadStatus.ERROR, fileWeight = msg) }
                } catch (e: SecurityException) {
                    val msg = "Error de permisos: ${e.message}"
                    _uiState.update { it.copy(status = DownloadStatus.ERROR, fileWeight = msg) }
                }
            }
        }

        private fun buildDestPath(
            dir: String,
            file: String,
        ): String = if (dir.endsWith("/")) "$dir$file" else "$dir/$file"

        private suspend fun ensureDirectoryExists(dir: String) {
            shizukuService.executeCommand("mkdir -p \"$dir\"")
        }

        private fun setLocalFileReadable(path: String) {
            try {
                java.io.File(path).setReadable(true, false)
            } catch (e: SecurityException) {
                android.util.Log.e("DownloadViewModel", "Permission denied for file readability", e)
            } catch (e: java.io.IOException) {
                android.util.Log.e("DownloadViewModel", "IO Error setting permissions", e)
            }
        }

        private suspend fun verifyAndFinalizeMove(
            destPath: String,
            fileName: String,
            sourcePath: String,
        ) {
            val verifyRes = shizukuService.executeCommand("ls \"$destPath\"")
            if (verifyRes is ShizukuService.StringResult.Success && verifyRes.output.contains(fileName)) {
                securityRepository.registerFile(destPath)
                _uiState.update {
                    it.copy(status = DownloadStatus.COMPLETED, fileWeight = "¡Instalado con éxito!")
                }
                java.io.File(sourcePath).delete()
            } else {
                _uiState.update {
                    it.copy(status = DownloadStatus.ERROR, fileWeight = "Error: El archivo no llegó al destino")
                }
            }
        }
    }
