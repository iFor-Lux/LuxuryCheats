package com.luxury.cheats.features.home.ui.seguridad.desactivado.logic

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp

/**
 * Lógica de eventos y estados para la sección de Seguridad Desactivada.
 * Maneja animaciones y lógica de presentación que no es puramente dibujo.
 */
object HomeSeguridadLogic {
    /**
     * Anima la rotación suavemente basándose en el estado de desbloqueo.
     * @param isUnlocked True si la seguridad está desbloqueada.
     * @return Estado con el valor de rotación actual.
     */
    @Composable
    fun animateRotation(isUnlocked: Boolean): State<Float> {
        return animateFloatAsState(
            targetValue = if (isUnlocked) 360f else 0f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            label = "rotation",
        )
    }

    /**
     * Calcula el padding dinámico para el contenedor interno.
     * @param isUnlocked True si la seguridad está desbloqueada.
     * @return Dp de padding basado en el estado.
     */
    fun calculatePadding(isUnlocked: Boolean): androidx.compose.ui.unit.Dp {
        return if (isUnlocked) 10.dp else 40.dp
    }
}
