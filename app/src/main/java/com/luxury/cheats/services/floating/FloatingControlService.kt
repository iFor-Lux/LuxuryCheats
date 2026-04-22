package com.luxury.cheats.services.floating

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import android.view.Gravity
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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FloatingControlService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    @Inject
    lateinit var floatingWidgetManager: FloatingWidgetManager

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

    companion object {
        private const val CHANNEL_ID = "floating_control_channel"
        private const val NOTIFICATION_ID = 2024
    }

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Widget Flotante Activo")
            .setContentText("Luxury Control está funcionando sobre otras apps.")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Canal Widget Flotante",
                NotificationManager.IMPORTANCE_MIN
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        if (rootContainer == null) {
            showFloatingWidget()
            observeWidgetConfig()
        }
        return START_STICKY
    }

    private fun observeWidgetConfig() {
        serviceScope.launch {
            floatingWidgetManager.config.collectLatest { config ->
                updateWidgetUI(config)
            }
        }
    }

    private fun updateWidgetUI(config: FloatingWidgetConfig) {
        val params = rootContainer?.layoutParams as? WindowManager.LayoutParams
        val config = floatingWidgetManager.config.value
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
                Log.d("FloatingWidget", "Ventana actualizada: X=${it.x}, Y=${it.y}")
            }
        }
    }

    private fun showFloatingWidget() {
        val config = floatingWidgetManager.config.value
        val density = resources.displayMetrics.density
        val totalWidthPx = ((config.width + 40) * density).toInt()
        val totalHeightPx = ((config.height + 40) * density).toInt()

        val params = WindowManager.LayoutParams(
            totalWidthPx,
            totalHeightPx,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
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

        rootContainer = FrameLayout(this).apply {
            addView(composeView)
            
            setViewTreeLifecycleOwner(this@FloatingControlService)
            setViewTreeViewModelStoreOwner(this@FloatingControlService)
            setViewTreeSavedStateRegistryOwner(this@FloatingControlService)
        }

        try {
            windowManager.addView(rootContainer, params)
        } catch (e: Exception) {
            Log.e("FloatingControl", "Error al añadir ventana: ${e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        _viewModelStore.clear()
        serviceScope.cancel()
        rootContainer?.let { windowManager.removeView(it) }
        super.onDestroy()
    }
}
