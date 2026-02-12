package com.luxury.cheats.features.home.ui.shapes

import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath

// Parámetros específicos para la Cookie de 4 lados (Estado bloqueado)
private const val COOKIE_INNER_RADIUS_RATIO = 0.35f 
private const val COOKIE_ROUNDING_RATIO = 0.22f     

/**
 * Shape de "Cookie" de 4 lados al estilo Material Design 3 Expressive.
 */
val HomeCookieShape: Shape = object : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val width = size.width
        val height = size.height
        val minSize = minOf(width, height)
        val radius = minSize / 2f

        val rounding = CornerRounding(
            radius = minSize * 0.25f, // Ligeramente más redondeado
            smoothing = 0.95f         // Máximo suavizado para look orgánico (Expressive)
        )

        val polygon = RoundedPolygon.star(
            numVerticesPerRadius = 4,
            radius = radius,
            innerRadius = radius * 0.45f, // Menos pronunciado, más sólido
            rounding = rounding,
            innerRounding = rounding,
            centerX = width / 2f,
            centerY = height / 2f
        )

        val path = polygon.toPath().asComposePath()
        return Outline.Generic(path)
    }
}
