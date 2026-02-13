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
    
    // Optimización: Reducir límite máximo para evitar freeze en pantallas grandes
    val safeMaxLimit = 100 // Antes 200

    androidx.compose.foundation.layout.Spacer(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                val widthPx = size.width
                val heightPx = size.height

                // Cálculo de posiciones
                val cols = ceil(widthPx / dotSpacingX).toInt().coerceAtMost(safeMaxLimit)
                val rows = ceil(heightPx / dotSpacingY).toInt().coerceAtMost(safeMaxLimit)

                val startX = (widthPx - (cols - 1) * dotSpacingX) / 2f
                val startY = (heightPx - (rows - 1) * dotSpacingY) / 2f

                // Batch circles by alpha to minimize draw calls
                // Key: Alpha (0..100 int for map key), Value: List of offsets
                val pointsByAlpha = mutableMapOf<Int, MutableList<Offset>>()

                for (r in 0 until rows) {
                    val y = startY + r * dotSpacingY
                    val fadeAlpha = calculateFadeAlpha(y)
                    
                    if (fadeAlpha <= 0f) continue // Skip invisible dots

                    val alphaKey = (fadeAlpha * 100).toInt()
                    val list = pointsByAlpha.getOrPut(alphaKey) { mutableListOf() }

                    for (c in 0 until cols) {
                        val x = startX + c * dotSpacingX
                        list.add(Offset(x, y))
                    }
                }

                onDrawBehind {
                    pointsByAlpha.forEach { (alphaKey, offsets) ->
                        val alpha = alphaKey / 100f
                        val color = dotColor.copy(alpha = dotColor.alpha * alpha)
                        
                        drawPoints(
                            points = offsets,
                            pointMode = androidx.compose.ui.graphics.PointMode.Points,
                            color = color,
                            strokeWidth = dotRadius * 2,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
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
