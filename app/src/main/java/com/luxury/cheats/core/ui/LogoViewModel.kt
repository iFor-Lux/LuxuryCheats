package com.luxury.cheats.core.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel para gestionar el ciclo de vida del WebView del Logo.
 * Reemplaza al anti-patrón Singleton (LogoWebViewManager).
 */
@HiltViewModel
class LogoViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Variable mutable para mantener la instancia del WebView
    // Al estar en un ViewModel, sobrevivirá a cambios de configuración
    private var webView: WebView? = null

    private val _isReady = MutableStateFlow(false)
    val isReadyFlow: StateFlow<Boolean> = _isReady.asStateFlow()

    /**
     * Obtiene el WebView existente o crea uno nuevo si es necesario.
     */
    fun getOrCreateWebView(): WebView {
        if (webView == null) {
            webView = createWebView()
        }
        return webView!!
    }

    private fun createWebView(): WebView {
        // Usamos el Application Context para evitar leaks de Activity
        return WebView(context).apply {
            LogoWebViewConfig.configureWebView(
                webView = this,
                onPageReady = { _isReady.value = true }
            )
        }
    }

    /**
     * Llamado cuando el ViewModel se va a destruir (Activity finalizada).
     * Aquí realizamos la limpieza crítica de recursos.
     */
    override fun onCleared() {
        super.onCleared()
        webView?.let {
            // Detener carga, destruir historial y limpiar vista
            it.stopLoading()
            it.destroy()
        }
        webView = null
    }
}
