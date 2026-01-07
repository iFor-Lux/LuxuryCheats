package com.luxury.cheats.core.ui

import android.content.Context
import android.webkit.WebView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manager singleton para reutilizar el WebView del logo
 */
object LogoWebViewManager {
    private var sharedWebView: WebView? = null
    private val _isReady = MutableStateFlow(false)
    val isReadyFlow: StateFlow<Boolean> = _isReady.asStateFlow()

    /**
     * Obtiene el WebView compartido o lo crea si no existe.
     *
     * @param context El contexto para crear el WebView.
     * @return La instancia compartida de WebView.
     */
    fun getOrCreateWebView(context: Context): WebView {
        if (sharedWebView == null) {
            sharedWebView = WebView(context.applicationContext).also { webView ->
                LogoWebViewConfig.configureWebView(
                    webView = webView,
                    onPageReady = { _isReady.value = true }
                )
            }
        }
        return sharedWebView!!
    }

    /**
     * Comprueba si el WebView ha terminado de cargar el logo.
     *
     * @return true si está listo, false en caso contrario.
     */
    fun isWebViewReady(): Boolean = _isReady.value
}
