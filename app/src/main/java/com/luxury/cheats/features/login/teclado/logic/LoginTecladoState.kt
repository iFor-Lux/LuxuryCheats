package com.luxury.cheats.features.login.teclado.logic

/**
 * Define el tipo de layout que debe mostrar el teclado.
 */
enum class TecladoType {
    NONE,
    ALPHABETIC,
    NUMERIC,
}

/**
 * Estado visual y de configuración del teclado virtual.
 */
data class LoginTecladoState(
    val type: TecladoType = TecladoType.NONE,
    val isVisible: Boolean = false,
)
