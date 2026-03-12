package com.luxury.cheats.services.floating

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
    widthDp: Int,
    heightDp: Int,
    strokeWidthDp: Float,
    isStrokeEnabled: Boolean,
    strokeColorLong: Long = 0xFFFFFFFF,
    isMenuVisible: Boolean = false,
    onToggleMenu: () -> Unit = {}
) {
    Log.d("FloatingWidget", "Recomponiendo Widget: WidthDp=$widthDp, HeightDp=$heightDp, MenuOpen=$isMenuVisible")

    val strokeWidth = if (isStrokeEnabled) strokeWidthDp else 0f
    val shape = RoundedCornerShape(24.dp)

    // Resolvemos el color: 0 significa "Dinámico" (Primary)
    val strokeColor = if (strokeColorLong == 0L) {
        MaterialTheme.colorScheme.primary
    } else {
        Color(strokeColorLong)
    }

    Box(
        modifier = Modifier
            .systemGestureExclusion()
            .fillMaxWidth()
            .pointerInput(isMenuVisible) {
                if (isMenuVisible) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        down.consume()
                        onToggleMenu()
                        var unconsumed = true
                        while (unconsumed) {
                            val event = awaitPointerEvent()
                            event.changes.forEach { it.consume() }
                            if (event.changes.none { it.pressed }) {
                                unconsumed = false
                            }
                        }
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barra Visual y Táctil Exacta (Sin buffer oculto)
            Box(
                modifier = Modifier
                    .systemGestureExclusion() // Evita gestos de barra de estado
                    .size(
                        width = (widthDp + (strokeWidth * 2)).dp,
                        height = (heightDp + (strokeWidth * 2)).dp
                    )
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            Log.d("FloatingWidget", "ACTION_DOWN registrado en X: ${down.position.x}, Y: ${down.position.y}")
                            down.consume()
                            onToggleMenu()
                            var unconsumed = true
                            while (unconsumed) {
                                val event = awaitPointerEvent()
                                event.changes.forEach { 
                                    if (it.isConsumed) {
                                       Log.d("FloatingWidget", "Evento de movimiento consumido por otro componente! X: ${it.position.x}, Y: ${it.position.y}")
                                    } else {
                                       it.consume() 
                                    }
                                }
                                if (event.changes.none { it.pressed }) {
                                    Log.d("FloatingWidget", "ACTION_UP o CANCEL. Fin del gesto.")
                                    unconsumed = false
                                }
                            }
                        }
                    }
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
                    .background(Color.Black, shape),
                contentAlignment = Alignment.Center
            ) {
                // Contenido interno si fuera necesario
            }

            // Menú (Cuadro que se muestra al dar click)
            AnimatedVisibility(visible = isMenuVisible) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(width = 160.dp, height = 160.dp)
                        .background(
                            Color.Black.copy(alpha = 0.9f),
                            RoundedCornerShape(16.dp)
                        )
                        .systemGestureExclusion()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        )
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                val down = awaitFirstDown()
                                down.consume()
                                var unconsumed = true
                                while (unconsumed) {
                                    val event = awaitPointerEvent()
                                    event.changes.forEach { it.consume() }
                                    if (event.changes.none { it.pressed }) {
                                        unconsumed = false
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "MENU",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
