package com.luxury.cheats.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable

/**
 * Tema principal de la aplicación - Luxury Cheats
 * - Migrado a Material 3 Expressive (MotionScheme y Shapes dinámicos)
 * - Se adapta automáticamente al modo claro/oscuro del sistema
 * - Usa Material You (Dynamic Colors) armonizados
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun luxuryCheatsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        ThemeManager.getColorScheme(
            darkTheme = darkTheme,
            useDynamicColor = dynamicColor,
        )

    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        androidx.compose.runtime.SideEffect {
            val window = (view.context as android.app.Activity).window
            val windowInsetsController = androidx.core.view.WindowCompat.getInsetsController(window, view)
            // Iconos oscuros en fondo claro, iconos blancos en fondo oscuro
            windowInsetsController.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = LuxuryShapes,
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}
