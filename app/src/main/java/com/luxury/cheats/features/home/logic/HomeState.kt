package com.luxury.cheats.features.home.logic

import com.luxury.cheats.core.ui.AppNotification

/**
 * Estado de la UI de la pantalla Home
 */
data class HomeState(
    val isIdAndConsoleVisible: Boolean = false,
    val isOpcionesVisible: Boolean = false,
    val isFloatingServiceRunning: Boolean = false,
    val userName: String = "",
    val idValue: String = "",
    val greeting: String = "",
    val greetingSubtitle: String = "",
    val consoleOutput: String = "SISTEMA OPERATIVO LUXURY CARGADO...\nESPERANDO ENTRADA DE DATOS...",
    val isLoadingPlayer: Boolean = false,
    val searchProgress: String = "",
    val isConsoleExpanded: Boolean = false,
    val isSearchSuccessful: Boolean = false,
    val notifications: List<AppNotification> = emptyList(),
    val isSeguridadUnlocked: Boolean = false,
    val appUpdate: com.luxury.cheats.features.update.logic.AppUpdate? = null,
    val currentInAppNotification: com.luxury.cheats.features.home.logic.InAppNotification? = null,
    val isDownloadArchivoVisible: Boolean = false,
    val downloadingFileName: String = "",
    val downloadingFileWeight: String = "",
    val cheatOptions: Map<String, Boolean> = emptyMap(), // Caché de estado de trampas (Aimbot, etc.)
    val fileWeightsCache: Map<String, String> = emptyMap() // Caché de pesos pre-cargados
)
