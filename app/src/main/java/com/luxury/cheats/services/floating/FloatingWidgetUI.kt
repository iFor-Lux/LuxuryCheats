package com.luxury.cheats.services.floating

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Contenido visual del widget flotante, compartido entre servicios.
 */
@Composable
fun FloatingWidgetContent(
    config: FloatingWidgetConfig,
    onToggleMenu: () -> Unit = {}
) {
    val widthDp = config.width
    val heightDp = config.height
    val strokeWidthDp = config.strokeWidth
    val isStrokeEnabled = config.isStrokeEnabled
    val strokeColorLong = config.strokeColor
    val isMenuVisible = config.isMenuVisible

    Log.d("FloatingWidget", "Recomponiendo Widget: CenterY=${config.centerY}, MenuOpen=$isMenuVisible")

    val strokeWidth = if (isStrokeEnabled) strokeWidthDp else 0f
    val shape = RoundedCornerShape(24.dp)
    val strokeColor = if (strokeColorLong == 0L) {
        MaterialTheme.colorScheme.primary
    } else {
        Color(strokeColorLong)
    }

    // El anclaje al top de la ventana es crítico para que crezca hacia abajo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(isMenuVisible) {
                if (isMenuVisible) {
                    awaitEachGesture {
                        awaitFirstDown().consume()
                        onToggleMenu()
                    }
                }
            },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.padding(top = 20.dp), // Búfer superior de 20dp para la ventana
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Contenedor táctil de la isla
            Box(
                modifier = Modifier
                    .size(width = (widthDp + 40).dp, height = (heightDp + 40).dp)
                    .systemGestureExclusion()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            awaitFirstDown().also { 
                                Log.d("FloatingWidget", "Click OK en Isla: Y local=${it.position.y}")
                                it.consume() 
                            }
                            onToggleMenu()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // La BARRA VISUAL real
                Box(
                    modifier = Modifier
                        .size(
                            width = (widthDp + (strokeWidth * 2)).dp,
                            height = (heightDp + (strokeWidth * 2)).dp
                        )
                        .then(
                            if (isStrokeEnabled) {
                                Modifier.border(
                                    width = strokeWidth.dp,
                                    color = strokeColor,
                                    shape = shape
                                )
                            } else Modifier
                        )
                        .padding(strokeWidth.dp)
                        .background(Color.Black, shape)
                )
            }

            // Menú
            AnimatedVisibility(visible = isMenuVisible) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(width = 160.dp, height = 280.dp)
                        .background(
                            Color.Black.copy(alpha = 0.95f),
                            RoundedCornerShape(24.dp)
                        )
                        .systemGestureExclusion()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            RoundedCornerShape(24.dp)
                        )
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                awaitFirstDown().consume()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "MENU",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
