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

private const val TRANSITION_DURATION = 400

/**
 * Grafo de navegación principal de la aplicación.
 * Organizado en grafos anidados para separar el flujo de autenticación del principal.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    onLogoReady: () -> Unit,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop? = null
) {
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

    Box(modifier = Modifier.fillMaxSize()) {
        if (isAuthScreen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier)
            ) {
                DotPatternBackground()
                WelcomeEclipseSection()
            }
        }

        NavHost(
            navController = navController,
            startDestination = AuthGraph,
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                fadeIn(animationSpec = tween(TRANSITION_DURATION, easing = FastOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(TRANSITION_DURATION, easing = FastOutSlowInEasing))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(TRANSITION_DURATION, easing = FastOutSlowInEasing))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(TRANSITION_DURATION, easing = FastOutSlowInEasing))
            }
        ) {
            navigation<AuthGraph>(startDestination = Splash) {
                welcomeGraph(navController, onLogoReady)
                
                composable<Login> {
                    LoginPantallaScreen(
                        onLoginSuccess = {
                            navController.navigate(MainGraph) {
                                popUpTo<AuthGraph> { inclusive = true }
                            }
                        }
                    )
                }
            }

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
