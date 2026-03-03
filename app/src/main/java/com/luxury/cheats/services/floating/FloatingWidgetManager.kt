package com.luxury.cheats.services.floating

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data class que representa la configuración actual del widget flotante.
 * Todos los valores están ahora en DP para consistencia entre dispositivos.
 */
data class FloatingWidgetConfig(
    val width: Int = 180,
    val height: Int = 35,
    val centerX: Int = 200, // Centro X en DP
    val centerY: Int = 400, // Centro Y en DP
    val strokeWidth: Float = 0f,
    val isStrokeEnabled: Boolean = false,
    val strokeColor: Long = 0xFFFFFFFF // Blanco por defecto
)

/**
 * Manager Singleton que gestiona el estado del widget flotante.
 */
@Singleton
class FloatingWidgetManager @Inject constructor() {
    private val _config = MutableStateFlow(FloatingWidgetConfig())
    val config: StateFlow<FloatingWidgetConfig> = _config.asStateFlow()

    fun updateConfig(
        width: Int? = null,
        height: Int? = null,
        centerX: Int? = null, // En DP
        centerY: Int? = null, // En DP
        strokeWidth: Float? = null,
        isStrokeEnabled: Boolean? = null,
        strokeColor: Long? = null
    ) {
        _config.update { current ->
            current.copy(
                width = width ?: current.width,
                height = height ?: current.height,
                centerX = centerX ?: current.centerX,
                centerY = centerY ?: current.centerY,
                strokeWidth = strokeWidth ?: current.strokeWidth,
                isStrokeEnabled = isStrokeEnabled ?: current.isStrokeEnabled,
                strokeColor = strokeColor ?: current.strokeColor
            )
        }
    }
}
