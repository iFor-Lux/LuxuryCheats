package com.luxury.cheats.core.ui

import androidx.compose.ui.graphics.Color

/**
 * Define el estilo visual para un botón de la barra de navegación.
 *
 * @property containerColor Color de fondo del botón.
 * @property borderColor Color del borde del botón.
 * @property contentColor Color del contenido (texto/iconos) del botón.
 */
data class WelcomeNavBarButtonStyle(
    val containerColor: Color,
    val borderColor: Color,
    val contentColor: Color,
)
