package com.luxury.cheats.features.perfil.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.luxury.cheats.core.ui.SquarePatternBackground
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kyant.backdrop.backdrops.layerBackdrop
import com.luxury.cheats.core.theme.luxuryCheatsTheme
import com.luxury.cheats.features.perfil.logic.PerfilAction
import com.luxury.cheats.features.perfil.logic.PerfilState
import com.luxury.cheats.features.perfil.logic.PerfilViewModel

/**
 * Pantalla de perfil principal
 * Integra las secciones de información, detalles, créditos y comunidad en un contenedor con scroll.
 *
 * @param modifier Modificador de Compose.
 */
@Composable
fun perfilScreen(
    modifier: Modifier = Modifier,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop? = null,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel =
        viewModel<PerfilViewModel>(
            factory =
                object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        val appContext = context.applicationContext
                        val prefsService = com.luxury.cheats.services.storage.UserPreferencesService(appContext)
                        val authService = com.luxury.cheats.services.firebase.AuthService()
                        val fileService = com.luxury.cheats.services.storage.FileService(appContext)
                        return PerfilViewModel(prefsService, authService, appContext, fileService) as T
                    }
                },
        )
    val uiState by viewModel.uiState.collectAsState()

    // Selector de Imagen de Perfil
    val profilePicker =
        androidx.activity.compose.rememberLauncherForActivityResult(
            contract = androidx.activity.result.contract.ActivityResultContracts.GetContent(),
        ) { uri: android.net.Uri? ->
            uri?.let {
                viewModel.handleAction(PerfilAction.ProfileImageSelected(it.toString()))
            }
        }

    // Selector de Banner
    val bannerPicker =
        androidx.activity.compose.rememberLauncherForActivityResult(
            contract = androidx.activity.result.contract.ActivityResultContracts.GetContent(),
        ) { uri: android.net.Uri? ->
            uri?.let {
                viewModel.handleAction(PerfilAction.BannerImageSelected(it.toString()))
            }
        }

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier =
                Modifier.fillMaxSize().then(
                    if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier,
                ),
        ) {
            SquarePatternBackground()
            perfilContent(
                uiState = uiState,
                modifier = Modifier,
                onAction = { action ->
                    when (action) {
                        PerfilAction.ProfileImageClicked -> profilePicker.launch("image/*")
                        PerfilAction.BannerImageClicked -> bannerPicker.launch("image/*")
                        else -> viewModel.handleAction(action)
                    }
                },
            )
            com.luxury.cheats.core.ui.FadingEdges()
        }
    }
}

@Composable
private fun perfilContent(
    uiState: PerfilState,
    onAction: (com.luxury.cheats.features.perfil.logic.PerfilAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 130.dp),
    ) {
        item { Spacer(modifier = Modifier.statusBarsPadding()) }

        item {
            perfilInfoSection(
                data =
                    ProfileInfoData(
                        userName = uiState.username,
                        userId = uiState.userId,
                        isVip = uiState.isVip,
                        remainingDays = uiState.remainingDays,
                        androidVersion = uiState.androidVersion,
                        appVersion = uiState.appVersion,
                        profileImageUri = uiState.profileImageUri,
                        bannerImageUri = uiState.bannerImageUri,
                    ),
                onAction = onAction,
            )
        }

        item { perfilDetallesSection(state = uiState) }
        item { perfilCreditosSection() }
        item { perfilComunidadSection() }
    }
}

/** Preview de la pantalla de Perfil en tema claro. */
@Preview(name = "Perfil Light")
@Composable
fun perfilScreenPreviewLight() {
    luxuryCheatsTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            perfilScreen()
        }
    }
}

/** Preview de la pantalla de Perfil en tema oscuro. */
@Preview(name = "Perfil Dark")
@Composable
fun perfilScreenPreviewDark() {
    luxuryCheatsTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            perfilScreen()
        }
    }
}
