package com.luxury.cheats.features.download.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luxury.cheats.features.download.service.DownloadService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.luxury.cheats.services.shizuku.ShizukuService
import com.luxury.cheats.services.shizuku.ShizukuFileUtil
import com.luxury.cheats.services.security.SecurityRepository

/**
 * ViewModel para gestionar la lógica de descarga de cheats.
 */

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadService: DownloadService,
    private val shizukuService: ShizukuService,
    private val shizukuFileUtil: ShizukuFileUtil,
    private val securityRepository: SecurityRepository,
    @ApplicationContext private val context: android.content.Context
) : ViewModel() {

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
            DownloadAction.StartDownload -> startRealDownload()
            DownloadAction.Reset -> _uiState.update { DownloadState() }
        }
    }

    private fun setupDownload(cheatName: String, directUrl: String?, directPath: String?, preloadedWeight: String) {
        _uiState.update { 
            it.copy(
                status = DownloadStatus.IDLE,
                fileName = cheatName,
                progress = 0f,
                fileWeight = preloadedWeight.ifEmpty { "Calculando..." }
            ) 
        }

        viewModelScope.launch {
            try {
                // 1. Obtener URL y Path si no vienen directos
                val (url, path) = if (directUrl != null) {
                    directUrl to (directPath ?: "")
                } else {
                    val info = downloadService.getDownloadInfo(cheatName)
                    info.url to info.path
                }
                
                if (url.isNotEmpty()) {
                    _uiState.update { it.copy(downloadUrl = url, downloadPath = path) }
                    
                    // 2. Calcular peso si no se tiene
                    if (preloadedWeight.isEmpty()) {
                        val weight = downloadService.getFileSize(url)
                        _uiState.update { it.copy(fileWeight = weight) }
                    }
                    
                    // 3. Iniciar descarga automáticamente al preparar (según comportamiento original)
                    startRealDownload()
                } else {
                    _uiState.update { it.copy(fileWeight = "Error: URL no encontrada", status = DownloadStatus.ERROR) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(fileWeight = "Error: ${e.message}", status = DownloadStatus.ERROR) }
            }
        }
    }

    private fun startRealDownload() {
        val currentState = _uiState.value
        if (currentState.downloadUrl.isEmpty()) return

        _uiState.update { it.copy(status = DownloadStatus.DOWNLOADING, progress = 0f) }

        viewModelScope.launch {
            try {
                // 1. Descargar a Carpeta Segura (App External Storage)
                val fileName = currentState.downloadUrl.substringAfterLast("/")
                // getExternalFilesDir es siempre accesible por la app y por ADB/Shizuku
                val tempDir = context.getExternalFilesDir(null) ?: context.cacheDir
                val cacheFile = java.io.File(tempDir, fileName)
                
                android.util.Log.d("DownloadViewModel", "Descargando a: ${cacheFile.absolutePath}")
                
                if (cacheFile.exists()) cacheFile.delete()

                downloadService.downloadFile(currentState.downloadUrl, cacheFile).collect { progress ->
                    _uiState.update { it.copy(progress = progress) }
                }

                // VERIFICACIÓN: ¿Realmente se descargó algo?
                if (!cacheFile.exists() || cacheFile.length() == 0L) {
                    _uiState.update { it.copy(status = DownloadStatus.ERROR, fileWeight = "Fallo: El archivo no se descargó") }
                    return@launch
                }

                android.util.Log.d("DownloadViewModel", "Descarga completada. Tamaño: ${cacheFile.length()} bytes")

                // 2. Mover a destino final con Shizuku
                val destPath = currentState.downloadPath
                if (destPath.isNotEmpty()) {
                    _uiState.update { it.copy(fileWeight = "Solicitando Shizuku...", progress = 1f) }
                    
                    if (!shizukuService.isShizukuAvailable()) {
                        _uiState.update { it.copy(status = DownloadStatus.ERROR, fileWeight = "Shizuku no está activo") }
                    } else if (!shizukuService.hasPermission()) {
                         withContext(Dispatchers.Main) {
                            shizukuService.requestPermission { granted ->
                                if (granted) {
                                    moveFileWithShizuku(cacheFile.absolutePath, destPath, fileName)
                                } else {
                                    _uiState.update { it.copy(status = DownloadStatus.ERROR, fileWeight = "Permiso Shizuku Requerido") }
                                }
                            }
                        }
                    } else {
                        moveFileWithShizuku(cacheFile.absolutePath, destPath, fileName)
                    }
                } else {
                    _uiState.update { it.copy(status = DownloadStatus.COMPLETED, fileWeight = "Guardado en: ${cacheFile.name}") }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(status = DownloadStatus.ERROR, fileWeight = "Error: ${e.message}") }
            }
        }
    }

    private fun moveFileWithShizuku(sourcePath: String, destPathDir: String, fileName: String) {
        viewModelScope.launch {
            try {
                // 1. Asegurar directorio destino
                val fullDestPath = if (destPathDir.endsWith("/")) "$destPathDir$fileName" else "$destPathDir/$fileName"
                
                // Intentar mkdir -p
                val mkdirCommand = "mkdir -p \"$destPathDir\""
                val mkdirRes = shizukuService.executeCommand(mkdirCommand)
                if (mkdirRes is ShizukuService.StringResult.Error) {
                    android.util.Log.w("DownloadViewModel", "Mkdir warning: ${mkdirRes.message}")
                }

                // 2. Dar permisos de lectura al archivo temporal para que el usuario 'shell' pueda leerlo
                try {
                    val tempFile = java.io.File(sourcePath)
                    tempFile.setReadable(true, false) // Readable por todos
                } catch (e: Exception) {
                    android.util.Log.e("DownloadViewModel", "Error setting file permissions", e)
                }

                // 3. Copiar (usamos cp porque mv entre particiones puede fallar)
                val copyCommand = "cp \"$sourcePath\" \"$fullDestPath\""
                android.util.Log.d("DownloadViewModel", "Ejecutando: $copyCommand")
                
                val result = shizukuService.executeCommand(copyCommand)
                if (result is ShizukuService.StringResult.Success) {
                    // VERIFICACIÓN ADICIONAL: ¿El archivo está en el destino?
                    val verifyRes = shizukuService.executeCommand("ls \"$fullDestPath\"")
                    if (verifyRes is ShizukuService.StringResult.Success && verifyRes.output.contains(fileName)) {
                        android.util.Log.d("DownloadViewModel", "Verificación exitosa: El archivo está en el destino.")
                        securityRepository.registerFile(fullDestPath)
                        _uiState.update { it.copy(status = DownloadStatus.COMPLETED, fileWeight = "¡Instalado con éxito!") }
                    } else {
                        android.util.Log.e("DownloadViewModel", "Verificación fallida: No se encuentra el archivo en el destino después del cp.")
                        _uiState.update { it.copy(status = DownloadStatus.ERROR, fileWeight = "Error: El archivo no llegó al destino") }
                    }
                    
                    // Limpiar cache
                    try { java.io.File(sourcePath).delete() } catch (e: Exception) { /* Ignorar */ }
                } else if (result is ShizukuService.StringResult.Error) {
                    android.util.Log.e("DownloadViewModel", "Copy failed: ${result.message}")
                    _uiState.update { it.copy(status = DownloadStatus.ERROR, fileWeight = "Fallo: ${result.message}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(status = DownloadStatus.ERROR, fileWeight = "Error: ${e.message}") }
            }
        }
    }
}
