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
import com.luxury.cheats.core.ui.LogoWebViewManager
import com.luxury.cheats.core.ui.LuxuryNavigationBar
import com.luxury.cheats.core.ui.PersistentLogo
import com.luxury.cheats.navigations.AppNavHost
import com.luxury.cheats.navigations.NavRoutes
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

/**
 * Componente raíz de la aplicación.
 * Gestiona el controlador de navegación, la barra de navegación persistente y el logo global.
 */
@Composable
fun LuxuryCheatsApp() {
    val navController = rememberNavController()
    var isLogoReady by remember { mutableStateOf<Boolean>(LogoWebViewManager.isWebViewReady()) }
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
                    onLogoReady = { isLogoReady = true }
                )
            }

            // CAPA 2: Navigation Bar (Overlay)
            if (currentRoute == NavRoutes.HOME || currentRoute == NavRoutes.PERFIL) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    LuxuryNavigationBar(
                        activeTab = if (currentRoute == NavRoutes.HOME) "Inicio" else "Perfil",
                        backdrop = backdrop,
                        onTabSelected = { tab ->
                            val targetRoute = if (tab == "Inicio") NavRoutes.HOME else NavRoutes.PERFIL
                            if (currentRoute != targetRoute) {
                                navController.navigate(targetRoute) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }

            // CAPA 3: Logo Persistente
            PersistentLogo(
                currentRoute = currentRoute,
                isLogoReady = isLogoReady
            )
        }
    }
}
