package com.luxury.cheats.features.welcome.page1.bienvenida.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luxury.cheats.features.welcome.page1.bienvenida.logic.WelcomePage1ViewModel

/**
 * Pantalla Welcome Page 1 (Bienvenida)
 * - Primera pantalla después del splash
 * - Diseño premium y minimalista
 * - MVVM: UI solo renderiza estado, lógica en ViewModel
 * - Usa el mismo WebView del logo (sin animación, más pequeño)
 */
@Composable
fun welcomePage1Screen(
    modifier: Modifier = Modifier,
    viewModel: WelcomePage1ViewModel = viewModel(),
    onNavigateNext: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val logoViewModel: com.luxury.cheats.core.ui.LogoViewModel =
        androidx.hilt.navigation.compose.hiltViewModel(context as androidx.lifecycle.ViewModelStoreOwner)

    // --- CONFIGURACIÓN DE DISEÑO ---
    val logoPresetOffsetY = 40.dp // Ajusta este valor para mover el LOGO REAL (Ej: 0.dp a 100.dp)
    val verticalPadding = 100.dp // Ajusta el padding vertical de la columna de texto/botones
    val horizontalPadding = 0.dp // Ajusta el padding horizontal de la columna
    // -------------------------------

    // Sincronizar el offset con el logo persistente
    androidx.compose.runtime.LaunchedEffect(logoPresetOffsetY) {
        logoViewModel.setLogoOffsetY(logoPresetOffsetY)
    }

    WelcomePage1Content(
        modifier = modifier,
        onNavigateNext = onNavigateNext,
        verticalPadding = verticalPadding,
        horizontalPadding = horizontalPadding,
    )
}

@Composable
private fun WelcomePage1Content(
    modifier: Modifier = Modifier,
    onNavigateNext: () -> Unit = {},
    verticalPadding: androidx.compose.ui.unit.Dp = 0.dp,
    horizontalPadding: androidx.compose.ui.unit.Dp = 0.dp,
) {

    Box(modifier = modifier.fillMaxSize()) {
        welcomePage1LanguageSection(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .zIndex(1f),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // El logo persistente ahora se posiciona solo mediante logoPresetOffsetY
            // Dejamos un espacio base si es necesario, o lo quitamos para control total
            welcomePage1LogoSection()

            // Espacio flexible entre Logo y Texto
            Spacer(modifier = Modifier.weight(0.1f))

            welcomePage1TextSection()

            // Espacio flexible entre Texto y Botón
            Spacer(modifier = Modifier.weight(0.2f))

            welcomePage1ButtonSection(
                onNavigateNext = onNavigateNext,
            )

            // Espacio flexible entre Botón e Insignias
            Spacer(modifier = Modifier.weight(0.4f))

            welcomePage1BadgesSection()

            // Padding final
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomePage1ScreenPreview() {
    MaterialTheme {
        WelcomePage1Content(
            verticalPadding = 100.dp
        )
    }
}
