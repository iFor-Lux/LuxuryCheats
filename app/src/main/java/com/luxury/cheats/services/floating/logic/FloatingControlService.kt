package com.luxury.cheats.services.floating.logic

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
import android.widget.FrameLayout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
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
class FloatingControlService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
    @Inject
    lateinit var floatingWidgetManager: FloatingWidgetManager

    private lateinit var windowManager: WindowManager

    private var rootContainer: FrameLayout? = null
    private var composeView: ComposeView? = null

    private var fovRootContainer: FrameLayout? = null
    private var fovComposeView: ComposeView? = null

    private var dragRootContainer: FrameLayout? = null
    private var dragComposeView: ComposeView? = null

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
        private const val DRAG_CIRCLE_SIZE_DP = 110

        private const val LAYOUT_BUFFER_DP = 40
        private const val MENU_PADDING_DP = 32
        private const val WIDGET_OFFSET_Y_DP = 20
        private const val MENU_BASE_HEIGHT_DP = 64

        private const val HEIGHT_CONFIG_DP = 175
        private const val HEIGHT_AIMBOT_DP = 270
        private const val HEIGHT_GLOO_DP = 260
        private const val HEIGHT_DEFAULT_DP = 240

        private const val EXTRA_FOV_DP = 120
        private const val EXTRA_DRAG_DP = 80
        private const val EXTRA_POINT_DP = 280

        private const val CROSSHAIR_BOX_DIVISOR = 4
        private const val CROSSHAIR_DOT_DIVISOR = 6
        private const val CROSSHAIR_CORNERS_DIVISOR = 3
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
            .setContentTitle("Luxury Control")
            .setContentText("Widget Activo")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Canal Widget", NotificationManager.IMPORTANCE_MIN)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        if (rootContainer == null) {
            // EL ORDEN IMPORTANTE: FOV y Drag debajo, Widget arriba al final
            showFovOverlay()
            showDragClickOverlay()
            showFloatingWidget()
            observeWidgetConfig()
        }
        return START_STICKY
    }

    private fun showFovOverlay() {
        val params =
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                },
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT,
            ).apply {
                gravity = Gravity.CENTER
            }

        fovComposeView =
            ComposeView(this).apply {
                setContent {
                    val config by floatingWidgetManager.config.collectAsState()
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)

                        // 1. DIBUJO DE FOV (SHAPE 1)
                        if (config.isFovEnabled) {
                            drawCircle(
                                color = Color.White.copy(alpha = 0.15f),
                                radius = config.fovRadius.dp.toPx(),
                                center = center,
                            )
                            drawCircle(
                                color = Color.White,
                                radius = config.fovRadius.dp.toPx(),
                                center = center,
                                style = Stroke(width = 2.dp.toPx()),
                            )
                        }

                        // 2. DIBUJO DE CROSSHAIR (SHAPE 3)
                        if (config.isPointScanEnabled) {
                            val color = Color(config.crosshairColor)
                            val size = config.crosshairSize.dp.toPx()
                            val thickness = 2.dp.toPx()

                            when (config.crosshairDesign) {
                                "Classic" -> {
                                    drawLine(
                                        color = color,
                                        start = Offset(center.x, center.y - size / 2),
                                        end = Offset(center.x, center.y + size / 2),
                                        strokeWidth = thickness
                                    )
                                    drawLine(
                                        color = color,
                                        start = Offset(center.x - size / 2, center.y),
                                        end = Offset(center.x + size / 2, center.y),
                                        strokeWidth = thickness
                                    )
                                }
                                "Box" -> {
                                    drawRect(
                                        color = color,
                                        topLeft = Offset(
                                            center.x - size / CROSSHAIR_BOX_DIVISOR,
                                            center.y - size / CROSSHAIR_BOX_DIVISOR
                                        ),
                                        size = androidx.compose.ui.geometry.Size(size / 2, size / 2),
                                        style = Stroke(thickness),
                                    )
                                    drawCircle(color, 2.dp.toPx(), center)
                                }
                                "Dot" -> {
                                    drawCircle(color, size / CROSSHAIR_DOT_DIVISOR, center)
                                }
                                "Corners" -> {
                                    val bSize = size / CROSSHAIR_CORNERS_DIVISOR
                                    // TL
                                    drawLine(
                                        color = color,
                                        start = Offset(center.x - size / 2, center.y - size / 2),
                                        end = Offset(center.x - size / 2 + bSize, center.y - size / 2),
                                        strokeWidth = thickness
                                    )
                                    drawLine(
                                        color = color,
                                        start = Offset(center.x - size / 2, center.y - size / 2),
                                        end = Offset(center.x - size / 2, center.y - size / 2 + bSize),
                                        strokeWidth = thickness
                                    )
                                    // TR
                                    drawLine(
                                        color = color,
                                        start = Offset(center.x + size / 2, center.y - size / 2),
                                        end = Offset(center.x + size / 2 - bSize, center.y - size / 2),
                                        strokeWidth = thickness
                                    )
                                    drawLine(
                                        color = color,
                                        start = Offset(center.x + size / 2, center.y - size / 2),
                                        end = Offset(center.x + size / 2, center.y - size / 2 + bSize),
                                        strokeWidth = thickness
                                    )
                                    // BL
                                    drawLine(
                                        color = color,
                                        start = Offset(center.x - size / 2, center.y + size / 2),
                                        end = Offset(center.x - size / 2 + bSize, center.y + size / 2),
                                        strokeWidth = thickness
                                    )
                                    drawLine(
                                        color = color,
                                        start = Offset(center.x - size / 2, center.y + size / 2),
                                        end = Offset(center.x - size / 2, center.y + size / 2 - bSize),
                                        strokeWidth = thickness
                                    )
                                    // BR
                                    drawLine(
                                        color = color,
                                        start = Offset(center.x + size / 2, center.y + size / 2),
                                        end = Offset(center.x + size / 2 - bSize, center.y + size / 2),
                                        strokeWidth = thickness
                                    )
                                    drawLine(
                                        color = color,
                                        start = Offset(center.x + size / 2, center.y + size / 2),
                                        end = Offset(center.x + size / 2, center.y + size / 2 - bSize),
                                        strokeWidth = thickness
                                    )
                                    // Center dot
                                    drawCircle(color, 2.dp.toPx(), center)
                                }
                            }
                        }
                    }
                }
            }

        fovRootContainer =
            FrameLayout(this).apply {
                addView(fovComposeView)
                setViewTreeLifecycleOwner(this@FloatingControlService)
                setViewTreeViewModelStoreOwner(this@FloatingControlService)
                setViewTreeSavedStateRegistryOwner(this@FloatingControlService)
            }
        windowManager.addView(fovRootContainer, params)
    }

    private fun showDragClickOverlay() {
        val density = resources.displayMetrics.density
        val sizePx = (DRAG_CIRCLE_SIZE_DP * 2 * density).toInt()

        val params =
            WindowManager.LayoutParams(
                sizePx,
                sizePx,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                },
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, // Por defecto no touchable
                PixelFormat.TRANSLUCENT,
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = resources.displayMetrics.widthPixels / 2 - sizePx / 2
                y = resources.displayMetrics.heightPixels / 2 - sizePx / 2
            }

        dragComposeView =
            ComposeView(this).apply {
                setContent {
                    val config by floatingWidgetManager.config.collectAsState()
                    if (config.isDragClickEnabled) {
                        Canvas(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, dragAmount ->
                                            if (!config.isDragClickLocked) {
                                                change.consume()
                                                val p = dragRootContainer?.layoutParams as WindowManager.LayoutParams
                                                p.x += dragAmount.x.toInt()
                                                p.y += dragAmount.y.toInt()
                                                windowManager.updateViewLayout(dragRootContainer, p)
                                                floatingWidgetManager.updateDragClickPosition(
                                                    (p.x + sizePx / 2).toFloat(),
                                                    (p.y + sizePx / 2).toFloat()
                                                )
                                            }
                                        }
                                    },
                        ) {
                            val center = Offset(size.width / 2, size.height / 2)
                            drawCircle(color = Color.Black.copy(alpha = 0.3f), radius = 150f, center = center)
                            drawCircle(color = Color.Black, radius = 150f, center = center, style = Stroke(width = 2f))
                        }
                    }
                }
            }

        dragRootContainer =
            FrameLayout(this).apply {
                addView(dragComposeView)
                setViewTreeLifecycleOwner(this@FloatingControlService)
                setViewTreeViewModelStoreOwner(this@FloatingControlService)
                setViewTreeSavedStateRegistryOwner(this@FloatingControlService)
            }
        windowManager.addView(dragRootContainer, params)
    }

    private fun observeWidgetConfig() {
        serviceScope.launch {
            floatingWidgetManager.config.collectLatest { config ->
                updateWidgetUI(config)
                updateOverlayStates(config)
            }
        }
    }

    private fun updateOverlayStates(config: FloatingWidgetConfig) {
        // Visibilidad FOV / Crosshair
        fovRootContainer?.visibility = if (config.isFovEnabled || config.isPointScanEnabled) {
            android.view.View.VISIBLE
        } else {
            android.view.View.GONE
        }

        // Estado Drag Click (Visibilidad + Táctil)
        dragRootContainer?.let { root ->
            root.visibility = if (config.isDragClickEnabled) android.view.View.VISIBLE else android.view.View.GONE
            val params = root.layoutParams as WindowManager.LayoutParams

            val shouldBeTouchable = config.isDragClickEnabled && !config.isDragClickLocked
            val currentFlags = params.flags
            val newFlags =
                if (shouldBeTouchable) {
                    currentFlags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
                } else {
                    currentFlags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                }

            if (currentFlags != newFlags) {
                params.flags = newFlags
                windowManager.updateViewLayout(root, params)
            }
        }
    }

    private fun updateWidgetUI(config: FloatingWidgetConfig) {
        val params = rootContainer?.layoutParams as? WindowManager.LayoutParams
        params?.let {
            var changed = false
            val density = resources.displayMetrics.density

            // 1. Calcular dimensiones
            val widgetWidthPx = (config.width * density).toInt()
            val bufferPx = (LAYOUT_BUFFER_DP * density).toInt()
            val screenWidthPx = resources.displayMetrics.widthPixels
            val menuPaddingPx = (MENU_PADDING_DP * density).toInt()
            val targetWidthPx = maxOf(widgetWidthPx + bufferPx, screenWidthPx - menuPaddingPx + bufferPx)

            val menuExpandedHeight =
                if (config.selectedCategory.isNotEmpty()) {
                    var baseHeightDp =
                        when (config.selectedCategory) {
                            "Config" -> HEIGHT_CONFIG_DP
                            "Aimbot" -> HEIGHT_AIMBOT_DP
                            "Gloo" -> HEIGHT_GLOO_DP
                            else -> HEIGHT_DEFAULT_DP
                        }
                    if (config.selectedCategory == "Config") {
                        baseHeightDp +=
                            when (config.selectedMicroSection) {
                                "FOV" -> if (config.isFovEnabled) EXTRA_FOV_DP else 0
                                "Drag" -> if (config.isDragClickEnabled) EXTRA_DRAG_DP else 0
                                "Point" -> if (config.isPointScanEnabled) EXTRA_POINT_DP else 0
                                else -> 0
                            }
                    }
                    (baseHeightDp + WIDGET_OFFSET_Y_DP) * density
                } else {
                    (MENU_BASE_HEIGHT_DP + WIDGET_OFFSET_Y_DP) * density
                }

            val targetHeightPx = if (config.isMenuVisible) {
                (config.height * density + bufferPx + menuExpandedHeight).toInt()
            } else {
                (config.height * density + bufferPx).toInt()
            }

            if (it.width != targetWidthPx || it.height != targetHeightPx) {
                it.width = targetWidthPx
                it.height = targetHeightPx
                changed = true
            }

            // 2. RE-CALCULAR POSICIÓN (Crucial para interacción)
            val newXPx = (config.centerX * density - it.width / 2).toInt()
            val newYPx = (config.centerY * density - config.height * density / 2 - WIDGET_OFFSET_Y_DP * density).toInt()

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

    private fun showFloatingWidget() {
        val config = floatingWidgetManager.config.value
        val density = resources.displayMetrics.density
        val totalWidthPx =
            maxOf(
                (config.width * density).toInt() + (LAYOUT_BUFFER_DP * density).toInt(),
                resources.displayMetrics.widthPixels -
                    (MENU_PADDING_DP * density).toInt() + (LAYOUT_BUFFER_DP * density).toInt(),
            )

        val params =
            WindowManager.LayoutParams(
                totalWidthPx,
                ((config.height + LAYOUT_BUFFER_DP) * density).toInt(),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                },
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
                addView(composeView)
                setViewTreeLifecycleOwner(this@FloatingControlService)
                setViewTreeViewModelStoreOwner(this@FloatingControlService)
                setViewTreeSavedStateRegistryOwner(this@FloatingControlService)
            }
        windowManager.addView(rootContainer, params)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        _viewModelStore.clear()
        serviceScope.cancel()
        rootContainer?.let { windowManager.removeView(it) }
        fovRootContainer?.let { windowManager.removeView(it) }
        dragRootContainer?.let { windowManager.removeView(it) }
        super.onDestroy()
    }
}
