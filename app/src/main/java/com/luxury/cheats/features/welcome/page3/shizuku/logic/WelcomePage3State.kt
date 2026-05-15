package com.luxury.cheats.features.welcome.page3.shizuku.logic

/**
 * Estado de la interfaz de usuario para la tercera página de bienvenida.
 *
 * @property isShizukuInstalled Indica si la app Shizuku está detectada.
 * @property isShizukuRunning Indica si el servicio de Shizuku está activo.
 * @property statusMessage Mensaje descriptivo del estado actual.
 */
data class WelcomePage3State(
    val isShizukuInstalled: Boolean = false,
    val isShizukuRunning: Boolean = false,
    val statusMessage: String = "Pendiente de verificación",
    val imageUrl: String? = null,
)
