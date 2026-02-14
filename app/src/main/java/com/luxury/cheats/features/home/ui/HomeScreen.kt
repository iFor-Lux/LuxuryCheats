package com.luxury.cheats.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.luxury.cheats.features.home.ui.seguridad.HomeSeguridadSection
import com.luxury.cheats.features.download.ui.DownloadArchivoBottomSheet
import com.luxury.cheats.navigations.Update
import com.kyant.backdrop.backdrops.layerBackdrop
import androidx.compose.ui.tooling.preview.Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: com.luxury.cheats.features.home.logic.HomeState,
    onAction: (HomeAction) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop? = null
) {
    HomeScreenContent(
        uiState = uiState,
        onAction = onAction,
        navController = navController,
        modifier = modifier,
        backdrop = backdrop
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: com.luxury.cheats.features.home.logic.HomeState,
    onAction: (HomeAction) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scrollState = rememberScrollState()
    val density = androidx.compose.ui.platform.LocalDensity.current



    androidx.compose.runtime.LaunchedEffect(uiState.isConsoleExpanded) {
        if (uiState.isConsoleExpanded && !scrollState.isScrollInProgress) {
            scrollState.animateScrollTo(
                value = scrollState.value + with(density) { 235.dp.toPx().toInt() },
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 600)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier)
        ) {
            HomeSectionsList(uiState, scrollState, onAction)
        }

        HomeOverlays(
            uiState = uiState,
            onAction = onAction,
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
            .padding(top = 50.dp)
    )



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
    onAction: (HomeAction) -> Unit
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

        Spacer(modifier = Modifier.height(10.dp))

        HomeSeguridadSection(
            modifier = Modifier.size(200.dp),
            isActivated = uiState.isSeguridadUnlocked,
            onClick = { onAction(HomeAction.ToggleSeguridad) }
        )

        // Botones y secciones que aparecen al activar seguridad
        if (uiState.isSeguridadUnlocked) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Botón Ver Estado
            HomeEstadoSection(
                onVerEstadoClick = { onAction(HomeAction.ToggleIdAndConsole) }
            )
            
            // Secciones de ID y Consola (aparecen al hacer click en Ver Estado)
            if (uiState.isIdAndConsoleVisible) {
                Spacer(modifier = Modifier.height(20.dp))
                
                HomeIdSection(
                    idValue = uiState.idValue,
                    onIdValueChange = { onAction(HomeAction.OnIdValueChange(it)) },
                    onSearchClick = { onAction(HomeAction.ExecuteSearch) },
                    onSaveClick = { onAction(HomeAction.SaveId) }
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                HomeConsoleSection(
                    consoleText = uiState.consoleOutput,
                    isExpanded = uiState.isConsoleExpanded,
                    onExpandClick = { onAction(HomeAction.ToggleConsoleExpansion) }
                )
            }
            
            Spacer(modifier = Modifier.height(15.dp))
            
            // Botón Activar
            HomeButtonActivarSection(
                onActivarClick = { onAction(HomeAction.ToggleOpciones) }
            )
        }

        // Sección de Opciones (aparece al hacer click en Activar)
        if (uiState.isOpcionesVisible) {
            Spacer(modifier = Modifier.height(20.dp))
            HomeOpcionesSection(
                uiState = uiState,
                onAction = onAction
            )
        }

        Spacer(modifier = Modifier.height(130.dp))
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreenContent(
            uiState = com.luxury.cheats.features.home.logic.HomeState(
                userName = "Lux User",
                greeting = "Buenas Tardes",
                greetingSubtitle = "Listo para ganar"
            ),
            onAction = {},
            navController = androidx.navigation.compose.rememberNavController()
        )
    }
}
