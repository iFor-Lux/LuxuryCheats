package com.luxury.cheats.features.perfil.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.luxury.cheats.core.theme.LuxuryCheatsTheme

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luxury.cheats.features.perfil.logic.PerfilViewModel
import com.luxury.cheats.features.perfil.logic.PerfilState
import com.luxury.cheats.features.perfil.logic.PerfilAction


/**
 * Pantalla de perfil principal
 * Integra las secciones de información, detalles, créditos y comunidad en un contenedor con scroll.
 *
 * @param modifier Modificador de Compose.
 */
@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel = viewModel<PerfilViewModel>(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val appContext = context.applicationContext
                val prefsService = com.luxury.cheats.services.UserPreferencesService(appContext)
                val authService = com.luxury.cheats.services.AuthService()
                val fileService = com.luxury.cheats.services.FileService(appContext)
                return PerfilViewModel(prefsService, authService, appContext, fileService) as T
            }
        }
    )
    val uiState by viewModel.uiState.collectAsState()

    // Selector de Imagen de Perfil
    val profilePicker = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            viewModel.handleAction(PerfilAction.ProfileImageSelected(it.toString()))
        }
    }

    // Selector de Banner
    val bannerPicker = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            viewModel.handleAction(PerfilAction.BannerImageSelected(it.toString()))
        }
    }

    val backdrop = rememberLayerBackdrop()
    
    Box(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().layerBackdrop(backdrop)) {
            PerfilContent(
                uiState = uiState,
                modifier = Modifier,
                onAction = { action ->
                    when (action) {
                        PerfilAction.ProfileImageClicked -> profilePicker.launch("image/*")
                        PerfilAction.BannerImageClicked -> bannerPicker.launch("image/*")
                        else -> viewModel.handleAction(action)
                    }
                }
            )
        }
    }
}

@Composable
private fun PerfilContent(
    uiState: PerfilState,
    onAction: (com.luxury.cheats.features.perfil.logic.PerfilAction) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 130.dp)
    ) {
        item { Spacer(modifier = Modifier.statusBarsPadding()) }

        item {
            PerfilInfoSection(
                data = ProfileInfoData(
                    userName = uiState.username,
                    userId = uiState.userId,
                    isVip = uiState.isVip,
                    remainingDays = uiState.remainingDays,
                    androidVersion = uiState.androidVersion,
                    appVersion = uiState.appVersion,
                    profileImageUri = uiState.profileImageUri,
                    bannerImageUri = uiState.bannerImageUri
                ),
                onAction = onAction
            )
        }

        item { PerfilDetallesSection(state = uiState) }
        item { PerfilCreditosSection() }
        item { PerfilComunidadSection() }
    }
}

/** Preview de la pantalla de Perfil en tema claro. */
@Preview(name = "Perfil Light")
@Composable
fun PerfilScreenPreviewLight() {
    LuxuryCheatsTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PerfilScreen()
        }
    }
}

/** Preview de la pantalla de Perfil en tema oscuro. */
@Preview(name = "Perfil Dark")
@Composable
fun PerfilScreenPreviewDark() {
    LuxuryCheatsTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            PerfilScreen()
        }
    }
}
