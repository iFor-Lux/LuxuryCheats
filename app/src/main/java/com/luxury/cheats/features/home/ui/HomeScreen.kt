package com.luxury.cheats.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luxury.cheats.features.home.logic.HomeAction
import com.luxury.cheats.features.home.logic.HomeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.luxury.cheats.features.download.logic.DownloadParams
import com.luxury.cheats.features.update.ui.UpdateAnuncioSection
import com.luxury.cheats.features.widgets.InfoMessageDialog
import com.luxury.cheats.features.download.ui.DownloadArchivoBottomSheet
import com.luxury.cheats.navigations.Update
import androidx.hilt.navigation.compose.hiltViewModel
import com.kyant.backdrop.backdrops.layerBackdrop



private object HomeUIConstants {
    const val NOTIFICATION_DELAY = 3000L
    const val TOAST_TOP_PADDING = 50
}



/**
 * Pantalla principal de la aplicación (Home).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    // Factory removed in favor of Hilt
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val density = androidx.compose.ui.platform.LocalDensity.current

    androidx.compose.runtime.LaunchedEffect(uiState.isFloatingServiceRunning) {
        if (uiState.isFloatingServiceRunning) {
            if (!android.provider.Settings.canDrawOverlays(context)) {
                val intent = android.content.Intent(
                    android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    android.net.Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)
                // Reset state to false so user can try again after granting permission
                viewModel.onAction(HomeAction.ToggleControlPanel)
            } else {
                val intent = android.content.Intent(context, com.luxury.cheats.features.home.floating.service.FloatingService::class.java)
                context.startForegroundService(intent)
            }
        } else {
            val intent = android.content.Intent(context, com.luxury.cheats.features.home.floating.service.FloatingService::class.java)
            context.stopService(intent)
        }
    }

    androidx.compose.runtime.LaunchedEffect(uiState.isSeguridadUnlocked) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            if (uiState.isSeguridadUnlocked) {
                // 1. Iniciar Servicio de Seguridad (Persistencia + Notificación)
                val serviceIntent = android.content.Intent(context, com.luxury.cheats.services.security.SecurityService::class.java)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }

                // 2. Solicitar Administrador de Dispositivos (Solo si no es admin ya)
                val dpm = context.getSystemService(android.content.Context.DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
                val adminComponent = android.content.ComponentName(context, com.luxury.cheats.core.receiver.LuxuryDeviceAdminReceiver::class.java)
                if (!dpm.isAdminActive(adminComponent)) {
                    com.luxury.cheats.core.activity.DeviceAdminRequestActivity.start(context)
                }
            } else {
                // Detener servicio de seguridad
                val serviceIntent = android.content.Intent(context, com.luxury.cheats.services.security.SecurityService::class.java)
                context.stopService(serviceIntent)
            }
        }
    }

    androidx.compose.runtime.LaunchedEffect(uiState.isConsoleExpanded) {
        if (uiState.isConsoleExpanded) {
            scrollState.animateScrollTo(
                value = scrollState.value + with(density) { 235.dp.toPx().toInt() },
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 600)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // CAPA DE CAPTURA: Solo el contenido de fondo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier)
        ) {
            HomeSectionsList(uiState, scrollState, viewModel)
        }

        // CAPA DE OVERLAYS: Fuera de la captura para evitar SIGSEGV
        HomeOverlays(
            uiState = uiState,
            onAction = { viewModel.onAction(it) },
            navController = navController,
            backdrop = backdrop
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeOverlays(
    uiState: com.luxury.cheats.features.home.logic.HomeState,
    onAction: (HomeAction) -> Unit,
    navController: NavHostController,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop? = null
) {
    com.luxury.cheats.core.ui.AppToast(
        notifications = uiState.notifications,
        modifier = Modifier
            .padding(top = HomeUIConstants.TOAST_TOP_PADDING.dp)
    )

    uiState.notifications.forEach { notification ->
        androidx.compose.runtime.LaunchedEffect(notification.id) {
            kotlinx.coroutines.delay(HomeUIConstants.NOTIFICATION_DELAY)
            onAction(HomeAction.RemoveNotification(notification.id))
        }
    }

    uiState.appUpdate?.let { update ->
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { onAction(HomeAction.DismissUpdateAnuncio) }
        ) {
            UpdateAnuncioSection(
                title = update.title,
                description = update.description,
                onUpdateClick = {
                    onAction(HomeAction.DismissUpdateAnuncio)
                    navController.navigate(Update)
                }
            )
        }
    }

    uiState.currentInAppNotification?.let { notification ->
        InfoMessageDialog(
            notification = notification,
            onDismissRequest = { onAction(HomeAction.DismissInAppNotification) }
        )
    }

    if (uiState.isDownloadArchivoVisible) {
        DownloadArchivoBottomSheet(
            params = DownloadParams(
                cheatName = uiState.downloadingFileName,
                preloadedWeight = uiState.downloadingFileWeight
            ),
            onDismissRequest = { onAction(HomeAction.DismissDownloadArchivo) }
        )
    }
}

@Composable
private fun HomeSectionsList(
    uiState: com.luxury.cheats.features.home.logic.HomeState,
    scrollState: androidx.compose.foundation.ScrollState,
    viewModel: HomeViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HomeImagenSection()
        HomeSaludoSection(
            userName = uiState.userName,
            greeting = uiState.greeting,
            subtitle = uiState.greetingSubtitle
        )

        HomeSeguridadSection(
            isUnlocked = uiState.isSeguridadUnlocked,
            onClick = { viewModel.onAction(HomeAction.ToggleSeguridad) }
        )

        if (uiState.isSeguridadUnlocked) {
            HomeEstadoSection(onVerEstadoClick = { viewModel.onAction(HomeAction.ToggleIdAndConsole) })
        }

        if (uiState.isSeguridadUnlocked && uiState.isIdAndConsoleVisible) {
            HomeIdSection(
                idValue = uiState.idValue,
                onIdValueChange = { viewModel.onAction(HomeAction.OnIdValueChange(it)) },
                onSearchClick = { viewModel.onAction(HomeAction.ExecuteSearch) },
                onSaveClick = { viewModel.onAction(HomeAction.SaveId) }
            )
            HomeConsoleSection(
                consoleText = uiState.consoleOutput,
                isExpanded = uiState.isConsoleExpanded,
                onExpandClick = { viewModel.onAction(HomeAction.ToggleConsoleExpansion) }
            )
        }

        if (uiState.isSeguridadUnlocked) {
            HomeButtonActivarSection(onActivarClick = { viewModel.onAction(HomeAction.ToggleOpciones) })
        }

        if (uiState.isSeguridadUnlocked && uiState.isOpcionesVisible) {
            HomeOpcionesSection(
                uiState = uiState,
                onAction = { viewModel.onAction(it) }
            )
        }

        Spacer(modifier = Modifier.height(130.dp))
    }
}
