package com.luxury.cheats.features.home.ui.seguridad.cambios

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath

/**
 * Componente Outer unificado que hace morphing entre:
 * - Desactivado: Cookie7Sided (tertiary color)
 * - Activado: Circle (primary color)
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeSeguridadOuter(
    size: Dp,
    isActivated: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    // Animación del progreso de morphing: 0f = Cookie7Sided, 1f = Circle
    val morphProgress by animateFloatAsState(
        targetValue = if (isActivated) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "ShapeMorphing"
    )

    val shape = remember(morphProgress) { 
        MorphingShape(morphProgress) 
    }

    val backgroundColor = if (isActivated) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
    }

    val borderColor = if (isActivated) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = backgroundColor,
                shape = shape
            )
            .border(
                width = 3.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Shape que hace morphing entre Cookie7Sided y Circle
 * @param progress 0f = Cookie7Sided, 1f = Circle
 */
private class MorphingShape(
    private val progress: Float
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radius = minOf(size.width, size.height) / 2f
        
        // Interpolar entre Cookie7Sided y Circle
        // Cookie: innerRadius = 0.7, rounding = 0.4
        // Circle: innerRadius = 0.95 (casi sin puntas pero < 1.0), rounding = 1.0 (completamente redondo)
        val innerRadius = 0.7f + (0.25f * progress) // 0.7 -> 0.95
        val roundingAmount = 0.4f + (0.6f * progress) // 0.4 -> 1.0
        
        val polygon = RoundedPolygon.star(
            numVerticesPerRadius = 7,
            innerRadius = radius * innerRadius,
            rounding = CornerRounding(radius = radius * roundingAmount),
            innerRounding = CornerRounding(radius = radius * roundingAmount),
            centerX = size.width / 2f,
            centerY = size.height / 2f,
            radius = radius
        )
        
        val path = android.graphics.Path()
        polygon.toPath(path)
        
        // Rotar solo cuando no es círculo completo
        if (progress < 0.99f) {
            val matrix = android.graphics.Matrix()
            matrix.postRotate(-90f, size.width / 2f, size.height / 2f)
            path.transform(matrix)
        }
        
        path.close()
        return Outline.Generic(path.asComposePath())
    }
}
