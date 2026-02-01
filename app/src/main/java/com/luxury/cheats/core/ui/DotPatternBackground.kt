package com.luxury.cheats.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import kotlin.math.ceil

// Constantes para Detekt y legibilidad
private const val FADE_HEIGHT_PX = 1500f
private const val MAX_DOT_LIMIT = 150
private const val DOT_COLOR_ALPHA_DARK = 0.3f
private const val DOT_COLOR_ALPHA_LIGHT = 0.7f

/**
 * Background estático de puntos (Dot Pattern)
 */
@Composable
fun DotPatternBackground(
    modifier: Modifier = Modifier,
    dotSpacingX: Float = 40f,
    dotSpacingY: Float = 40f,
    dotRadius: Float = 2f
) {
    val isDark = isSystemInDarkTheme()
    val dotColor = getDotColor(isDark, MaterialTheme.colorScheme.onBackground)
    val backgroundColor = MaterialTheme.colorScheme.background

    Canvas(modifier = modifier.fillMaxSize()) {
        val widthPx = size.width
        val heightPx = size.height

        val cols = ceil(widthPx / dotSpacingX).toInt().coerceAtMost(MAX_DOT_LIMIT)
        val rows = ceil(heightPx / dotSpacingY).toInt().coerceAtMost(MAX_DOT_LIMIT)

        val startX = (widthPx - (cols - 1) * dotSpacingX) / 2f
        val startY = (heightPx - (rows - 1) * dotSpacingY) / 2f

        drawRect(color = backgroundColor, size = size)

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val x = startX + c * dotSpacingX
                val y = startY + r * dotSpacingY

                val fadeAlpha = calculateFadeAlpha(y)
                drawCircle(
                    color = dotColor.copy(alpha = dotColor.alpha * fadeAlpha),
                    radius = dotRadius,
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
private fun getDotColor(isDark: Boolean, onBackground: Color): Color {
    return if (isDark) {
        onBackground.copy(alpha = DOT_COLOR_ALPHA_DARK)
    } else {
        onBackground.copy(alpha = DOT_COLOR_ALPHA_LIGHT)
    }
}


private fun calculateFadeAlpha(y: Float): Float {
    return if (y < FADE_HEIGHT_PX) {
        val progress = y / FADE_HEIGHT_PX
        progress * progress
    } else 1f
}


