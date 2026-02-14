package com.luxury.cheats.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.ceil

// Constantes para Detekt y legibilidad
private const val DOT_COLOR_ALPHA_DARK = 0.40f
private const val DOT_COLOR_ALPHA_LIGHT = 0.50f
private const val SAFE_MAX_DOT_LIMIT = 500

/**
 * Background estático de puntos (Dot Pattern)
 */
@Composable
fun dotPatternBackground(
    modifier: Modifier = Modifier,
    // Mucho más denso para look ultra-premium
    dotSpacingX: Float = 40f,
    dotSpacingY: Float = 40f,
    // Puntos más finos para que no saturen con la densidad
    dotRadius: Float = 2.2f,
) {
    val isDark = isSystemInDarkTheme()
    val dotColor = getDotColor(isDark, MaterialTheme.colorScheme.onBackground)

    // Límite de seguridad para alta densidad

    androidx.compose.foundation.layout.Spacer(
        modifier =
            modifier
                .fillMaxSize()
                .drawBehind {
                    val widthPx = size.width
                    val heightPx = size.height

                    val cols = ceil(widthPx / dotSpacingX).toInt().coerceAtMost(SAFE_MAX_DOT_LIMIT)
                    val rows = ceil(heightPx / dotSpacingY).toInt().coerceAtMost(SAFE_MAX_DOT_LIMIT)

                    val startX = (widthPx - (cols - 1) * dotSpacingX) / 2f
                    val startY = (heightPx - (rows - 1) * dotSpacingY) / 2f

                    for (r in 0 until rows) {
                        val y = startY + r * dotSpacingY

                        // Restauración del desvanecimiento superior (Fade In)
                        val fadeAlpha = calculateFadeAlpha(y)
                        if (fadeAlpha <= 0f) continue

                        val finalColor = dotColor.copy(alpha = dotColor.alpha * fadeAlpha)

                        for (c in 0 until cols) {
                            val x = startX + c * dotSpacingX
                            drawCircle(
                                color = finalColor,
                                radius = dotRadius,
                                center = Offset(x, y),
                            )
                        }
                    }
                },
    )
}

@Composable
private fun getDotColor(
    isDark: Boolean,
    onBackground: Color,
): Color {
    return if (isDark) {
        onBackground.copy(alpha = DOT_COLOR_ALPHA_DARK)
    } else {
        onBackground.copy(alpha = DOT_COLOR_ALPHA_LIGHT)
    }
}

// Configuración de desvanecimiento (Fade)
private const val FADE_START_Y = 500f // Despejado significativamente arriba
private const val FADE_END_Y = 1000f

/**
 * Calcula el alpha dinámico para crear un efecto de desvanecimiento suave.
 */
private fun calculateFadeAlpha(y: Float): Float {
    return when {
        y < FADE_START_Y -> 0f
        y < FADE_END_Y -> {
            val progress = (y - FADE_START_Y) / (FADE_END_Y - FADE_START_Y)
            progress * progress // Suavizado cuadrático
        }
        else -> 1f
    }
}
