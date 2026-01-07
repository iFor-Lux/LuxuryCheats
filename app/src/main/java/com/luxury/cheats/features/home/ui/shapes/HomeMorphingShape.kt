package com.luxury.cheats.features.home.ui.shapes

import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath

/**
 * Shape morphing para la animación de loading.
 * La secuencia inicia con la Cookie para evitar saltos visuales al activarse.
 */
class HomeMorphingShape(private val progress: Float) : Shape {

    private object ShapeConstants {
        const val MORPH_DURATION = 0.5f
        const val EASE_THRESHOLD = 0.5f
        const val EASE_FACTOR = 2f
        const val ONE_F = 1f
        
        const val RADIUS_SCALE_STD = 0.85f
        const val RADIUS_SCALE_FULL = 1f
        const val RADIUS_SCALE_WIDE = 1.2f
        
        const val SMOOTHING = 0.8f
        
        const val COOKIE_VERTICES = 4
        const val STAR_VERTICES = 8
        const val CIRCLE_VERTICES = 12
        
        const val STAR_INNER_COOKIE = 0.56f
        const val STAR_INNER_STD = 0.55f

        const val TRIANGLE_VERTICES = 3
        const val SQUARE_VERTICES = 4
        const val PENTAGON_VERTICES = 5
        const val HEXAGON_VERTICES = 6
    }

    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radius = minOf(size.width, size.height) / 2f
        val shapes = MorphingHelper.createShapes(radius, size.width / 2f, size.height / 2f)
        
        val totalShapes = shapes.size
        val duration = ShapeConstants.MORPH_DURATION

        val scaledProgress = progress * totalShapes % totalShapes
        val currentIndex = scaledProgress.toInt()
        val nextIndex = (currentIndex + 1) % totalShapes

        val localProgress = scaledProgress - currentIndex
        val morphProgress = calculateMorphProgress(localProgress, duration)

        val morph = Morph(shapes[currentIndex], shapes[nextIndex])
        val morphedPath = morph.toPath(morphProgress).asComposePath()

        return Outline.Generic(morphedPath)
    }

    private fun calculateMorphProgress(localProgress: Float, duration: Float): Float {
        return if (localProgress < duration) {
            val t = localProgress / duration
            if (t < ShapeConstants.EASE_THRESHOLD) {
                ShapeConstants.EASE_FACTOR * t * t
            } else {
                ShapeConstants.ONE_F - ShapeConstants.EASE_FACTOR * 
                    (ShapeConstants.ONE_F - t) * (ShapeConstants.ONE_F - t)
            }
        } else ShapeConstants.ONE_F
    }

    private object MorphingHelper {
        private const val ROUNDING_FACTOR = 0.15f
        private const val COOKIE_ROUNDING_FACTOR = 0.18f

        fun createShapes(radius: Float, centerX: Float, centerY: Float): List<RoundedPolygon> {
            val stdRounding = CornerRounding(radius * ROUNDING_FACTOR, ShapeConstants.SMOOTHING)
            val cookieRounding = CornerRounding(
                radius * 2f * COOKIE_ROUNDING_FACTOR, 
                ShapeConstants.SMOOTHING
            )

            return listOf(
                createStarShape(radius, centerX, centerY, cookieRounding, isCookie = true),
                createCircleShape(radius, centerX, centerY),
                createPolygonShape(
                    ShapeConstants.SQUARE_VERTICES, 
                    radius * ShapeConstants.RADIUS_SCALE_STD, 
                    centerX, 
                    centerY, 
                    stdRounding
                ),
                createPolygonShape(
                    ShapeConstants.TRIANGLE_VERTICES, 
                    radius * ShapeConstants.RADIUS_SCALE_FULL, 
                    centerX, 
                    centerY, 
                    stdRounding
                ),
                createPolygonShape(
                    ShapeConstants.PENTAGON_VERTICES, 
                    radius * ShapeConstants.RADIUS_SCALE_STD, 
                    centerX, 
                    centerY, 
                    stdRounding
                ),
                createPolygonShape(
                    ShapeConstants.HEXAGON_VERTICES, 
                    radius * ShapeConstants.RADIUS_SCALE_STD, 
                    centerX, 
                    centerY, 
                    stdRounding
                ),
                createStarShape(
                    radius * ShapeConstants.RADIUS_SCALE_WIDE, 
                    centerX, 
                    centerY, 
                    stdRounding, 
                    isCookie = false
                )
            )
        }

        private fun createStarShape(
            radius: Float, 
            centerX: Float, 
            centerY: Float, 
            rounding: CornerRounding,
            isCookie: Boolean
        ): RoundedPolygon {
            return RoundedPolygon.star(
                numVerticesPerRadius = if (isCookie) {
                    ShapeConstants.COOKIE_VERTICES
                } else {
                    ShapeConstants.STAR_VERTICES
                },
                radius = radius,
                innerRadius = radius * if (isCookie) {
                    ShapeConstants.STAR_INNER_COOKIE
                } else {
                    ShapeConstants.STAR_INNER_STD
                },
                rounding = rounding,
                innerRounding = if (isCookie) rounding else CornerRounding.Unrounded,
                centerX = centerX,
                centerY = centerY
            )
        }

        private fun createCircleShape(radius: Float, centerX: Float, centerY: Float): RoundedPolygon {
            return RoundedPolygon.circle(
                numVertices = ShapeConstants.CIRCLE_VERTICES,
                radius = radius * ShapeConstants.RADIUS_SCALE_STD,
                centerX = centerX,
                centerY = centerY
            )
        }

        private fun createPolygonShape(
            vertices: Int, 
            radius: Float, 
            centerX: Float, 
            centerY: Float, 
            rounding: CornerRounding
        ): RoundedPolygon {
            return RoundedPolygon(
                numVertices = vertices,
                radius = radius,
                rounding = rounding,
                centerX = centerX,
                centerY = centerY
            )
        }
    }
}
