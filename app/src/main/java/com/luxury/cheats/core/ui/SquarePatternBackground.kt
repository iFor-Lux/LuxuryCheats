package com.luxury.cheats.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.luxury.cheats.core.theme.luxuryCheatsTheme

// Constantes para Detekt y legibilidad
private const val GRID_COLOR_ALPHA_DARK = 0.40f
private const val GRID_COLOR_ALPHA_LIGHT = 0.50f
private const val FADE_START_Y = 500f
private const val FADE_END_Y = 1000f

/**
 * Background estático de cuadrícula (Square Pattern)
 * Proporciona un look técnico y premium.
 */
@Composable
fun SquarePatternBackground(
    modifier: Modifier = Modifier,
    squareSize: Float = 60f,
    strokeWidth: Float = 0.1f,
) {
    val isDark = isSystemInDarkTheme()
    val gridColor = getGridColor(isDark, MaterialTheme.colorScheme.onBackground)

    androidx.compose.foundation.layout.Spacer(
        modifier =
            modifier
                .fillMaxSize()
                .drawBehind {
                    val widthPx = size.width
                    val heightPx = size.height

                    // Brush para el desvanecimiento superior (Fade out en la parte de arriba)
                    val fadeBrush =
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colorStops =
                                arrayOf(
                                    0f to gridColor.copy(alpha = 0f),
                                    (FADE_START_Y / heightPx).coerceIn(0f, 1f) to gridColor.copy(alpha = 0f),
                                    (FADE_END_Y / heightPx).coerceIn(0f, 1f) to gridColor,
                                    1f to gridColor,
                                ),
                        )

                    // Dibujar líneas horizontales
                    var y = 0f
                    while (y <= heightPx) {
                        // Solo dibujamos si está por debajo del inicio del desvanecimiento total para optimizar
                        if (y >= FADE_START_Y) {
                            drawLine(
                                brush = fadeBrush,
                                start = Offset(0f, y),
                                end = Offset(widthPx, y),
                                strokeWidth = strokeWidth,
                            )
                        }
                        y += squareSize
                    }

                    // Dibujar líneas verticales
                    var x = 0f
                    while (x <= widthPx) {
                        drawLine(
                            brush = fadeBrush, // El mismo brush aplica el fade-out a la línea vertical
                            start = Offset(x, 0f),
                            end = Offset(x, heightPx),
                            strokeWidth = strokeWidth,
                        )
                        x += squareSize
                    }
                },
    )
}

@Composable
private fun getGridColor(
    isDark: Boolean,
    onBackground: Color,
): Color {
    return if (isDark) {
        onBackground.copy(alpha = GRID_COLOR_ALPHA_DARK)
    } else {
        onBackground.copy(alpha = GRID_COLOR_ALPHA_LIGHT)
    }
}

/** Preview de la cuadrícula en tema oscuro. */
@Preview(showBackground = true, backgroundColor = 0xFF080808)
@Composable
fun SquarePatternBackgroundDarkPreview() {
    luxuryCheatsTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize()) {
            SquarePatternBackground()
        }
    }
}

/** Preview de la cuadrícula en tema claro. */
@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun SquarePatternBackgroundLightPreview() {
    luxuryCheatsTheme(darkTheme = false) {
        Box(modifier = Modifier.fillMaxSize()) {
            SquarePatternBackground()
        }
    }
}
