package com.luxury.cheats.features.welcome.splash.logic

/**
 * Estado de UI para Welcome Splash Screen
 * - Representa el estado visual de la pantalla
 * - Separación de lógica de UI (cumple AGENTS.md)
 */
data class WelcomeSplashState(
    /**
     * Indica si el splash está listo para navegar
     * (cuando todas las animaciones han terminado)
     */
    val isReadyToNavigate: Boolean = false,
)
