package com.luxury.cheats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.luxury.cheats.core.ui.LogoViewModel
import com.luxury.cheats.core.ui.LuxuryNavigationBar
import com.luxury.cheats.core.ui.PersistentLogo
import com.luxury.cheats.navigations.Home
import com.luxury.cheats.navigations.Perfil
import com.luxury.cheats.navigations.Tools
import com.luxury.cheats.navigations.AppNavHost

/**
 * Componente raíz de la aplicación.
 * Gestiona el controlador de navegación, la barra de navegación persistente y el logo global.
 */
@Composable
fun LuxuryCheatsApp(viewModel: LogoViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val isLogoReady by viewModel.isReadyFlow.collectAsState()
    val logoOffsetY by viewModel.logoOffsetY.collectAsState()
    val webView = viewModel.getOrCreateWebView()
    val backdrop = rememberLayerBackdrop()

    // Observar la ruta actual para UI condicional (NavigationBar, Logo)
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // CAPA 1: NavHost (Sin captura global para evitar recursión en overlays)
            Box(modifier = Modifier.fillMaxSize()) {
                AppNavHost(
                    navController = navController,
                    onLogoReady = { /* No-op: Managed by ViewModel state */ },
                    backdrop = backdrop,
                )
            }

            NavigationBarOverlay(
                navController = navController,
                navBackStackEntry = navBackStackEntry,
                backdrop = backdrop,
            )

            // CAPA 3: Logo Persistente
            LogoPersistenceLayer(
                navBackStackEntry = navBackStackEntry,
                isLogoReady = isLogoReady,
                webView = webView,
                customLogoOffsetY = logoOffsetY,
            )
        }
    }
}

@Composable
private fun NavigationBarOverlay(
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry?,
    backdrop: LayerBackdrop,
) {
    val isHome = navBackStackEntry?.destination?.hasRoute<Home>() == true
    val isPerfil = navBackStackEntry?.destination?.hasRoute<Perfil>() == true
    val isTools = navBackStackEntry?.destination?.hasRoute<Tools>() == true

    if (isHome || isPerfil || isTools) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            LuxuryNavigationBar(
                activeTab = when {
                    isHome -> "Inicio"
                    isTools -> "Tools"
                    else -> "Perfil"
                },
                backdrop = backdrop,
                onTabSelected = { tab: String ->
                    handleTabSelection(navController, tab, isHome, isPerfil, isTools)
                },
                onFabClick = {
                    navController.navigate(com.luxury.cheats.navigations.ArchivoPlus)
                }
            )
        }
    }
}

private fun handleTabSelection(
    navController: NavHostController,
    tab: String,
    isHome: Boolean,
    isPerfil: Boolean,
    isTools: Boolean,
) {
    if (tab == "Inicio" && !isHome) {
        navController.navigate(Home) {
            popUpTo<Home> {
                saveState = true
                inclusive = false
            }
            launchSingleTop = true
            restoreState = true
        }
    } else if (tab == "Tools" && !isTools) {
        navController.navigate(Tools) {
            popUpTo<Home>() { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    } else if (tab == "Perfil" && !isPerfil) {
        navController.navigate(Perfil) {
            popUpTo<Home> { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}

@Composable
private fun LogoPersistenceLayer(
    navBackStackEntry: NavBackStackEntry?,
    isLogoReady: Boolean,
    webView: android.webkit.WebView?,
    customLogoOffsetY: androidx.compose.ui.unit.Dp,
) {
    webView?.let {
        PersistentLogo(
            navDestination = navBackStackEntry?.destination,
            isLogoReady = isLogoReady,
            webView = it,
            customLogoOffsetY = customLogoOffsetY,
        )
    }
}
