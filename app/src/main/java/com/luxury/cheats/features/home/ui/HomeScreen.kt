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
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    // Factory removed in favor of Hilt
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()
    val density = androidx.compose.ui.platform.LocalDensity.current

    androidx.compose.runtime.LaunchedEffect(uiState.isConsoleExpanded) {
        if (uiState.isConsoleExpanded) {
            scrollState.animateScrollTo(
                value = scrollState.value + with(density) { 235.dp.toPx().toInt() },
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 600)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        HomeSectionsList(uiState, scrollState, viewModel)

        HomeOverlays(
            uiState = uiState,
            onAction = { viewModel.onAction(it) },
            navController = navController
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeOverlays(
    uiState: com.luxury.cheats.features.home.logic.HomeState,
    onAction: (HomeAction) -> Unit,
    navController: NavHostController
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
