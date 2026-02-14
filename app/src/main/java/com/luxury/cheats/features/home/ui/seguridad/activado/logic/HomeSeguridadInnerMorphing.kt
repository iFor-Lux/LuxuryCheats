package com.luxury.cheats.features.home.ui.seguridad.activado.logic

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import kotlinx.coroutines.delay

private object MorphingConstants {
    const val INFINITE_ROTATION_DURATION = 8000
    const val OUTER_ANIM_DELAY = 600L
    const val SHAPE_CHANGE_DELAY = 1500L
    const val BASE_ROTATION_DURATION = 400
    const val ROTATION_INCREMENT = 45f
    const val ICON_SIZE_DP = 48
    const val ALIGNMENT_ROTATION = -90f
}

/**
 * Componente Inner activado con morphing automático entre formas.
 * - Espera 600ms (duración de animación Outer) antes de iniciar
 * - Rotación continua entre cada cambio de forma
 * - Sensación fluida tipo Material 3 Expressive
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun homeSeguridadInnerMorphing(
    modifier: Modifier = Modifier,
    rotation: Float = 0f,
    onClick: () -> Unit = {},
) {
    val shapeCycleState = HomeSeguridadShapeCycler.rememberShapeCycleState()
    var baseRotation by remember { mutableFloatStateOf(0f) }

    // Rotación continua infinita lenta (360° cada 8 segundos)
    // Esta animación es INDEPENDIENTE del cambio de formas
    val infiniteTransition = rememberInfiniteTransition(label = "InfiniteRotation")
    val continuousRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = MorphingConstants.INFINITE_ROTATION_DURATION, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "ContinuousRotation",
    )

    // Ciclo automático de formas (independiente de la rotación)
    LaunchedEffect(Unit) {
        // Esperar a que termine la animación del Outer (600ms)
        delay(MorphingConstants.OUTER_ANIM_DELAY)

        while (true) {
            delay(MorphingConstants.SHAPE_CHANGE_DELAY) // Cambiar forma cada 1.5 segundos
            shapeCycleState.nextShape()
            baseRotation += MorphingConstants.ROTATION_INCREMENT // Rotación adicional al cambiar forma
        }
    }

    // Animación suave de la rotación base
    val animatedBaseRotation by animateFloatAsState(
        targetValue = baseRotation,
        animationSpec = tween(durationMillis = MorphingConstants.BASE_ROTATION_DURATION),
        label = "BaseRotation",
    )

    val shape =
        remember(shapeCycleState.currentShape) {
            MorphingInnerShape(shapeCycleState.currentShape.polygon)
        }

    morphingInnerBox(
        modifier = modifier,
        rotation = rotation,
        state =
            MorphingState(
                baseRotation = animatedBaseRotation,
                continuousRotation = continuousRotation,
                shape = shape,
                shapeName = shapeCycleState.currentShape.name,
            ),
    )
}

@Composable
private fun morphingInnerBox(
    modifier: Modifier,
    rotation: Float,
    state: MorphingState,
) {
    val totalRotation = rotation + state.baseRotation + state.continuousRotation

    Box(
        modifier =
            modifier
                .graphicsLayer { rotationZ = totalRotation }
                .clip(state.shape)
                .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = "Seguridad Activada - ${state.shapeName}",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier =
                Modifier
                    .size(MorphingConstants.ICON_SIZE_DP.dp)
                    .graphicsLayer { rotationZ = -totalRotation },
        )
    }
}

/** Clase para agrupar el estado de la animación de morphing. */
private data class MorphingState(
    val baseRotation: Float,
    val continuousRotation: Float,
    val shape: Shape,
    val shapeName: String,
)

/**
 * Shape wrapper para RoundedPolygon de MaterialShapes.
 */
private class MorphingInnerShape(
    private val polygon: RoundedPolygon,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        // Escalar el polígono al tamaño del contenedor
        val path = android.graphics.Path()
        polygon.toPath(path)

        // Calcular el bounding box del polígono original
        val bounds = android.graphics.RectF()
        path.computeBounds(bounds, true)

        // Escalar y centrar
        val scaleX = size.width / bounds.width()
        val scaleY = size.height / bounds.height()
        val scale = minOf(scaleX, scaleY)

        val matrix = android.graphics.Matrix()
        matrix.postScale(scale, scale)
        matrix.postTranslate(
            (size.width - bounds.width() * scale) / 2f - bounds.left * scale,
            (size.height - bounds.height() * scale) / 2f - bounds.top * scale,
        )

        // Rotar -90 grados para alinear al top
        matrix.postRotate(MorphingConstants.ALIGNMENT_ROTATION, size.width / 2f, size.height / 2f)

        path.transform(matrix)
        path.close()

        return Outline.Generic(path.asComposePath())
    }
}
