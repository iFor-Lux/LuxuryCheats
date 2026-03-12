package com.luxury.cheats.services.floating

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import android.graphics.Color as AndroidColor

@AndroidEntryPoint
class IslandAccessibilityService : AccessibilityService() {

    @Inject lateinit var floatingWidgetManager: FloatingWidgetManager
    
    private lateinit var windowManager: WindowManager
    private var rootContainer: FrameLayout? = null
    private var composeView: ComposeView? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("IslandAccessibility", "Servicio conectado")
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        showFloatingIsland()
        observeWidgetConfig()
    }

    private fun observeWidgetConfig() {
        serviceScope.launch {
            floatingWidgetManager.config.collectLatest { config ->
                updateIslandUI(config)
            }
        }
    }

    private fun updateIslandUI(config: FloatingWidgetConfig) {
        val params = rootContainer?.layoutParams as? WindowManager.LayoutParams
        params?.let {
            var changed = false
            val density = resources.displayMetrics.density
            val strokePadding = if (config.isStrokeEnabled) config.strokeWidth else 0f

            val menuExpandedHeight = 250
            val currentHeight = if (config.isMenuVisible) menuExpandedHeight else config.height
            val menuWidth = 160
            val currentWidth = if (config.isMenuVisible) maxOf(config.width, menuWidth) else config.width

            val totalWidthPx = ((currentWidth + (strokePadding * 2)) * density).toInt()
            val totalHeightPx = ((currentHeight + (strokePadding * 2)) * density).toInt()

            if (it.width != totalWidthPx || it.height != totalHeightPx) {
                it.width = totalWidthPx
                it.height = totalHeightPx
                changed = true
            }

            val centerXpx = config.centerX * density
            val centerYpx = config.centerY * density
            val newXPx = (centerXpx - (totalWidthPx / 2f)).toInt()
            val barHeightWithStrokePx = (config.height + (strokePadding * 2)) * density
            val newYPx = (centerYpx - (barHeightWithStrokePx / 2f)).toInt()

            if (it.x != newXPx || it.y != newYPx) {
                it.x = newXPx
                it.y = newYPx
                changed = true
            }

            if (changed) {
                windowManager.updateViewLayout(rootContainer, it)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No necesitamos procesar eventos de accesibilidad específicos por ahora,
        // ya que usaremos el overlay táctil directo.
    }

    override fun onInterrupt() {
        Log.d("IslandAccessibility", "Servicio interrumpido")
    }

    private fun showFloatingIsland() {
        val config = floatingWidgetManager.config.value
        val density = resources.displayMetrics.density
        val strokePadding = if (config.isStrokeEnabled) config.strokeWidth else 0f

        val totalWidthPx = ((config.width + (strokePadding * 2)) * density).toInt()
        val totalHeightPx = ((config.height + (strokePadding * 2)) * density).toInt()

        val params = WindowManager.LayoutParams(
            totalWidthPx,
            totalHeightPx,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
            WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = ((config.centerX * density) - (totalWidthPx / 2f)).toInt()
            y = ((config.centerY * density) - ((config.height + (strokePadding * 2)) * density / 2f)).toInt()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            }
        }

        composeView = ComposeView(this).apply {
            setContent {
                val currentConfig by floatingWidgetManager.config.collectAsState()
                FloatingWidgetContent(
                    widthDp = currentConfig.width,
                    heightDp = currentConfig.height,
                    strokeWidthDp = currentConfig.strokeWidth,
                    isStrokeEnabled = currentConfig.isStrokeEnabled,
                    strokeColorLong = currentConfig.strokeColor,
                    isMenuVisible = currentConfig.isMenuVisible,
                    onToggleMenu = { floatingWidgetManager.toggleMenu() }
                )
            }
        }

        rootContainer = object : FrameLayout(this) {
            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                if (ev.action == MotionEvent.ACTION_DOWN) {
                    Log.d("IslandAccessibility", "TOUCH en Isla! X: ${ev.rawX}, Y: ${ev.rawY}")
                }
                return super.dispatchTouchEvent(ev)
            }
        }.apply {
            setBackgroundColor(AndroidColor.argb(1, 0, 0, 0))
            addView(composeView)
            
            // Requerido para Compose en un Service/AccessibilityService
            setViewTreeLifecycleOwner(this@IslandAccessibilityService)
            // Nota: AccessibilityService no implementa ViewModelStoreOwner ni SavedStateRegistryOwner por defecto.
            // Para una implementación simple de la isla, FloatingWidgetContent podría no necesitarlos
            // si no usa ViewModels internos. Si los usa, LuxuryApp podría proporcionarlos.
        }

        try {
            windowManager.addView(rootContainer, params)
            Log.d("IslandAccessibility", "Window TYPE_ACCESSIBILITY_OVERLAY añadida")
        } catch (e: Exception) {
            Log.e("IslandAccessibility", "Error al añadir ventana: ${e.message}")
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        rootContainer?.let {
            windowManager.removeView(it)
        }
        super.onDestroy()
    }
}
