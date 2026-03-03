package com.luxury.cheats.features.tools.logic

/**
 * Estado de la UI para la pantalla de Herramientas (Tools)
 */
data class ToolsState(
    val floatingWidth: Int = 180,
    val floatingHeight: Int = 35,
    val floatingCenterX: Int = 500,
    val floatingCenterY: Int = 500,
    val floatingStrokeWidth: Float = 0f,
    val isFloatingStrokeEnabled: Boolean = false,
    val floatingStrokeColor: Long = 0xFFFFFFFF,
    val isFloatingWidgetActive: Boolean = false
)
