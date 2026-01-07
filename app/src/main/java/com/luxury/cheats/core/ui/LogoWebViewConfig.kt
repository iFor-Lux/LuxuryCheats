package com.luxury.cheats.core.ui

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Configuración optimizada del WebView para logo HTML/WebGL
 * - Hardware acceleration forzado
 * - WebGL2 con fallback a WebGL
 * - Optimizado para FPS estables
 * - Separación de lógica de UI (cumple AGENTS.md)
 * - Ubicado en core porque es compartido entre pantallas
 */
object LogoWebViewConfig {
    
    private const val TAG = "LogoWebView"
    
    /**
     * Configura el WebView con todas las optimizaciones necesarias
     */
    fun configureWebView(
        webView: WebView,
        onPageReady: (() -> Unit)? = null
    ) {
        // Forzar hardware acceleration (crítico para WebGL)
        webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null)
        
        // Configurar WebViewClient (sin interceptación)
        webView.webViewClient = createWebViewClient()
        
        // Configurar WebChromeClient para logs y debugging
        webView.webChromeClient = createWebChromeClient()
        
        // Agregar JavaScript Interface para comunicación JS -> Android
        webView.addJavascriptInterface(
            WebGLReadyInterface(onPageReady),
            "AndroidInterface"
        )
        
        // Aplicar configuración de settings optimizada
        configureSettings(webView.settings)
        
        // Configurar propiedades visuales
        configureVisualProperties(webView)
        
        // Cargar contenido
        webView.loadUrl("file:///android_asset/index.html")
    }
    
    /**
     * JavaScript Interface para comunicación JS -> Android
     * Permite que JavaScript notifique cuando WebGL está listo
     */
    @SuppressLint("JavascriptInterface")
    private class WebGLReadyInterface(
        private val onPageReady: (() -> Unit)?
    ) {
        @JavascriptInterface
        fun onWebGLReady() {
            Log.d(TAG, "WebGL ready notification from JavaScript")
            onPageReady?.invoke()
        }
    }
    
    private fun createWebViewClient(): WebViewClient {
        return object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "Page finished: $url")
            }
        }
    }
    
    private fun createWebChromeClient(): WebChromeClient {
        return object : WebChromeClient() {
            override fun onConsoleMessage(
                consoleMessage: android.webkit.ConsoleMessage?
            ): Boolean {
                val message = consoleMessage?.message() ?: ""
                val level = consoleMessage?.messageLevel()
                when (level) {
                    android.webkit.ConsoleMessage.MessageLevel.ERROR -> 
                        Log.e(TAG, "JS Error: $message")
                    android.webkit.ConsoleMessage.MessageLevel.WARNING -> 
                        Log.w(TAG, "JS Warning: $message")
                    else -> 
                        Log.d(TAG, "JS Console: $message")
                }
                return true
            }
        }
    }
    
    private fun configureSettings(settings: android.webkit.WebSettings) {
        // JavaScript y DOM (requerido para WebGL)
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        
        // Acceso a archivos locales (para assets)
        settings.allowFileAccess = true
        settings.allowContentAccess = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true
        
        // Configuración de Viewport para DP exactos
        settings.useWideViewPort = false
        settings.loadWithOverviewMode = false
        
        // Optimizaciones de rendimiento
        settings.mediaPlaybackRequiresUserGesture = false
        settings.loadsImagesAutomatically = true
        settings.blockNetworkLoads = false
        settings.blockNetworkImage = false
        
        // Deshabilitar zoom (no necesario para logo)
        settings.javaScriptCanOpenWindowsAutomatically = false
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
        
        // Mixed content (para desarrollo)
        settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        
        // Cache optimizado para assets locales
        settings.cacheMode = android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK
        
        // Renderizado acelerado (importante para WebGL)
        settings.setRenderPriority(android.webkit.WebSettings.RenderPriority.HIGH)
    }
    
    private fun configureVisualProperties(webView: WebView) {
        // Fondo transparente
        webView.setBackgroundColor(0x00000000)
        
        // Inicialmente invisible (se mostrará cuando esté listo)
        webView.alpha = 0f
        webView.visibility = android.view.View.VISIBLE
        
        // No interceptar toques (solo visual)
        webView.isClickable = false
        webView.isFocusable = false
        webView.isFocusableInTouchMode = false
        
        // Bloquear cualquier intento de interacción consumiendo los eventos
        webView.setOnTouchListener { _, _ -> true }
        
        // Deshabilitar barras de desplazamiento (horizontal y vertical)
        webView.isHorizontalScrollBarEnabled = false
        webView.isVerticalScrollBarEnabled = false
        webView.overScrollMode = android.view.View.OVER_SCROLL_NEVER
        
        // Optimizaciones de rendimiento adicionales
        webView.setWillNotDraw(false) // Permitir que se dibuje
    }
    
    /**
     * Muestra el WebView cuando está listo (para animación)
     */
    fun showWebView(webView: WebView) {
        webView.alpha = 1f
    }
}

