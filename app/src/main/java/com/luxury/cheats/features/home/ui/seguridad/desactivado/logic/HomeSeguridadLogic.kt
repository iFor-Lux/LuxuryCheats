package com.luxury.cheats.features.home.ui.seguridad.desactivado.logic

import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue

/**
 * Lógica de eventos y estados para la sección de Seguridad Desactivada.
 * Maneja animaciones y lógica de presentación que no es puramente dibujo.
 */
object HomeSeguridadLogic {

    @Composable
    fun animateRotation(isUnlocked: Boolean): State<Float> {
        return animateFloatAsState(
            targetValue = if (isUnlocked) 360f else 0f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            label = "rotation"
        )
    }

    fun calculatePadding(isUnlocked: Boolean): androidx.compose.ui.unit.Dp {
        return if (isUnlocked) 10.dp else 40.dp
    }
}
