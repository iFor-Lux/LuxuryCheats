package com.luxury.cheats.features.download.logic

/**
 * Acciones para la lógica de descarga.
 */
sealed class DownloadAction {
    /** Inicia la configuración de la descarga para un cheat específico. */
    data class StartSetup(
        val cheatName: String,
        val directUrl: String? = null,
        val directPath: String? = null,
        val preloadedWeight: String = ""
    ) : DownloadAction()
    /** Ejecuta el proceso de descarga del archivo. */
    object StartDownload : DownloadAction()
    /** Reinicia el estado de la descarga. */
    object Reset : DownloadAction()
}
