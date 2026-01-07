package com.luxury.cheats.features.welcome.splash.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luxury.cheats.core.ui.DotPatternBackground
import com.luxury.cheats.core.ui.WelcomeEclipseSection
import com.luxury.cheats.features.welcome.splash.logic.WelcomeSplashViewModel
import kotlinx.coroutines.delay

private const val SPLASH_AUTO_NAV_DELAY = 1500L

/**
 * Pantalla de bienvenida / Splash
 */
@Composable
fun WelcomeSplashScreen(
    modifier: Modifier = Modifier,
    viewModel: WelcomeSplashViewModel = viewModel(),
    onNavigateToPage1: () -> Unit = {},
    onLogoReady: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var isLogoReadyInternal by remember { mutableStateOf(false) }

    // Detectar cuando el logo está listo y las animaciones han terminado
    LaunchedEffect(isLogoReadyInternal) {
        if (isLogoReadyInternal) {
            // 1. Esperamos un poco después de que los sprays inicien
            delay(1)

            onLogoReady()
            // 2. Dar tiempo para que el usuario aprecie el logo y el texto LUXURY
            delay(SPLASH_AUTO_NAV_DELAY)
            viewModel.markAsReady()
        }
    }


    // Observar estado y navegar cuando esté listo
    LaunchedEffect(state.isReadyToNavigate) {
        if (state.isReadyToNavigate) {
            onNavigateToPage1()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 🌌 Background animado
        DotPatternBackground()

        // 🎨 Sprays decorativos
        WelcomeSpraysSection()

        // 🌑 Eclipse decorativo
        WelcomeEclipseSection()

        // 🧩 Logo centrado (notifica cuando está listo)
        WelcomeLogoSection(
            onReady = { isLogoReadyInternal = true }
        )

        // ✨ Texto "LUXURY" centrado
        WelcomeLuxuryTextSection()
    }
}
