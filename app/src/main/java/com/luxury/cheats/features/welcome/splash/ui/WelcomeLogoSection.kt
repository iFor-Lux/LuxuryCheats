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
import androidx.compose.ui.unit.dp
import com.luxury.cheats.core.ui.LogoWebViewManager

/**
 * Logo principal del Welcome / Splash
 * - Ahora es un placeholder (el WebView vive en MainApp)
 */
@Composable
fun WelcomeLogoSection(
    modifier: Modifier = Modifier,
    onReady: (() -> Unit)? = null
) {
    val isWebViewReady by LogoWebViewManager.isReadyFlow.collectAsState()

    // Notificar que el logo está listo basándonos en el Manager
    LaunchedEffect(isWebViewReady) {
        if (isWebViewReady) {
            onReady?.invoke()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Espacio reservado para el logo persistente
        Box(modifier = Modifier.size(500.dp))
    }
}
