package com.luxury.cheats.features.welcome.splash.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luxury.cheats.features.welcome.splash.logic.WelcomeSplashViewModel

/**
 * Pantalla de bienvenida / Splash
 */
@Composable
fun welcomeSplashScreen(
    modifier: Modifier = Modifier,
    viewModel: WelcomeSplashViewModel = viewModel(),
    onNavigateToPage1: () -> Unit = {},
    onLogoReady: () -> Unit = {},
) {
    // Obtenemos el LogoViewModel para saber cuándo está listo el WebView
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity =
        context as? androidx.lifecycle.ViewModelStoreOwner
            ?: error("Context must be a ViewModelStoreOwner")
    val logoViewModel: com.luxury.cheats.core.ui.LogoViewModel =
        androidx.hilt.navigation.compose.hiltViewModel(
            activity,
        )

    // Secuencia de inicio centralizada
    LaunchedEffect(Unit) {
        viewModel.startSplashSequence(
            isLogoReadyFlow = logoViewModel.isReadyFlow,
            onLogoReady = onLogoReady,
            onReadyToNavigate = onNavigateToPage1,
        )
    }

    // Ya no necesitamos observar estados locales ni efectos secundarios dispersos

    Box(modifier = modifier.fillMaxSize()) {
        // 🎨 Sprays decorativos
        welcomeSpraysSection()

        // 🧩 Logo centrado
        welcomeLogoSection(
            onReady = { /* No-op: Gestionado por LogoViewModel flow en startSplashSequence */ },
        )

        // ✨ Texto "LUXURY" centrado
        welcomeLuxuryTextSection()
    }
}
