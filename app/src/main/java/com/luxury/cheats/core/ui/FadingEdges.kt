package com.luxury.cheats.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Agrega gradientes de desvanecimiento en la parte superior e inferior de la pantalla.
 * Simula el efecto de "Fading Edges" de apps premium como ChatGPT o iOS.
 */
@Composable
fun FadingEdges(
    modifier: Modifier = Modifier,
    topHeight: Dp = 100.dp,
    bottomHeight: Dp = 120.dp,
    color: Color = MaterialTheme.colorScheme.background,
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Desvanecimiento Superior (Más largo e intenso)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(topHeight)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to color,
                            0.4f to color.copy(alpha = 0.95f),
                            1.0f to Color.Transparent
                        )
                    )
                )
        )

        // Desvanecimiento Inferior (Más largo e intenso)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomHeight)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Transparent,
                            0.6f to color.copy(alpha = 0.95f),
                            1.0f to color
                        )
                    )
                )
        )
    }
}
