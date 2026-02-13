package com.luxury.cheats.navigations

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.luxury.cheats.features.home.ui.HomeScreen
import com.luxury.cheats.features.login.pantalla.ui.LoginPantallaScreen
import com.luxury.cheats.features.perfil.ui.PerfilScreen
import com.luxury.cheats.features.welcome.page1.bienvenida.ui.WelcomePage1Screen
import com.luxury.cheats.features.welcome.page2.permisos.ui.WelcomePage2Screen
import com.luxury.cheats.features.welcome.page3.shizuku.ui.WelcomePage3Screen
import com.luxury.cheats.features.welcome.splash.ui.WelcomeSplashScreen
import com.luxury.cheats.features.update.ui.DownloadUpdateScreen
import com.luxury.cheats.core.ui.DotPatternBackground
import com.luxury.cheats.core.ui.WelcomeEclipseSection
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.kyant.backdrop.backdrops.layerBackdrop
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.backdrops.LayerBackdrop

private const val TRANSITION_DURATION = 400

/**
 * Grafo de navegación principal de la aplicación.
 * Organizado en grafos anidados para separar el flujo de autenticación del principal.
 */
// ... imports removed ...

@Composable
fun AppNavHost(
    navController: NavHostController,
    onLogoReady: () -> Unit,
    backdrop: LayerBackdrop
) {
    
    // Estado para controlar la opacidad del fondo global desde las pantallas
    var globalBackgroundAlpha by remember { mutableFloatStateOf(1f) }
    val animatedGlobalAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = globalBackgroundAlpha,
        animationSpec = tween(500),
        label = "globalBackgroundAlpha"
    )

    // Observar la ruta actual para UI condicional (NavigationBar, Logo)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Lista de rutas que deben mostrar el fondo de Bienvenida (DotPattern + Eclipse)
    val authScreenRoutes = listOf(
        Splash::class.qualifiedName,
        WelcomePage1::class.qualifiedName,
        WelcomePage2::class.qualifiedName,
        WelcomePage3::class.qualifiedName,
        Login::class.qualifiedName
    )
    
    val isAuthScreen = currentDestination?.route?.let { route ->
        authScreenRoutes.any { route.contains(it ?: "") }
    } ?: true // Default to true for start destination

    // Reset alpha when navigating away from Login or to other screens if needed
    androidx.compose.runtime.LaunchedEffect(currentDestination) {
        // Fallback robusto para detectar si NO estamos en Login
        val loginRoute = Login::class.qualifiedName ?: "Login"
        val currentRoute = currentDestination?.route ?: ""
        if (!currentRoute.contains(loginRoute)) {
             globalBackgroundAlpha = 1f
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isAuthScreen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .layerBackdrop(backdrop)
                    .alpha(animatedGlobalAlpha)
            ) {
                DotPatternBackground()
                WelcomeEclipseSection()
            }
        }
// ...

        NavHost(
            navController = navController,
            startDestination = AuthGraph,
            modifier = Modifier.fillMaxSize(),
// ... transitions ...
        ) {
            navigation<AuthGraph>(startDestination = Splash) {
                welcomeGraph(navController, onLogoReady)
                
                composable<Login> {
                    LoginPantallaScreen(
                        onLoginSuccess = {
                            navController.navigate(MainGraph) {
                                popUpTo<AuthGraph> { inclusive = true }
                            }
                        },
                        onUpdateBackgroundVisibility = { isVisible ->
                            globalBackgroundAlpha = if (isVisible) 1f else 0f
                        }
                    )
                }
            }
// ...

            navigation<MainGraph>(startDestination = Home) {
                mainGraph(navController, backdrop)
            }
        }
    }
}

private fun androidx.navigation.NavGraphBuilder.welcomeGraph(
    navController: NavHostController,
    onLogoReady: () -> Unit
) {
    composable<Splash> {
        WelcomeSplashScreen(
            onNavigateToPage1 = {
                navController.navigate(WelcomePage1) {
                    popUpTo<Splash> { inclusive = true }
                }
            },
            onLogoReady = onLogoReady
        )
    }

    composable<WelcomePage1> {
        WelcomePage1Screen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateNext = { navController.navigate(WelcomePage2) }
        )
    }

    composable<WelcomePage2> {
        WelcomePage2Screen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateNext = { navController.navigate(WelcomePage3) }
        )
    }

    composable<WelcomePage3> {
        WelcomePage3Screen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateNext = {
                navController.navigate(Login)
            }
        )
    }
}

private fun androidx.navigation.NavGraphBuilder.mainGraph(
    navController: NavHostController,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop? = null
) {
    composable<Home> {
        HomeScreen(navController = navController, backdrop = backdrop)
    }

    composable<Perfil> {
        PerfilScreen(backdrop = backdrop)
    }

    composable<Update> {
        DownloadUpdateScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}
