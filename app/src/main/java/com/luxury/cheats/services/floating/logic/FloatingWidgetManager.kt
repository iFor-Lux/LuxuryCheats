package com.luxury.cheats.services.floating.logic

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
    val isMenuVisible: Boolean = false,
    val selectedCategory: String = "",
    val aimbotTarget: String = "Base",
    val fovRadius: Float = 50f,
    val isFovEnabled: Boolean = false,
    val isDragClickEnabled: Boolean = false,
    val dragClickX: Float = 0f,
    val dragClickY: Float = 0f,
    val isDragClickLocked: Boolean = false,
    val isPointScanEnabled: Boolean = false,
    val selectedMicroSection: String = "FOV",
    val crosshairSize: Float = 40f,
    val crosshairColor: Long = 0xFFFFD700, // Dorado Luxury
    val crosshairDesign: String = "Classic",
    val isGlooRotated: Boolean = false,
)

/**
 * Manager Singleton que gestiona el estado del widget flotante.
 */
@Singleton
class FloatingWidgetManager
    @Inject
    constructor(
        private val preferencesService: UserPreferencesService,
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
                strokeColor = saved["strokeColor"] as Long,
            )
        }

        /**
         * Alterna la visibilidad del menú desplegable del widget.
         */
        fun toggleMenu() {
            _config.update { it.copy(isMenuVisible = !it.isMenuVisible) }
        }

        /**
         * Selecciona una categoría específica para mostrar en el menú.
         * @param category Nombre de la categoría (Aimbot, Config, etc.)
         */
        fun selectCategory(category: String) {
            _config.update { it.copy(selectedCategory = category) }
        }

        /**
         * Establece el objetivo del aimbot (Cabeza, Pecho, etc.)
         * @param target Nombre del objetivo.
         */
        fun setAimbotTarget(target: String) {
            _config.update { it.copy(aimbotTarget = target) }
        }

        /**
         * Actualiza el radio del círculo FOV.
         * @param radius Radio en píxeles/dp.
         */
        fun updateFov(radius: Float) {
            _config.update { it.copy(fovRadius = radius) }
        }

        /**
         * Activa o desactiva la visualización del círculo FOV.
         * @param enabled Verdadero para mostrar, falso para ocultar.
         */
        fun toggleFov(enabled: Boolean) {
            _config.update { it.copy(isFovEnabled = enabled) }
        }

        /**
         * Activa o desactiva la función DragClick.
         * @param enabled Verdadero para activar.
         */
        fun toggleDragClick(enabled: Boolean) {
            _config.update { it.copy(isDragClickEnabled = enabled) }
        }

        /**
         * Actualiza la posición del punto de DragClick en la pantalla.
         * @param x Coordenada X.
         * @param y Coordenada Y.
         */
        fun updateDragClickPosition(
            x: Float,
            y: Float,
        ) {
            _config.update { it.copy(dragClickX = x, dragClickY = y) }
        }

        /**
         * Bloquea o desbloquea la posición del DragClick para evitar movimientos accidentales.
         * @param locked Verdadero para bloquear.
         */
        fun toggleDragClickLock(locked: Boolean) {
            _config.update { it.copy(isDragClickLocked = locked) }
        }

        /**
         * Activa o desactiva el escaneo de puntos.
         * @param enabled Verdadero para activar.
         */
        fun togglePointScan(enabled: Boolean) {
            _config.update { it.copy(isPointScanEnabled = enabled) }
        }

        /**
         * Cambia la sub-sección activa en la configuración de precisión.
         * @param section Nombre de la sub-sección.
         */
        fun updateMicroSection(section: String) {
            _config.update { it.copy(selectedMicroSection = section) }
        }

        /**
         * Actualiza el tamaño visual del crosshair.
         * @param size Tamaño en dp.
         */
        fun updateCrosshairSize(size: Float) {
            _config.update { it.copy(crosshairSize = size) }
        }

        /**
         * Actualiza el color del crosshair.
         * @param color Valor ARGB del color.
         */
        fun updateCrosshairColor(color: Long) {
            _config.update { it.copy(crosshairColor = color) }
        }

        /**
         * Cambia el diseño visual del crosshair.
         * @param design Nombre del diseño (Classic, Dot, etc.)
         */
        fun updateCrosshairDesign(design: String) {
            _config.update { it.copy(crosshairDesign = design) }
        }

        /**
         * Alterna el estado de rotación de la pared Gloo en la vista previa.
         */
        fun toggleGlooRotation() {
            _config.update { it.copy(isGlooRotated = !it.isGlooRotated) }
        }

        /**
         * Actualiza la configuración global de apariencia y posición del widget.
         * Persiste los cambios en UserPreferencesService.
         */
        fun updateConfig(
            width: Int? = null,
            height: Int? = null,
            centerX: Int? = null, // En DP
            centerY: Int? = null, // En DP
            strokeWidth: Float? = null,
            isStrokeEnabled: Boolean? = null,
            strokeColor: Long? = null,
        ) {
            // Actualizar en el servicio de persistencia
            preferencesService.accessFloatingConfig(
                width = width,
                height = height,
                centerX = centerX,
                centerY = centerY,
                strokeWidth = strokeWidth,
                isStrokeEnabled = isStrokeEnabled,
                strokeColor = strokeColor,
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
                    strokeColor = strokeColor ?: current.strokeColor,
                )
            }
        }
    }
