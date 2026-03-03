package com.luxury.cheats.features.tools.logic

/**
 * Acciones del usuario en la pantalla de Herramientas
 */
sealed class ToolsAction {
    data class UpdateFloatingConfig(
        val width: Int? = null,
        val height: Int? = null,
        val centerX: Int? = null,
        val centerY: Int? = null,
        val strokeWidth: Float? = null,
        val isStrokeEnabled: Boolean? = null,
        val strokeColor: Long? = null
    ) : ToolsAction()

    object ToggleFloatingWidget : ToolsAction()
}
