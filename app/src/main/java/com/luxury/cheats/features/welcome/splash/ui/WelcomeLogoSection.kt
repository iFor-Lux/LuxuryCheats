package com.luxury.cheats.features.welcome.splash.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.luxury.cheats.core.ui.LogoViewModel

/**
 * Logo principal del Welcome / Splash
 * - Ahora es un placeholder (el WebView vive en MainApp)
 * - Observa el ViewModel compartido para notificar cuando está listo.
 */
@Composable
fun welcomeLogoSection(
    modifier: Modifier = Modifier,
    onReady: (() -> Unit)? = null,
) {
    // Obtenemos el ViewModel scopped a la Activity para compartir la instancia de MainApp
    val context = LocalContext.current
    val activity =
        context as? ViewModelStoreOwner
            ?: error("Context must be a ViewModelStoreOwner")

    val viewModel: LogoViewModel = hiltViewModel(activity)
    val isWebViewReady by viewModel.isReadyFlow.collectAsState()

    // Notificar que el logo está listo basándonos en el ViewModel
    LaunchedEffect(isWebViewReady) {
        if (isWebViewReady) {
            onReady?.invoke()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        // Espacio reservado para el logo persistente
        Box(modifier = Modifier.size(500.dp))
    }
}
