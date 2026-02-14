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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kyant.backdrop.backdrops.layerBackdrop
import com.luxury.cheats.features.download.logic.DownloadParams
import com.luxury.cheats.features.download.ui.downloadArchivoBottomSheet
import com.luxury.cheats.features.home.logic.HomeAction
import com.luxury.cheats.features.home.ui.seguridad.homeSeguridadSection
import com.luxury.cheats.features.update.ui.updateAnuncioSection
import com.luxury.cheats.features.widgets.infoMessageDialog
import com.luxury.cheats.navigations.Update

/**
 * Pantalla principal de la aplicación Luxury Cheats.
 * Orquestador principal que maneja el estado de la UI y las acciones del usuario.
 *
 * @param uiState Estado actual de la pantalla de inicio.
 * @param onAction Callback para procesar acciones del usuario.
 * @param navController Controlador de navegación.
 * @param modifier Modificador de layout.
 * @param backdrop Backdrop opcional para efectos visuales.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun homeScreen(
    uiState: com.luxury.cheats.features.home.logic.HomeState,
    onAction: (HomeAction) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop? = null,
) {
    homeScreenContent(
        uiState = uiState,
        onAction = onAction,
        navController = navController,
        modifier = modifier,
        backdrop = backdrop,
    )
}

/**
 * Contenedor de contenido para la pantalla principal.
 * Separa la estructura visual de la lógica de navegación global.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun homeScreenContent(
    uiState: com.luxury.cheats.features.home.logic.HomeState,
    onAction: (HomeAction) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop? = null,
) {
    val scrollState = rememberScrollState()
    val density = androidx.compose.ui.platform.LocalDensity.current

    androidx.compose.runtime.LaunchedEffect(uiState.isConsoleExpanded) {
        if (uiState.isConsoleExpanded && !scrollState.isScrollInProgress) {
            scrollState.animateScrollTo(
                value = scrollState.value + with(density) { 235.dp.toPx().toInt() },
                animationSpec = androidx.compose.animation.core.tween(durationMillis = 600),
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .then(if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier),
        ) {
            homeSectionsList(uiState, scrollState, onAction)
        }

        homeOverlays(
            uiState = uiState,
            onAction = onAction,
            navController = navController,
            backdrop = backdrop,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun homeOverlays(
    uiState: com.luxury.cheats.features.home.logic.HomeState,
    onAction: (HomeAction) -> Unit,
    navController: NavHostController,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop? = null,
) {
    com.luxury.cheats.core.ui.appToast(
        notifications = uiState.notifications,
        modifier =
            Modifier
                .padding(top = 50.dp),
    )

    uiState.appUpdate?.let { update ->
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { onAction(HomeAction.DismissUpdateAnuncio) },
        ) {
            updateAnuncioSection(
                title = update.title,
                description = update.description,
                onUpdateClick = {
                    onAction(HomeAction.DismissUpdateAnuncio)
                    navController.navigate(Update)
                },
            )
        }
    }

    uiState.currentInAppNotification?.let { notification ->
        infoMessageDialog(
            notification = notification,
            onDismissRequest = { onAction(HomeAction.DismissInAppNotification) },
        )
    }

    if (uiState.isDownloadArchivoVisible) {
        downloadArchivoBottomSheet(
            params =
                DownloadParams(
                    cheatName = uiState.downloadingFileName,
                    preloadedWeight = uiState.downloadingFileWeight,
                ),
            onDismissRequest = { onAction(HomeAction.DismissDownloadArchivo) },
        )
    }
}

@Composable
private fun homeSectionsList(
    uiState: com.luxury.cheats.features.home.logic.HomeState,
    scrollState: androidx.compose.foundation.ScrollState,
    onAction: (HomeAction) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        homeImagenSection()
        homeSaludoSection(
            userName = uiState.userName,
            greeting = uiState.greeting,
            subtitle = uiState.greetingSubtitle,
        )

        Spacer(modifier = Modifier.height(10.dp))

        homeSeguridadSection(
            modifier = Modifier.size(200.dp),
            isActivated = uiState.isSeguridadUnlocked,
            onClick = { onAction(HomeAction.ToggleSeguridad) },
        )

        // Botones y secciones que aparecen al activar seguridad
        if (uiState.isSeguridadUnlocked) {
            homeUnlockedContent(uiState, onAction)
        }

        // Sección de Opciones (aparece al hacer click en Activar)
        if (uiState.isOpcionesVisible) {
            Spacer(modifier = Modifier.height(20.dp))
            homeOpcionesSection(
                uiState = uiState,
                onAction = onAction,
            )
        }

        Spacer(modifier = Modifier.height(130.dp))
    }
}

@Composable
private fun homeUnlockedContent(
    uiState: com.luxury.cheats.features.home.logic.HomeState,
    onAction: (HomeAction) -> Unit,
) {
    if (!uiState.isSeguridadUnlocked) return

    Spacer(modifier = Modifier.height(20.dp))

    // Botón Ver Estado
    homeEstadoSection(
        onVerEstadoClick = { onAction(HomeAction.ToggleIdAndConsole) },
    )

    // Secciones de ID y Consola (aparecen al hacer click en Ver Estado)
    if (uiState.isIdAndConsoleVisible) {
        Spacer(modifier = Modifier.height(20.dp))

        homeIdSection(
            idValue = uiState.idValue,
            onIdValueChange = { onAction(HomeAction.OnIdValueChange(it)) },
            onSearchClick = { onAction(HomeAction.ExecuteSearch) },
            onSaveClick = { onAction(HomeAction.SaveId) },
        )

        Spacer(modifier = Modifier.height(20.dp))

        homeConsoleSection(
            consoleText = uiState.consoleOutput,
            isExpanded = uiState.isConsoleExpanded,
            onExpandClick = { onAction(HomeAction.ToggleConsoleExpansion) },
        )
    }

    Spacer(modifier = Modifier.height(15.dp))

    // Botón Activar
    homeButtonActivarSection(
        onActivarClick = { onAction(HomeAction.ToggleOpciones) },
    )
}

/**
 * Previsualización de la pantalla de inicio para el IDE.
 */
@Preview(showSystemUi = true)
@Composable
fun homeScreenPreview() {
    MaterialTheme {
        homeScreenContent(
            uiState =
                com.luxury.cheats.features.home.logic.HomeState(
                    userName = "Lux User",
                    greeting = "Buenas Tardes",
                    greetingSubtitle = "Listo para ganar",
                ),
            onAction = {},
            navController = androidx.navigation.compose.rememberNavController(),
        )
    }
}
