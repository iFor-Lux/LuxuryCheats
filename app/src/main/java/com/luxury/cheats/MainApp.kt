package com.luxury.cheats

import android.webkit.WebView
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
import com.luxury.cheats.core.ui.luxuryNavigationBar
import com.luxury.cheats.core.ui.persistentLogo
import com.luxury.cheats.navigations.Home
import com.luxury.cheats.navigations.Perfil
import com.luxury.cheats.navigations.appNavHost

/**
 * Componente raíz de la aplicación.
 * Gestiona el controlador de navegación, la barra de navegación persistente y el logo global.
 */
@Composable
fun luxuryCheatsApp(viewModel: LogoViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val isLogoReady by viewModel.isReadyFlow.collectAsState()
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
                appNavHost(
                    navController = navController,
                    onLogoReady = { /* No-op: Managed by ViewModel state */ },
                    backdrop = backdrop,
                )
            }

            navigationBarOverlay(
                navController = navController,
                navBackStackEntry = navBackStackEntry,
                backdrop = backdrop,
            )

            // CAPA 3: Logo Persistente
            logoPersistenceLayer(
                navBackStackEntry = navBackStackEntry,
                isLogoReady = isLogoReady,
                webView = webView,
            )
        }
    }
}

@Composable
private fun navigationBarOverlay(
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry?,
    backdrop: LayerBackdrop,
) {
    val isHome = navBackStackEntry?.destination?.hasRoute<Home>() == true
    val isPerfil = navBackStackEntry?.destination?.hasRoute<Perfil>() == true

    if (isHome || isPerfil) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 32.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            luxuryNavigationBar(
                activeTab = if (isHome) "Inicio" else "Perfil",
                backdrop = backdrop,
                onTabSelected = { tab: String ->
                    handleTabSelection(navController, tab, isHome, isPerfil)
                },
            )
        }
    }
}

private fun handleTabSelection(
    navController: NavHostController,
    tab: String,
    isHome: Boolean,
    isPerfil: Boolean,
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
    } else if (tab == "Perfil" && !isPerfil) {
        navController.navigate(Perfil) {
            popUpTo<Home> { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}

@Composable
private fun logoPersistenceLayer(
    navBackStackEntry: NavBackStackEntry?,
    isLogoReady: Boolean,
    webView: WebView?,
) {
    webView?.let {
        persistentLogo(
            navDestination = navBackStackEntry?.destination,
            isLogoReady = isLogoReady,
            webView = it,
        )
    }
}
