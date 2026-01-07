package com.luxury.cheats.core.ui

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

private const val CENTER_X_RATIO = 0.5f
private const val CENTER_Y_RATIO = 0.97f
private const val FILL_COLOR_DARK = 0x80911AA9
private const val FILL_COLOR_LIGHT = 0x40B24ED1 // Morado más claro y vibrante
private const val STROKE_COLOR_DARK = 0x99270931
private const val STROKE_COLOR_LIGHT = 0x60911AA9 // Morado Luxury pero con transparencia

/**
 * Sección de Eclipse decorativo
 */
@Composable
fun WelcomeEclipseSection(
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val isDark = isSystemInDarkTheme()
    val (fillColor, strokeColor) = getEclipseColors(isDark)
    val blurRadius = with(density) { 80.dp.toPx() }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val eclipseWidth = with(density) { 352.dp.toPx() }
        val eclipseHeight = with(density) { 112.dp.toPx() }
        val centerX = widthPx * CENTER_X_RATIO
        val centerY = heightPx * CENTER_Y_RATIO
        val strokeWidth = with(density) { 90.dp.toPx() }

        Canvas(
            modifier = Modifier.fillMaxSize().then(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.graphicsLayer {
                        renderEffect = android.graphics.RenderEffect.createBlurEffect(
                            blurRadius, blurRadius, android.graphics.Shader.TileMode.CLAMP
                        ).asComposeRenderEffect()
                    }
                } else Modifier
            )
        ) {
            val topLeft = Offset(centerX - eclipseWidth / 2f, centerY - eclipseHeight / 2f)
            val size = Size(eclipseWidth, eclipseHeight)
            drawOval(color = fillColor, topLeft = topLeft, size = size)
            drawOval(color = strokeColor, topLeft = topLeft, size = size, style = Stroke(width = strokeWidth))
        }
    }
}

@Composable
private fun getEclipseColors(isDark: Boolean): Pair<Color, Color> {
    val fillColor = if (isDark) Color(FILL_COLOR_DARK) else Color(FILL_COLOR_LIGHT)
    val strokeColor = if (isDark) Color(STROKE_COLOR_DARK) else Color(STROKE_COLOR_LIGHT)
    return Pair(fillColor, strokeColor)
}
