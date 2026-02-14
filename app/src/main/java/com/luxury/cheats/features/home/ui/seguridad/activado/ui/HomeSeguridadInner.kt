package com.luxury.cheats.features.home.ui.seguridad.activado.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath

private object InnerConstants {
    const val NUM_VERTICES = 7
    const val INNER_RADIUS_RATIO = 0.7f
    const val ROUNDING_RATIO = 0.4f
    const val ALIGNMENT_ROTATION = -90f
    const val ICON_SIZE_DP = 48
}

/**
 * Componente interno del botón de seguridad cuando está activado.
 * Implementa un polígono de 7 lados con morphing y un icono de seguridad.
 *
 * @param modifier Modificador de layout.
 * @param rotation Ángulo de rotación para la animación.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun homeSeguridadInner(
    modifier: Modifier = Modifier,
    rotation: Float = 0f,
) {
    val shape = remember { HomeSeguridadInnerShape() }

    Box(
        modifier =
            modifier
                .graphicsLayer { rotationZ = rotation }
                .clip(shape)
                .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = "Seguridad Activada",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier =
                Modifier
                    .size(InnerConstants.ICON_SIZE_DP.dp)
                    .graphicsLayer { rotationZ = -rotation },
        )
    }
}

private class HomeSeguridadInnerShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val radius = minOf(size.width, size.height) / 2f

        // \"Cookie7Sided\" manual implementation mimicking official spec
        // 7 vertices, smoothed corners
        val polygon =
            RoundedPolygon.star(
                numVerticesPerRadius = InnerConstants.NUM_VERTICES,
                innerRadius = radius * InnerConstants.INNER_RADIUS_RATIO,
                rounding = CornerRounding(radius = radius * InnerConstants.ROUNDING_RATIO),
                innerRounding = CornerRounding(radius = radius * InnerConstants.ROUNDING_RATIO),
                centerX = size.width / 2f,
                centerY = size.height / 2f,
                radius = radius,
            )
        val path = android.graphics.Path()
        polygon.toPath(path)

        // Rotate the path to align the top point (12 o'clock)
        // Since RoundedPolygon.star starts at 3 o'clock (0 degrees), we need -90 degrees
        val matrix = android.graphics.Matrix()
        matrix.postRotate(InnerConstants.ALIGNMENT_ROTATION, size.width / 2f, size.height / 2f)
        path.transform(matrix)

        path.close()
        return Outline.Generic(path.asComposePath())
    }
}
