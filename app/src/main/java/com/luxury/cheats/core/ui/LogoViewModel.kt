package com.luxury.cheats.core.ui

import android.content.Context
import android.webkit.WebView
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import javax.inject.Inject


/**
 * ViewModel para gestionar el ciclo de vida del WebView del Logo.
 * Reemplaza al anti-patrón Singleton (LogoWebViewManager).
 */
@HiltViewModel
class LogoViewModel
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : ViewModel() {
        // Variable mutable para mantener la instancia del WebView
        // Al estar en un ViewModel, sobrevivirá a cambios de configuración
        private var webView: WebView? = null

        private val isReadyMutable = MutableStateFlow(false)
        val isReadyFlow: StateFlow<Boolean> = isReadyMutable.asStateFlow()

        // [NEW] Control dinámico de offset para personalización por pantalla
        private val _logoOffsetY = MutableStateFlow<Dp>(0.dp)
        val logoOffsetY: StateFlow<Dp> = _logoOffsetY.asStateFlow()

        /**
         * Actualiza el offset vertical del logo para posicionarlo dinámicamente.
         * @param offset El valor de Dp para mover el logo verticalmente.
         */
        fun setLogoOffsetY(offset: Dp) {
            _logoOffsetY.value = offset
        }

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
                    onPageReady = { isReadyMutable.value = true },
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
