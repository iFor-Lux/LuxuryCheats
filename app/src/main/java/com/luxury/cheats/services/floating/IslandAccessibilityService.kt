package com.luxury.cheats.services.floating

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout
import androidx.compose.ui.graphics.toArgb
import com.luxury.cheats.services.floating.FloatingWidgetContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IslandAccessibilityService : AccessibilityService(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {

    @Inject lateinit var floatingWidgetManager: FloatingWidgetManager

    private lateinit var windowManager: WindowManager
    private var rootContainer: FrameLayout? = null
    private var composeView: ComposeView? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val _viewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = _viewModelStore
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
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
            
            // Calculamos dimensiones con búfer incluido
            val widgetWidthPx = (config.width * density).toInt()
            val widgetHeightPx = (config.height * density).toInt()
            val bufferPx = (40 * density).toInt()
            
            val menuExpandedHeight = (280 + 20) * density // Menu + pequeño gap
            val targetContainerHeightPx = if (config.isMenuVisible) menuExpandedHeight.toInt() else (widgetHeightPx + bufferPx)
            val targetContainerWidthPx = if (config.isMenuVisible) (160 * density).toInt() else (widgetWidthPx + bufferPx)

            if (it.width != targetContainerWidthPx || it.height != targetContainerHeightPx) {
                it.width = targetContainerWidthPx
                it.height = targetContainerHeightPx
                changed = true
            }

            // POSICIÓN DINÁMICA: La ventana se mueve con el widget
            // y = centerY - (widgetHeight/2) - (20dp de padding superior)
            val newXPx = (config.centerX * density - it.width / 2).toInt()
            val newYPx = (config.centerY * density - (config.height * density) / 2 - (20 * density)).toInt()

            if (it.x != newXPx || it.y != newYPx) {
                it.x = newXPx
                it.y = newYPx
                changed = true
            }

            if (changed) {
                windowManager.updateViewLayout(rootContainer, it)
                Log.d("IslandAccessibility", "Ventana actualizada: X=${it.x}, Y=${it.y}, W=${it.width}, H=${it.height}")
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {
        Log.d("IslandAccessibility", "Servicio interrumpido")
    }

    private fun showFloatingIsland() {
        val config = floatingWidgetManager.config.value
        val density = resources.displayMetrics.density
        val totalWidthPx = ((config.width + 40) * density).toInt()
        val totalHeightPx = ((config.height + 40) * density).toInt()

        val params = WindowManager.LayoutParams(
            totalWidthPx,
            totalHeightPx,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (config.centerX * density - totalWidthPx / 2).toInt()
            y = (config.centerY * density - (config.height * density) / 2 - (20 * density)).toInt()
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            }
        }

        composeView = ComposeView(this).apply {
            setContent {
                val currentConfig by floatingWidgetManager.config.collectAsState()
                FloatingWidgetContent(
                    config = currentConfig,
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
            addView(composeView)

            // Requerido para Compose en un Service/AccessibilityService
            setViewTreeLifecycleOwner(this@IslandAccessibilityService)
            setViewTreeViewModelStoreOwner(this@IslandAccessibilityService)
            setViewTreeSavedStateRegistryOwner(this@IslandAccessibilityService)
        }

        try {
            windowManager.addView(rootContainer, params)
            Log.d("IslandAccessibility", "Window TYPE_ACCESSIBILITY_OVERLAY añadida")
        } catch (e: Exception) {
            Log.e("IslandAccessibility", "Error al añadir ventana: ${e.message}")
        }
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        _viewModelStore.clear()
        serviceScope.cancel()
        rootContainer?.let {
            windowManager.removeView(it)
        }
        super.onDestroy()
    }
}
