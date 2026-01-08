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

private const val TRANSITION_DURATION = 400

/**
 * Grafo de navegación principal de la aplicación.
 * Define todas las rutas y transiciones entre pantallas.
 *
 * @param navController El controlador de navegación de Jetpack Compose.
 * @param onLogoReady Callback invocado cuando el logo del Splash está listo.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    onLogoReady: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH,
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
        welcomeGraph(navController, onLogoReady)
        mainGraph(navController)
    }
}

private fun androidx.navigation.NavGraphBuilder.welcomeGraph(
    navController: NavHostController,
    onLogoReady: () -> Unit
) {
    composable(NavRoutes.SPLASH) {
        WelcomeSplashScreen(
            onNavigateToPage1 = {
                navController.navigate(NavRoutes.WELCOME_PAGE1) {
                    popUpTo(NavRoutes.SPLASH) { inclusive = true }
                }
            },
            onLogoReady = onLogoReady
        )
    }

    composable(NavRoutes.WELCOME_PAGE1) {
        WelcomePage1Screen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateNext = { navController.navigate(NavRoutes.WELCOME_PAGE2) }
        )
    }

    composable(NavRoutes.WELCOME_PAGE2) {
        WelcomePage2Screen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateNext = { navController.navigate(NavRoutes.WELCOME_PAGE3) }
        )
    }

    composable(NavRoutes.WELCOME_PAGE3) {
        WelcomePage3Screen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateNext = {
                navController.navigate(NavRoutes.LOGIN)
            }
        )
    }
}

private fun androidx.navigation.NavGraphBuilder.mainGraph(
    navController: NavHostController
) {
    composable(NavRoutes.LOGIN) {
        LoginPantallaScreen(
            onLoginSuccess = {
                navController.navigate(NavRoutes.HOME)
            }
        )
    }

    composable(NavRoutes.HOME) {
        HomeScreen(navController = navController)
    }

    composable(NavRoutes.PERFIL) {
        PerfilScreen()
    }

    composable(NavRoutes.UPDATE) {
        DownloadUpdateScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}
