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
import androidx.navigation.navigation

private const val TRANSITION_DURATION = 400

/**
 * Grafo de navegación principal de la aplicación.
 * Organizado en grafos anidados para separar el flujo de autenticación del principal.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    onLogoReady: () -> Unit
) {
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
            mainGraph(navController)
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
    navController: NavHostController
) {
    composable<Home> {
        HomeScreen(navController = navController)
    }

    composable<Perfil> {
        PerfilScreen()
    }

    composable<Update> {
        DownloadUpdateScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}
