package com.luxury.cheats.features.welcome.page3.shizuku.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.luxury.cheats.core.ui.DotPatternBackground
import com.luxury.cheats.core.ui.WelcomeEclipseSection
import com.luxury.cheats.core.ui.WelcomeNavBarSection

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luxury.cheats.features.welcome.page3.shizuku.logic.WelcomePage3ViewModel

/**
 * Pantalla informativa sobre Shizuku (Página 3 del flujo de bienvenida).
 *
 * @param onNavigateBack Callback para retroceder.
 * @param onNavigateNext Callback para avanzar.
 * @param viewModel ViewModel encargado de la lógica de Shizuku.
 */
@Composable
fun WelcomePage3Screen(
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
    viewModel: WelcomePage3ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkShizukuStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            WelcomePage3TextSection()

            Spacer(modifier = Modifier.height(24.dp))
            WelcomePage3Imagen()

            Spacer(modifier = Modifier.height(30.dp)) // Reducido para mejor ajuste
            WelcomePage3Mensaje()
            
            // Espacio extra al final para que el contenido no quede debajo de la Nav Bar
            Spacer(modifier = Modifier.height(140.dp))
        }

        WelcomeNavBarSection(
            currentPage = "3/4",
            onBack = onNavigateBack,
            onNext = onNavigateNext,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp, start = 24.dp, end = 24.dp)
        )
    }
}
