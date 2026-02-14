package com.luxury.cheats.features.welcome.page2.permisos.logic

/**
 * Estado de la interfaz para la segunda página de bienvenida.
 * Mantiene el seguimiento de si los permisos han sido concedidos o denegados.
 *
 * @property isStorageGranted Indica si el permiso de almacenamiento está concedido.
 * @property isStorageDenied Indica si el permiso de almacenamiento ha sido denegado tras intentarlo.
 * @property isNotificationsGranted Indica si las notificaciones están activas.
 * @property isNotificationsDenied Indica si las notificaciones han sido denegadas tras intentarlo.
 * @property isAdminGranted Indica si el administrador de dispositivo está activo.
 * @property isAdminDenied Indica si el permiso de administrador ha sido denegado tras intentarlo.
 * @property isOverlayGranted Indica si la superposición sobre otras apps está concedida.
 * @property isOverlayDenied Indica si la superposición ha sido denegada tras intentarlo.
 */
data class WelcomePage2State(
    val isStorageGranted: Boolean = false,
    val isStorageDenied: Boolean = false,
    val isNotificationsGranted: Boolean = false,
    val isNotificationsDenied: Boolean = false,
    val isAdminGranted: Boolean = false,
    val isAdminDenied: Boolean = false,
    val isOverlayGranted: Boolean = false,
    val isOverlayDenied: Boolean = false,
)
