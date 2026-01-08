package com.luxury.cheats.features.update.logic

/**
 * Acciones de la pantalla de actualización.
 */
sealed interface UpdateAction {
    /** Acción disparada al pulsar el botón principal de descarga */
    data object DownloadClicked : UpdateAction
    
    /** Acción disparada al cerrar el widget de descarga */
    data object DismissDownloadSheet : UpdateAction
}
