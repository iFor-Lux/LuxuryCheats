package com.luxury.cheats.features.download.logic

/**
 * Estado de la sección de descarga.
 */
data class DownloadState(
    val fileName: String = "",
    val fileWeight: String = "",
    val downloadPath: String = "",
    val downloadUrl: String = "",
    val progress: Float = 0f,
    val status: DownloadStatus = DownloadStatus.IDLE,
    val error: String? = null
)

/**
 * Parámetros para configurar una descarga.
 */
data class DownloadParams(
    val cheatName: String,
    val directUrl: String? = null,
    val directPath: String? = null,
    val preloadedWeight: String = ""
)

/**
 * Representa los posibles estados de una descarga.
 */
enum class DownloadStatus {
    IDLE, FETCHING_INFO, DOWNLOADING, COMPLETED, ERROR
}
