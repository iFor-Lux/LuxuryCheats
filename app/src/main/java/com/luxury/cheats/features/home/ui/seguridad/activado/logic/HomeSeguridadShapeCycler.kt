package com.luxury.cheats.features.home.ui.seguridad.activado.logic

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.graphics.shapes.RoundedPolygon

/**
 * Gestor de ciclo de formas para el componente Inner activado.
 * Cicla entre diferentes formas de Material 3 Expressive.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
object HomeSeguridadShapeCycler {
    
    /**
     * Lista de formas disponibles para ciclar.
     * Cada forma es un RoundedPolygon de MaterialShapes.
     * Solo incluye formas confirmadas disponibles en Material 3 Expressive.
     */
    private val availableShapes = listOf(
        ShapeInfo("Cookie7Sided", MaterialShapes.Cookie7Sided),
        ShapeInfo("Cookie9Sided", MaterialShapes.Cookie9Sided),
        ShapeInfo("Cookie4Sided", MaterialShapes.Cookie4Sided),
        ShapeInfo("Cookie6Sided", MaterialShapes.Cookie6Sided),
        ShapeInfo("Gem", MaterialShapes.Gem),
        ShapeInfo("SoftBurst", MaterialShapes.SoftBurst),
        ShapeInfo("Clover4Leaf", MaterialShapes.Clover4Leaf),
        ShapeInfo("Pill", MaterialShapes.Pill)
    )
    
    /**
     * Composable que gestiona el estado del índice de forma actual.
     * Retorna la forma actual y una función para avanzar al siguiente.
     */
    @Composable
    fun rememberShapeCycleState(): ShapeCycleState {
        var currentIndex by remember { mutableIntStateOf(0) }
        
        return ShapeCycleState(
            currentShape = availableShapes[currentIndex],
            nextShape = {
                currentIndex = (currentIndex + 1) % availableShapes.size
            },
            currentIndex = currentIndex,
            totalShapes = availableShapes.size
        )
    }
}

/**
 * Información de una forma.
 */
data class ShapeInfo(
    val name: String,
    val polygon: RoundedPolygon
)

/**
 * Estado del ciclo de formas.
 */
data class ShapeCycleState(
    val currentShape: ShapeInfo,
    val nextShape: () -> Unit,
    val currentIndex: Int,
    val totalShapes: Int
)
