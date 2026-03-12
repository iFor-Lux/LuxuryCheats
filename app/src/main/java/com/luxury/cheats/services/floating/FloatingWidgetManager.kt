package com.luxury.cheats.services.floating

import com.luxury.cheats.services.storage.UserPreferencesService
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
    val strokeColor: Long = 0xFFFFFFFF, // Blanco por defecto
    val isMenuVisible: Boolean = false
)

/**
 * Manager Singleton que gestiona el estado del widget flotante.
 */
@Singleton
class FloatingWidgetManager @Inject constructor(
    private val preferencesService: UserPreferencesService
) {
    private val _config = MutableStateFlow(loadInitialConfig())
    val config: StateFlow<FloatingWidgetConfig> = _config.asStateFlow()

    private fun loadInitialConfig(): FloatingWidgetConfig {
        val saved = preferencesService.accessFloatingConfig()
        return FloatingWidgetConfig(
            width = saved["width"] as Int,
            height = saved["height"] as Int,
            centerX = saved["centerX"] as Int,
            centerY = saved["centerY"] as Int,
            strokeWidth = saved["strokeWidth"] as Float,
            isStrokeEnabled = saved["isStrokeEnabled"] as Boolean,
            strokeColor = saved["strokeColor"] as Long
        )
    }

    fun toggleMenu() {
        _config.update { it.copy(isMenuVisible = !it.isMenuVisible) }
    }

    fun updateConfig(
        width: Int? = null,
        height: Int? = null,
        centerX: Int? = null, // En DP
        centerY: Int? = null, // En DP
        strokeWidth: Float? = null,
        isStrokeEnabled: Boolean? = null,
        strokeColor: Long? = null
    ) {
        // Actualizar en el servicio de persistencia
        preferencesService.accessFloatingConfig(
            width = width,
            height = height,
            centerX = centerX,
            centerY = centerY,
            strokeWidth = strokeWidth,
            isStrokeEnabled = isStrokeEnabled,
            strokeColor = strokeColor
        )

        // Actualizar el estado dinámico
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
