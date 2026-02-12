package com.luxury.cheats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavDestination.Companion.hasRoute
import com.luxury.cheats.navigations.*
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.luxury.cheats.core.ui.LogoViewModel
import com.luxury.cheats.core.ui.LuxuryNavigationBar
import com.luxury.cheats.core.ui.PersistentLogo
import com.luxury.cheats.navigations.AppNavHost
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

/**
 * Componente raíz de la aplicación.
 * Gestiona el controlador de navegación, la barra de navegación persistente y el logo global.
 */
@Composable
fun LuxuryCheatsApp(
    viewModel: LogoViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val isLogoReady by viewModel.isReadyFlow.collectAsState()
    val webView = viewModel.getOrCreateWebView()
    val backdrop = rememberLayerBackdrop()

    // Observar la ruta actual para UI condicional (NavigationBar, Logo)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // CAPA 1: NavHost con captura de backdrop
            Box(modifier = Modifier.fillMaxSize().layerBackdrop(backdrop)) {
                AppNavHost(
                    navController = navController,
                    onLogoReady = { /* No-op: Managed by ViewModel state */ }
                )
            }

            val isHome = navBackStackEntry?.destination?.hasRoute<Home>() == true
            val isPerfil = navBackStackEntry?.destination?.hasRoute<Perfil>() == true

            // CAPA 2: Navigation Bar (Overlay)
            if (isHome || isPerfil) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    LuxuryNavigationBar(
                        activeTab = if (isHome) "Inicio" else "Perfil",
                        backdrop = backdrop,
                        onTabSelected = { tab ->
                            if (tab == "Inicio" && !isHome) {
                                navController.navigate(Home) {
                                    popUpTo<Home> { inclusive = true }
                                    launchSingleTop = true
                                }
                            } else if (tab == "Perfil" && !isPerfil) {
                                navController.navigate(Perfil) {
                                    popUpTo<Home> { inclusive = false }
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            }

            // CAPA 3: Logo Persistente
            PersistentLogo(
                navDestination = navBackStackEntry?.destination,
                isLogoReady = isLogoReady,
                webView = webView
            )
        }
    }
}
