package com.luxury.cheats.features.welcome.page2.permisos.logic

/**
 * Acciones de usuario disponibles en la segunda página de bienvenida (Permisos).
 */
sealed class WelcomePage2Action {
    /** Click en la sección de permiso de Almacenamiento. */
    data object StorageClicked : WelcomePage2Action()

    /** Click en la sección de permiso de Notificaciones. */
    data object NotificationsClicked : WelcomePage2Action()

    /** Click en la sección de permiso de Administrador de Dispositivo. */
    data object AdminClicked : WelcomePage2Action()

    /** Click en la sección de permiso de Superposición. */
    data object OverlayClicked : WelcomePage2Action()
}
