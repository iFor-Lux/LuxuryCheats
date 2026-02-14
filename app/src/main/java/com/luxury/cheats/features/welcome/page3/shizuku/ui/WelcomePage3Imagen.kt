package com.luxury.cheats.features.welcome.page3.shizuku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private const val DARK_PLACEHOLDER_COLOR = 0xFF303030
private const val LIGHT_PLACEHOLDER_COLOR = 0xFFE0E0E0

/**
 * Componente visual que muestra una imagen ilustrativa sobre Shizuku.
 */
@Composable
fun welcomePage3Imagen() {
    val isDark = isSystemInDarkTheme()
    val placeholderColor = if (isDark) Color(DARK_PLACEHOLDER_COLOR) else Color(LIGHT_PLACEHOLDER_COLOR)

    Box(
        modifier =
            Modifier
                .size(width = 300.dp, height = 210.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(placeholderColor),
    ) {
        // Placeholder para la imagen
        // Aquí se colocará la imagen cuando esté definida
    }
}
