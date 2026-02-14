package com.luxury.cheats.features.home.ui.seguridad.desactivado.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeSeguridadOuter(
    size: Dp,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val shape = remember { HomeSeguridadOuterShape() }

    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                shape = shape
            )
            .border(
                width = 3.dp,
                color = MaterialTheme.colorScheme.tertiary,
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

private class HomeSeguridadOuterShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radius = minOf(size.width, size.height) / 2f
        
        // "Cookie7Sided" manual implementation mimicking official spec
        val polygon = RoundedPolygon.star(
            numVerticesPerRadius = 7,
            innerRadius = radius * 0.7f, 
            rounding = CornerRounding(radius = radius * 0.4f),
            innerRounding = CornerRounding(radius = radius * 0.4f),
            centerX = size.width / 2f,
            centerY = size.height / 2f,
            radius = radius
        )
        val path = android.graphics.Path()
        polygon.toPath(path)
        
        // Rotate the path to align the top point (12 o'clock)
        val matrix = android.graphics.Matrix()
        matrix.postRotate(-90f, size.width / 2f, size.height / 2f)
        path.transform(matrix)
        
        path.close()
        return Outline.Generic(path.asComposePath())
    }
}
