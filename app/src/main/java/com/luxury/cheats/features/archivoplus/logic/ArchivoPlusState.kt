package com.luxury.cheats.features.archivoplus.logic

import android.net.Uri

/**
 * Modos de selección del explorador interno
 */
enum class BrowserMode {
    SELECT_FILE,        // Seleccionar un único archivo
    SELECT_ZIP,         // Seleccionar un único ZIP/RAR
    SELECT_MULTIPLE,    // Seleccionar múltiples archivos
    SELECT_PATH         // Seleccionar una carpeta de destino
}

/**
 * Estado de la UI para la pantalla Archivo Plus
 */
data class ArchivoPlusState(
    val selectedFileUri: Uri? = null,
    val selectedFileName: String? = null,
    
    val selectedFilesUris: List<Uri> = emptyList(),
    val selectedFilesNames: List<String> = emptyList(),
    
    val selectedZipUri: Uri? = null,
    val selectedZipName: String? = null,
    
    val destinationPath: String = "",
    val isVip: Boolean = false,
    
    // Estados de Ejecución
    val isExecuting: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val isVipError: Boolean = false,
    
    // Lógica de Contraseña ZIP
    val showPasswordDialog: Boolean = false,
    val zipPassword: String = "",
    
    // Estado para el Mini Gestor de Archivos
    val showBrowser: Boolean = false,
    val browserMode: BrowserMode = BrowserMode.SELECT_PATH,
    val currentBrowserPath: String = "/sdcard",
    val browserItems: List<String> = emptyList(),
    val isBrowserLoading: Boolean = false,
    val selectedBrowserItems: Set<String> = emptySet()
)
