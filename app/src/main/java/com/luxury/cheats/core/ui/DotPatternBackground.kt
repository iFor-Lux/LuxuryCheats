package com.luxury.cheats.core.ui

import androidx.compose.ui.draw.drawWithCache
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import kotlin.math.ceil

// Constantes para Detekt y legibilidad
private const val MAX_DOT_LIMIT = 200
private const val DOT_COLOR_ALPHA_DARK = 0.5f // Aumentado un poco más
private const val DOT_COLOR_ALPHA_LIGHT = 0.6f

/**
 * Background estático de puntos (Dot Pattern)
 */
@Composable
fun DotPatternBackground(
    modifier: Modifier = Modifier,
    dotSpacingX: Float = 50f,
    dotSpacingY: Float = 50f,
    dotRadius: Float = 3f
) {
    val isDark = isSystemInDarkTheme()
    val dotColor = getDotColor(isDark, MaterialTheme.colorScheme.onBackground)
    val backgroundColor = MaterialTheme.colorScheme.background

    androidx.compose.foundation.layout.Spacer(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val widthPx = size.width
                val heightPx = size.height

                // Cálculo de posiciones
                val cols = ceil(widthPx / dotSpacingX).toInt().coerceAtMost(MAX_DOT_LIMIT)
                val rows = ceil(heightPx / dotSpacingY).toInt().coerceAtMost(MAX_DOT_LIMIT)

                val startX = (widthPx - (cols - 1) * dotSpacingX) / 2f
                val startY = (heightPx - (rows - 1) * dotSpacingY) / 2f

                val dots = buildList {
                    for (r in 0 until rows) {
                        for (c in 0 until cols) {
                            val x = startX + c * dotSpacingX
                            val y = startY + r * dotSpacingY
                            val fadeAlpha = calculateFadeAlpha(y)
                            
                            add(
                                Triple(
                                    Offset(x, y),
                                    dotRadius,
                                    dotColor.copy(alpha = dotColor.alpha * fadeAlpha)
                                )
                            )
                        }
                    }
                }

                onDrawBehind {
                    // drawRect(color = backgroundColor) // Eliminado para transparencia
                    
                    dots.forEach { (offset, radius, color) ->
                        drawCircle(
                            color = color,
                            radius = radius,
                            center = offset
                        )
                    }
                }
            }
    )
}

@Composable
private fun getDotColor(isDark: Boolean, onBackground: Color): Color {
    return if (isDark) {
        onBackground.copy(alpha = DOT_COLOR_ALPHA_DARK)
    } else {
        onBackground.copy(alpha = DOT_COLOR_ALPHA_LIGHT)
    }
}

private const val FADE_START_Y = 80f // Ajustado: Empieza casi desde arriba
private const val FADE_END_Y = 2500f

private fun calculateFadeAlpha(y: Float): Float {
    return when {
        y < FADE_START_Y -> 0f // Completamente transparente al inicio
        y < FADE_END_Y -> {
            val progress = (y - FADE_START_Y) / (FADE_END_Y - FADE_START_Y)
            progress * progress // Interpolación cuadrática para suavidad premium
        }
        else -> 1f
    }
}
