package com.luxury.cheats.features.welcome.page1.bienvenida.logic

/**
 * Acciones/Eventos para Welcome Page 1 (Bienvenida)
 * - Eventos de UI que el usuario puede disparar
 * - Separación de lógica de UI (cumple AGENTS.md)
 */
sealed interface WelcomePage1Action {
    /**
     * Acción cuando el usuario presiona el botón de continuar
     */
    object ContinueClicked : WelcomePage1Action

    /**
     * Acción cuando el usuario presiona el botón de atrás
     */
    object BackClicked : WelcomePage1Action
}
