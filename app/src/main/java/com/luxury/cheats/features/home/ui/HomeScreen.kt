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
import com.luxury.cheats.features.update.ui.UpdateAnuncioSection
import com.luxury.cheats.navigations.NavRoutes

const val HOME_ROUTE = "home_screen"

private object HomeUIConstants {
    const val NOTIFICATION_DELAY = 3000L
    const val TOAST_TOP_PADDING = 50
}

/**
 * Pantalla principal de la aplicación (Home).
 */
@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel: HomeViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val prefsService = com.luxury.cheats.services.UserPreferencesService(context)
                return HomeViewModel(prefsService) as T
            }
        }
    )
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
        HomeSectionsList(uiState, scrollState, viewModel, navController)

        com.luxury.cheats.core.ui.AppToast(
            notifications = uiState.notifications,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = HomeUIConstants.TOAST_TOP_PADDING.dp)
        )

        uiState.notifications.forEach { notification ->
            androidx.compose.runtime.LaunchedEffect(notification.id) {
                kotlinx.coroutines.delay(HomeUIConstants.NOTIFICATION_DELAY)
                viewModel.onAction(HomeAction.RemoveNotification(notification.id))
            }
        }

        uiState.appUpdate?.let { update ->
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { viewModel.onAction(HomeAction.DismissUpdateAnuncio) }
            ) {
                UpdateAnuncioSection(
                    title = update.title,
                    description = update.description,
                    onUpdateClick = {
                        viewModel.onAction(HomeAction.DismissUpdateAnuncio)
                        navController.navigate(NavRoutes.UPDATE)
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeSectionsList(
    uiState: com.luxury.cheats.features.home.logic.HomeState,
    scrollState: androidx.compose.foundation.ScrollState,
    viewModel: HomeViewModel,
    navController: NavHostController
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
        HomeSaludoSection(uiState.userName, uiState.greeting, uiState.greetingSubtitle)
        
        HomeSeguridadSection(uiState.isSeguridadUnlocked, onClick = { viewModel.onAction(HomeAction.ToggleSeguridad) })

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
            HomeOpcionesSection()
        }

        Spacer(modifier = Modifier.height(130.dp))
    }
}
