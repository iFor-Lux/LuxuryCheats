package com.luxury.cheats.features.welcome.splash.logic

/**
 * Acciones/Eventos para Welcome Splash Screen
 * - Eventos de UI que el usuario puede disparar
 * - Separación de lógica de UI (cumple AGENTS.md)
 */
sealed interface WelcomeSplashAction {
    /**
     * Acción cuando el splash ha terminado de mostrarse
     * y se debe navegar a la siguiente pantalla
     */
    object NavigateToNext : WelcomeSplashAction
}

