package com.luxury.cheats.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        val dots = rememberDots(widthPx, heightPx, dotSpacingX, dotSpacingY)

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = backgroundColor, size = size)
            dots.forEach { dot ->
                if (Rect(Offset.Zero, size).contains(dot)) {
                    val fadeAlpha = calculateFadeAlpha(dot.y, FADE_HEIGHT_PX)
                    drawCircle(
                        color = dotColor.copy(alpha = dotColor.alpha * fadeAlpha),
                        radius = dotRadius,
                        center = dot
                    )
                }
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

@Composable
private fun rememberDots(width: Float, height: Float, spacingX: Float, spacingY: Float): List<Offset> {
    val cols = ceil(width / spacingX).toInt().coerceAtMost(MAX_DOT_LIMIT)
    val rows = ceil(height / spacingY).toInt().coerceAtMost(MAX_DOT_LIMIT)
    
    return remember(cols, rows, spacingX, spacingY) {
        List(cols * rows) { index ->
            val col = index % cols
            val row = index / cols
            val startX = (width - (cols - 1) * spacingX) / 2f
            val startY = (height - (rows - 1) * spacingY) / 2f
            Offset(x = startX + col * spacingX, y = startY + row * spacingY)
        }
    }
}

private fun calculateFadeAlpha(y: Float, fadeHeight: Float): Float {
    return if (y < fadeHeight) {
        val progress = y / fadeHeight
        progress * progress
    } else 1f
}


