package com.luxury.cheats.features.welcome.page1.bienvenida.logic

/**
 * Estado de UI para Welcome Page 1 (Bienvenida)
 * - Representa el estado visual de la pantalla
 * - Separación de lógica de UI (cumple AGENTS.md)
 */
data class WelcomePage1State(
    /**
     * Indica si la pantalla está cargando
     */
    val isLoading: Boolean = false,
    /**
     * Indica si hay un error
     */
    val error: String? = null,
    /**
     * Indica si el menú de idiomas está expandido
     */
    val isLanguageExpanded: Boolean = false,
    /**
     * Idioma seleccionado actualmente
     */
    val selectedLanguage: String = "Español",
)
