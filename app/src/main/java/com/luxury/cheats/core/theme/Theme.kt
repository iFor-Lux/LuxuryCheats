package com.luxury.cheats.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Tema principal de la aplicación
 * - Se adapta automáticamente al modo claro/oscuro del sistema
 * - Usa Material You (Dynamic Colors) cuando está disponible
 * - Colores premium y minimalistas
 * - Gestionado por ThemeManager (separación de lógica)
 */
@Composable
fun LuxuryCheatsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Activado por defecto para Material You
    content: @Composable () -> Unit
) {
    val colorScheme = ThemeManager.getColorScheme(
        darkTheme = darkTheme,
        useDynamicColor = dynamicColor
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
