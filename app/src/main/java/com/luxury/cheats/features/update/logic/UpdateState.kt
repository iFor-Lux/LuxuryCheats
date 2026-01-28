package com.luxury.cheats.features.update.logic

/**
 * Modelo de datos para la actualización de la aplicación desde Firebase.
 */
data class AppUpdate(
    val active: Boolean = false,
    val title: String = "",
    val description: String = "",
    val version: String = "",
    val downloadLink: String = "",
    val timestamp: String = ""
)

/**
 * Estado de la pantalla de actualización.
 * @param showDownloadSheet Controla la visibilidad del widget de descarga.
 * @param appUpdate Información de la actualización actual.
 */
data class UpdateState(
    val showDownloadSheet: Boolean = false,
    val appUpdate: AppUpdate? = null,
    val appVersion: String = "1.0.0",
    val releaseDate: String = "2025-01-01"
)
