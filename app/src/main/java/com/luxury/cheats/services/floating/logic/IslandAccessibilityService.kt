package com.luxury.cheats.services.floating.logic

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
import com.luxury.cheats.services.floating.ui.FloatingWidgetContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class IslandAccessibilityService :
    AccessibilityService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {
    @Inject lateinit var floatingWidgetManager: FloatingWidgetManager

    companion object {
        private const val LAYOUT_BUFFER_DP = 40
        private const val MENU_PADDING_DP = 32
        private const val WIDGET_OFFSET_Y_DP = 20
        private const val MENU_BASE_HEIGHT_DP = 64
        private const val MAX_EXPANDED_HEIGHT_DP = 280
    }

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
            val bufferPx = (LAYOUT_BUFFER_DP * density).toInt()

            // ANCHURA ESTABLE: Adaptada para expansión a ancho de pantalla
            val screenWidthPx = resources.displayMetrics.widthPixels
            val menuPaddingPx = (MENU_PADDING_DP * density).toInt()
            val targetContainerWidthPx = maxOf(widgetWidthPx + bufferPx, screenWidthPx - menuPaddingPx + bufferPx)

            // Altura dinámica: 64dp para barra, 280dp para sección abierta
            val menuExpandedHeight = if (config.selectedCategory.isNotEmpty()) {
                (MAX_EXPANDED_HEIGHT_DP + WIDGET_OFFSET_Y_DP) * density
            } else {
                (MENU_BASE_HEIGHT_DP + WIDGET_OFFSET_Y_DP) * density
            }

            val targetContainerHeightPx = if (config.isMenuVisible) {
                (widgetHeightPx + bufferPx + menuExpandedHeight).toInt()
            } else {
                widgetHeightPx + bufferPx
            }

            if (it.width != targetContainerWidthPx || it.height != targetContainerHeightPx) {
                it.width = targetContainerWidthPx
                it.height = targetContainerHeightPx
                changed = true
            }

            // POSICIÓN DINÁMICA ESTABLE: Basada en el centro invariante
            val newXPx = (config.centerX * density - it.width / 2).toInt()
            val newYPx = (config.centerY * density - config.height * density / 2 - WIDGET_OFFSET_Y_DP * density).toInt()

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

        val widgetWidthPx = (config.width * density).toInt()
        val bufferPx = (LAYOUT_BUFFER_DP * density).toInt()
        val screenWidthPx = resources.displayMetrics.widthPixels
        val menuPaddingPx = (MENU_PADDING_DP * density).toInt()

        // Anchura estable desde la creación (Full Width Support)
        val totalWidthPx = maxOf(widgetWidthPx + bufferPx, screenWidthPx - menuPaddingPx + bufferPx)
        val totalHeightPx = ((config.height + LAYOUT_BUFFER_DP) * density).toInt()

        val params =
            WindowManager.LayoutParams(
                totalWidthPx,
                totalHeightPx,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT,
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = (config.centerX * density - totalWidthPx / 2).toInt()
                y = (config.centerY * density - config.height * density / 2 - WIDGET_OFFSET_Y_DP * density).toInt()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                }
            }

        composeView =
            ComposeView(this).apply {
                setContent {
                    val currentConfig by floatingWidgetManager.config.collectAsState()
                    FloatingWidgetContent(
                        manager = floatingWidgetManager,
                        config = currentConfig,
                        onToggleMenu = { floatingWidgetManager.toggleMenu() },
                        onSelectCategory = {
                            floatingWidgetManager.selectCategory(it)
                        },
                        onSetAimbotTarget = {
                            floatingWidgetManager.setAimbotTarget(it)
                        },
                    )
                }
            }

        rootContainer =
            FrameLayout(this).apply {
                // Listener para cerrar al tocar fuera
                setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_OUTSIDE) {
                        if (floatingWidgetManager.config.value.isMenuVisible) {
                            floatingWidgetManager.toggleMenu()
                        }
                    }
                    false
                }

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
