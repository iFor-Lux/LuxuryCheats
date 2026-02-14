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
import androidx.compose.foundation.layout.size
import kotlinx.coroutines.delay

/**
 * Componente Inner activado con morphing automático entre formas.
 * - Espera 600ms (duración de animación Outer) antes de iniciar
 * - Rotación continua entre cada cambio de forma
 * - Sensación fluida tipo Material 3 Expressive
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeSeguridadInnerMorphing(
    modifier: Modifier = Modifier,
    rotation: Float = 0f,
    onClick: () -> Unit = {}
) {
    val shapeCycleState = HomeSeguridadShapeCycler.rememberShapeCycleState()
    var baseRotation by remember { mutableFloatStateOf(0f) }
    
    // Rotación continua infinita lenta (360° cada 8 segundos)
    // Esta animación es INDEPENDIENTE del cambio de formas
    val infiniteTransition = rememberInfiniteTransition(label = "InfiniteRotation")
    val continuousRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ContinuousRotation"
    )
    
    // Ciclo automático de formas (independiente de la rotación)
    LaunchedEffect(Unit) {
        // Esperar a que termine la animación del Outer (600ms)
        delay(600)
        
        while (true) {
            delay(1500) // Cambiar forma cada 1.5 segundos
            shapeCycleState.nextShape()
            baseRotation += 45f // Rotación adicional al cambiar forma
        }
    }
    
    // Animación suave de la rotación base
    val animatedBaseRotation by animateFloatAsState(
        targetValue = baseRotation,
        animationSpec = tween(durationMillis = 400),
        label = "BaseRotation"
    )
    
    val shape = remember(shapeCycleState.currentShape) {
        MorphingInnerShape(shapeCycleState.currentShape.polygon)
    }

    Box(
        modifier = modifier
            .graphicsLayer { 
                rotationZ = rotation + animatedBaseRotation + continuousRotation
            }
            .clip(shape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = "Seguridad Activada - ${shapeCycleState.currentShape.name}",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer { 
                    rotationZ = -(rotation + animatedBaseRotation + continuousRotation)
                }
        )
    }
}

/**
 * Shape wrapper para RoundedPolygon de MaterialShapes.
 */
private class MorphingInnerShape(
    private val polygon: RoundedPolygon
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radius = minOf(size.width, size.height) / 2f
        
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
            (size.height - bounds.height() * scale) / 2f - bounds.top * scale
        )
        
        // Rotar -90 grados para alinear al top
        matrix.postRotate(-90f, size.width / 2f, size.height / 2f)
        
        path.transform(matrix)
        path.close()
        
        return Outline.Generic(path.asComposePath())
    }
}
