package com.luxury.cheats.services.floating

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
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
import com.luxury.cheats.R
import com.luxury.cheats.features.home.logic.HomeAction
import com.luxury.cheats.features.home.logic.HomeState
import com.luxury.cheats.features.home.logic.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope

@AndroidEntryPoint
class FloatingControlService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    @Inject
    lateinit var floatingWidgetManager: FloatingWidgetManager

    private lateinit var windowManager: WindowManager
    private var composeView: ComposeView? = null

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

        showFloatingWidget()
        observeWidgetConfig()
    }

    private fun observeWidgetConfig() {
        lifecycleScope.launch {
            floatingWidgetManager.config.collectLatest { config ->
                updateFloatingUI(config)
            }
        }
    }

    private fun updateFloatingUI(config: FloatingWidgetConfig) {
        val params = composeView?.layoutParams as? WindowManager.LayoutParams
        params?.let {
            var changed = false

            val density = resources.displayMetrics.density
            val strokePadding = if (config.isStrokeEnabled) config.strokeWidth else 0f
            
            // Tamaño total incluye el stroke exterior
            val totalWidthPx = ((config.width + (strokePadding * 2)) * density).toInt()
            val totalHeightPx = ((config.height + (strokePadding * 2)) * density).toInt()

            if (it.width != totalWidthPx || it.height != totalHeightPx) {
                it.width = totalWidthPx
                it.height = totalHeightPx
                changed = true
            }

            // Calculamos X e Y (top-left) a partir del centro (config.centerX/Y) en DP convertido a Píxeles
            val centerXpx = config.centerX * density
            val centerYpx = config.centerY * density
            
            val newXPx = (centerXpx - (totalWidthPx / 2f)).toInt()
            val newYPx = (centerYpx - (totalHeightPx / 2f)).toInt()

            if (it.x != newXPx || it.y != newYPx) {
                it.x = newXPx
                it.y = newYPx
                changed = true
            }

            if (changed) {
                windowManager.updateViewLayout(composeView, it)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun showFloatingWidget() {
        val config = floatingWidgetManager.config.value
        val density = resources.displayMetrics.density
        val strokePadding = if (config.isStrokeEnabled) config.strokeWidth else 0f

        val totalWidthPx = ((config.width + (strokePadding * 2)) * density).toInt()
        val totalHeightPx = ((config.height + (strokePadding * 2)) * density).toInt()

        val params = WindowManager.LayoutParams(
            totalWidthPx,
            totalHeightPx,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or 
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            
            val centerXpx = config.centerX * density
            val centerYpx = config.centerY * density
            x = (centerXpx - (totalWidthPx / 2f)).toInt()
            y = (centerYpx - (totalHeightPx / 2f)).toInt()
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        composeView = ComposeView(this).apply {
            setContent {
                val currentConfig by floatingWidgetManager.config.collectAsState()
                
                // Optimizamos: solo pasamos lo necesario para el dibujo. 
                // X e Y los maneja WindowManager, no el Composable interno.
                FloatingWidgetContent(
                    widthDp = currentConfig.width,
                    heightDp = currentConfig.height,
                    strokeWidthDp = currentConfig.strokeWidth,
                    isStrokeEnabled = currentConfig.isStrokeEnabled,
                    strokeColorLong = currentConfig.strokeColor
                )
            }

            setViewTreeLifecycleOwner(this@FloatingControlService)
            setViewTreeViewModelStoreOwner(this@FloatingControlService)
            setViewTreeSavedStateRegistryOwner(this@FloatingControlService)
        }

        windowManager.addView(composeView, params)
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
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        composeView?.let { windowManager.removeView(it) }
        super.onDestroy()
    }
}

/**
 * Contenido visual del widget flotante.
 */
@Composable
fun FloatingWidgetContent(
    widthDp: Int,
    heightDp: Int,
    strokeWidthDp: Float,
    isStrokeEnabled: Boolean,
    strokeColorLong: Long = 0xFFFFFFFF
) {
    val strokeWidth = if (isStrokeEnabled) strokeWidthDp else 0f
    val shape = RoundedCornerShape(24.dp)
    
    // Resolvemos el color: 0 significa "Dinámico" (Primary)
    val strokeColor = if (strokeColorLong == 0L) {
        MaterialTheme.colorScheme.primary
    } else {
        Color(strokeColorLong)
    }

    Box(
        modifier = Modifier
            .size(
                width = (widthDp + (strokeWidth * 2)).dp,
                height = (heightDp + (strokeWidth * 2)).dp
            )
            .then(
                if (isStrokeEnabled) {
                    Modifier.border(
                        width = strokeWidth.dp,
                        color = strokeColor,
                        shape = shape
                    )
                } else Modifier
            )
            .padding(strokeWidth.dp)
            .background(Color.Black, shape),
        contentAlignment = Alignment.Center
    ) {
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun FloatingWidgetPreview() {
    Box(
        modifier = Modifier
            .size(250.dp)
            .background(Color.Gray.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        FloatingWidgetContent(
            widthDp = 200,
            heightDp = 50,
            isStrokeEnabled = true,
            strokeWidthDp = 2f
        )
    }
}
