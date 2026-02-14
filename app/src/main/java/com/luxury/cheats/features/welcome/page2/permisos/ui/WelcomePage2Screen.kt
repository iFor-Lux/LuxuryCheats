package com.luxury.cheats.features.welcome.page2.permisos.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luxury.cheats.core.ui.welcomeNavBarSection
import com.luxury.cheats.features.welcome.page2.permisos.logic.WelcomePage2Action
import com.luxury.cheats.features.welcome.page2.permisos.logic.WelcomePage2ViewModel

/**
 * Pantalla de permisos (Página 2 del flujo de bienvenida).
 * Muestra una lista de permisos necesarios y su estado actual.
 *
 * @param onNavigateBack Callback para retroceder.
 * @param onNavigateNext Callback para avanzar.
 * @param viewModel ViewModel encargado de la lógica de permisos.
 */
@Composable
fun welcomePage2Screen(
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
    viewModel: WelcomePage2ViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    permissionLifecycleObserver(viewModel)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(100.dp)) // Reducido un poco para dar aire
            welcomePage2TextSection()

            Spacer(modifier = Modifier.height(20.dp))
            welcomePage2NoticeSection()

            Spacer(modifier = Modifier.height(24.dp))
            permissionsList(uiState, viewModel)

            // Espacio extra al final para que el contenido no quede debajo de la Nav Bar en pantallas pequeñas
            Spacer(modifier = Modifier.height(140.dp))
        }

        welcomeNavBarSection(
            currentPage = "2/4",
            onBack = onNavigateBack,
            onNext = onNavigateNext,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp, start = 24.dp, end = 24.dp),
        )
    }
}

@Composable
private fun permissionLifecycleObserver(viewModel: WelcomePage2ViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    viewModel.checkAllPermissions()
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
private fun permissionsList(
    uiState: com.luxury.cheats.features.welcome.page2.permisos.logic.WelcomePage2State,
    viewModel: WelcomePage2ViewModel,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        welcomePage2Permission1Section(
            isGranted = uiState.isStorageGranted,
            isDenied = uiState.isStorageDenied,
            onClick = { viewModel.handleAction(WelcomePage2Action.StorageClicked) },
        )

        Spacer(modifier = Modifier.height(12.dp))
        welcomePage2Permission2Section(
            isGranted = uiState.isNotificationsGranted,
            isDenied = uiState.isNotificationsDenied,
            onClick = { viewModel.handleAction(WelcomePage2Action.NotificationsClicked) },
        )

        Spacer(modifier = Modifier.height(12.dp))
        welcomePage2Permission3Section(
            isGranted = uiState.isOverlayGranted,
            isDenied = uiState.isOverlayDenied,
            onClick = { viewModel.handleAction(WelcomePage2Action.OverlayClicked) },
        )

        Spacer(modifier = Modifier.height(12.dp))
        welcomePage2Permission4Section(
            isGranted = uiState.isAdminGranted,
            isDenied = uiState.isAdminDenied,
            onClick = { viewModel.handleAction(WelcomePage2Action.AdminClicked) },
        )
    }
}
