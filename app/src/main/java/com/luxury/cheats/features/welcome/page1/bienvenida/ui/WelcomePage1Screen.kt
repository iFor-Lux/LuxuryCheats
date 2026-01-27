package com.luxury.cheats.features.welcome.page1.bienvenida.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luxury.cheats.core.ui.DotPatternBackground
import com.luxury.cheats.core.ui.WelcomeEclipseSection
import com.luxury.cheats.features.welcome.page1.bienvenida.logic.WelcomePage1ViewModel

/**
 * Pantalla Welcome Page 1 (Bienvenida)
 * - Primera pantalla después del splash
 * - Diseño premium y minimalista
 * - MVVM: UI solo renderiza estado, lógica en ViewModel
 * - Usa el mismo WebView del logo (sin animación, más pequeño)
 */
@Composable
fun WelcomePage1Screen(
    modifier: Modifier = Modifier,
    viewModel: WelcomePage1ViewModel = viewModel(),
    onNavigateNext: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        DotPatternBackground()
        WelcomeEclipseSection()
        WelcomePage1LanguageSection(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(1f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp), // Espacio base para el contenido inferior
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            WelcomePage1LogoSection()
            // Empujamos el contenido hacia abajo con un peso flexible
            Spacer(modifier = Modifier.weight(1f))

            WelcomePage1LuxuryText()
            Spacer(modifier = Modifier.height(24.dp))
            WelcomePage1TextSection()

            Spacer(modifier = Modifier.height(35.dp))
            WelcomePage1ButtonSection(
                onNavigateNext = onNavigateNext
            )

            Spacer(modifier = Modifier.height(44.dp))
            WelcomePage1BadgesSection()

            // Padding extra al final para que no pegue con el borde en pantallas pequeñas
            Spacer(modifier = Modifier.height(40.dp))
        }
    }


}
