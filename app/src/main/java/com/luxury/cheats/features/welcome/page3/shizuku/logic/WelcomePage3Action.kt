package com.luxury.cheats.features.welcome.page3.shizuku.logic

/**
 * Acciones de usuario disponibles en la tercera página de bienvenida (Shizuku/ADB).
 */
sealed class WelcomePage3Action {
    /** Click en el botón de descargar Shizuku. */
    data object DownloadShizukuClicked : WelcomePage3Action()
    /** Click en el botón de iniciar Shizuku. */
    data object StartShizukuClicked : WelcomePage3Action()
    /** Click en el botón de verificar estado de Shizuku. */
    data object CheckStatusClicked : WelcomePage3Action()
}
