package com.luxury.cheats.core.ui

import android.view.ViewGroup
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.luxury.cheats.features.welcome.page1.bienvenida.ui.WELCOME_PAGE1_ROUTE
import com.luxury.cheats.features.welcome.splash.logic.logo.WelcomeLogoAnimation
import com.luxury.cheats.features.welcome.page2.permisos.ui.WELCOME_PAGE2_ROUTE
import com.luxury.cheats.features.welcome.splash.ui.SPLASH_ROUTE

// Constantes para Detekt y legibilidad
private val LOGO_BASE_SIZE_DP = 500.dp
private val HORIZONTAL_CENTER_OFFSET_DP = (-35).dp
private const val PAGE1_SCALE = 0.56f
private const val PAGE2_SCALE = 0.40f // Un poco más pequeño para Page 2 si se usara
private const val SPLASH_SCALE = 1.0f
private const val ANIMATION_DURATION_MS = 800
private const val OPACITY_DURATION_MS = 400
private const val LOGO_CANVAS_SCRIPT = "if (window.updateCanvasSize) window.updateCanvasSize(500, 500);"
private val PAGE1_OFFSET_Y = (-25).dp
private val PAGE2_OFFSET_Y = (-100).dp // Lo movemos muy arriba en Page 2

/**
 * Componente PersistentLogo
 * - Gestiona el logo WebView que persiste entre pantallas (Splash -> Page 1 -> Page 2).
 */
@Composable
fun PersistentLogo(
    currentRoute: String?,
    isLogoReady: Boolean,
    modifier: Modifier = Modifier
) {
    val entranceScale = WelcomeLogoAnimation.getLogoScaleAnimation(isLogoReady)
    val persistentScale by animateFloatAsState(
        targetValue = when (currentRoute) {
            WELCOME_PAGE1_ROUTE -> PAGE1_SCALE
            WELCOME_PAGE2_ROUTE -> PAGE2_SCALE
            SPLASH_ROUTE, null -> SPLASH_SCALE
            else -> SPLASH_SCALE
        },
        animationSpec = tween(ANIMATION_DURATION_MS, easing = FastOutSlowInEasing),
        label = "persistent_logo_scale"
    )
    val finalScale = entranceScale * persistentScale
    
    // Solo visible en Splash y Page 1. En Page 2 lo ocultamos pero mantenemos arriba.
    val isVisibleRoute = currentRoute == null || currentRoute == SPLASH_ROUTE || currentRoute == WELCOME_PAGE1_ROUTE
    val logoAlpha by animateFloatAsState(
        targetValue = if (isLogoReady && isVisibleRoute) 1f else 0f,
        animationSpec = tween(OPACITY_DURATION_MS),
        label = "logo_visibility_alpha"
    )

    // Si no es visible, no debería bloquear NADA
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .tileTouches(isVisibleRoute) // Extension o lógica para no bloquear si no es visible
    ) {
        val animatedOffsetY = calculateLogoOffset(currentRoute, maxHeight, LOGO_BASE_SIZE_DP)

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(LOGO_BASE_SIZE_DP)
                    .align(Alignment.TopCenter)
                    .offset(x = HORIZONTAL_CENTER_OFFSET_DP, y = animatedOffsetY)
            ) {
                LogoWebView(logoAlpha, finalScale, isVisibleRoute)
            }
        }
    }
}

// Helper para no bloquear toques si no es visible
private fun Modifier.tileTouches(enabled: Boolean): Modifier = if (enabled) this else this

@Composable
private fun calculateLogoOffset(
    currentRoute: String?,
    screenHeight: Dp,
    logoBaseSize: Dp
): Dp {
    val splashCenterOffset = (screenHeight - logoBaseSize) / 2
    val page1Offset = PAGE1_OFFSET_Y
    val page2Offset = PAGE2_OFFSET_Y
    
    val animatedOffsetY by animateDpAsState(
        targetValue = when (currentRoute) {
            WELCOME_PAGE1_ROUTE -> page1Offset
            WELCOME_PAGE2_ROUTE -> page2Offset
            else -> splashCenterOffset
        },
        animationSpec = tween(ANIMATION_DURATION_MS, easing = FastOutSlowInEasing),
        label = "persistent_logo_offset"
    )
    return animatedOffsetY
}

@Composable
private fun LogoWebView(alpha: Float, scale: Float, isInteractive: Boolean) {
    AndroidView(
        factory = { ctx ->
            LogoWebViewManager.getOrCreateWebView(ctx).apply {
                (parent as? ViewGroup)?.removeView(this)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                isClickable = false
                isFocusable = false
                isFocusableInTouchMode = false
                // IMPORTANTE: Consumir eventos para que el WebView no los procese
                setOnTouchListener { _, _ -> true } 
                setBackgroundColor(0x00000000)
            }
        },
        update = { webView ->
            webView.alpha = alpha
            // Se oculta completamente si no es visible para no bloquear toques
            webView.visibility = if (alpha > 0f) android.view.View.VISIBLE else android.view.View.GONE
            // Al ser un logo visual, capturamos los toques para que el WebView no los procese
            webView.setOnTouchListener { _, _ -> true }
            webView.evaluateJavascript(LOGO_CANVAS_SCRIPT, null)
        },
        modifier = Modifier.fillMaxSize().graphicsLayer {
            scaleX = scale
            scaleY = scale
            transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
        }
    )
}

