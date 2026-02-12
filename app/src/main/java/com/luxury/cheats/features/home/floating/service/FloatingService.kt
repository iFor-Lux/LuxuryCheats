package com.luxury.cheats.features.home.floating.service

import android.app.*
import android.content.*
import android.graphics.PixelFormat
import android.os.*
import android.view.*
import android.graphics.Outline
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.luxury.cheats.features.home.floating.ui.FloatingBubble
import com.luxury.cheats.features.home.floating.ui.HomeControlPanelSection
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

/**
 * Servicio definitivo para el Panel Flotante.
 * FIJADO: Interacción con el fondo y eliminación de blur global molesto.
 */
class FloatingService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val _viewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = _viewModelStore
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private var isExpanded = mutableStateOf(false)
    private val floatingParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        format = PixelFormat.TRANSLUCENT
        // FLAG_NOT_FOCUSABLE: Permite que el teclado y el sistema sigan funcionando fuera.
        // FLAG_LAYOUT_NO_LIMITS: Permite mover la burbuja por toda la pantalla.
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.TOP or Gravity.START
        x = 100
        y = 100
    }

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createFloatingView()
        startForegroundService()
    }

    private fun createFloatingView() {
        composeView = ComposeView(this).apply {
            setContent {
                Box(modifier = Modifier.wrapContentSize()) {
                    if (!isExpanded.value) {
                        FloatingBubble(
                            onDrag = { dx, dy ->
                                floatingParams.x += dx.toInt()
                                floatingParams.y += dy.toInt()
                                windowManager.updateViewLayout(this@apply, floatingParams)
                            },
                            onClick = { 
                                toggleExpansion()
                            }
                        )
                    } else {
                        HomeControlPanelSection(
                            visible = true,
                            backdrop = null,
                            onDismissRequest = { toggleExpansion() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        composeView.setViewTreeLifecycleOwner(this@FloatingService)
        composeView.setViewTreeViewModelStoreOwner(this@FloatingService)
        composeView.setViewTreeSavedStateRegistryOwner(this@FloatingService)

        windowManager.addView(composeView, floatingParams)
    }

    private fun toggleExpansion() {
        val density = resources.displayMetrics.density
        isExpanded.value = !isExpanded.value
        
        if (isExpanded.value) {
            // OBJETIVO 1: Clicks fuera del cuadro.
            // Para que esto funcione, la ventana de Android DEBE ser del tamaño exacto del menú.
            floatingParams.width = (320 * density).toInt()
            floatingParams.height = (420 * density).toInt()
            
            // FLAG_NOT_TOUCH_MODAL: Envía los toques fuera de la ventana al fondo.
            // FLAG_DIM_BEHIND: A veces es necesario para que el sistema aplique efectos de blur detrás.
            floatingParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
                                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                                WindowManager.LayoutParams.FLAG_DIM_BEHIND
                                
            // OBJETIVO 2: Solo difuminar el cuadro. (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                floatingParams.blurBehindRadius = (50 * density).toInt()
                floatingParams.dimAmount = 0.0f // Sin oscurecer, solo queremos el blur.
                
                composeView.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(0, 0, view.width, view.height, 32 * density)
                    }
                }
                composeView.clipToOutline = true
            }
        } else {
            // Volver a estado Burbuja
            floatingParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            floatingParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            floatingParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
                                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                floatingParams.blurBehindRadius = 0
                floatingParams.dimAmount = 0.0f
                composeView.outlineProvider = null
                composeView.clipToOutline = false
            }
        }
        windowManager.updateViewLayout(composeView, floatingParams)
    }

    private fun startForegroundService() {
        val channelId = "floating_service"
        val channel = NotificationChannel(channelId, "Panel Flotante", NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("Luxury Cheats")
            .setContentText("Panel de Control Activo")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        windowManager.removeView(composeView)
    }
}
