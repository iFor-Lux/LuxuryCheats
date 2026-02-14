package com.luxury.cheats.navigations

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.luxury.cheats.core.ui.dotPatternBackground
import com.luxury.cheats.core.ui.welcomeEclipseSection
import com.luxury.cheats.features.home.logic.HomeAction
import com.luxury.cheats.features.home.logic.HomeViewModel
import com.luxury.cheats.features.home.ui.homeScreen
import com.luxury.cheats.features.login.pantalla.ui.loginPantallaScreen
import com.luxury.cheats.features.perfil.ui.perfilScreen
import com.luxury.cheats.features.update.ui.downloadUpdateScreen
import com.luxury.cheats.features.welcome.page1.bienvenida.ui.welcomePage1Screen
import com.luxury.cheats.features.welcome.page2.permisos.ui.welcomePage2Screen
import com.luxury.cheats.features.welcome.page3.shizuku.ui.welcomePage3Screen
import com.luxury.cheats.features.welcome.splash.ui.welcomeSplashScreen

private const val BACKGROUND_ANIM_DURATION = 500

/**
 * Grafo de navegación principal de la aplicación.
 * Organizado en grafos anidados para separar el flujo de autenticación del principal.
 */

@Composable
fun appNavHost(
    navController: NavHostController,
    onLogoReady: () -> Unit,
    backdrop: LayerBackdrop,
) {
    // Estado para controlar la opacidad del fondo global desde las pantallas
    var globalBackgroundAlpha by remember { mutableFloatStateOf(1f) }
    val animatedGlobalAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = globalBackgroundAlpha,
        animationSpec = tween(BACKGROUND_ANIM_DURATION),
        label = "globalBackgroundAlpha",
    )

    // Observar la ruta actual para UI condicional (NavigationBar, Logo)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Lista de rutas que deben mostrar el fondo de Bienvenida (DotPattern + Eclipse)
    val authScreenRoutes =
        listOf(
            Splash::class.qualifiedName,
            WelcomePage1::class.qualifiedName,
            WelcomePage2::class.qualifiedName,
            WelcomePage3::class.qualifiedName,
            Login::class.qualifiedName,
        )

    val isAuthScreen =
        currentDestination?.route?.let { route ->
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
        authBackgroundLayer(
            isAuthScreen = isAuthScreen,
            backdrop = backdrop,
            animatedGlobalAlpha = animatedGlobalAlpha,
        )

        appNavGraph(
            navController = navController,
            onLogoReady = onLogoReady,
            backdrop = backdrop,
            onUpdateAlpha = { globalBackgroundAlpha = it },
        )
    }
}

@Composable
private fun authBackgroundLayer(
    isAuthScreen: Boolean,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop,
    animatedGlobalAlpha: Float,
) {
    if (isAuthScreen) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .layerBackdrop(backdrop)
                    .alpha(animatedGlobalAlpha),
        ) {
            com.luxury.cheats.core.ui.dotPatternBackground()
            com.luxury.cheats.core.ui.welcomeEclipseSection()
        }
    }
}

@Composable
private fun appNavGraph(
    navController: NavHostController,
    onLogoReady: () -> Unit,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop,
    onUpdateAlpha: (Float) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = AuthGraph,
        modifier = Modifier.fillMaxSize(),
    ) {
        navigation<AuthGraph>(startDestination = Splash) {
            welcomeGraph(navController, onLogoReady)

            composable<Login> {
                com.luxury.cheats.features.login.pantalla.ui.loginPantallaScreen(
                    onLoginSuccess = {
                        navController.navigate(MainGraph) {
                            popUpTo<AuthGraph> { inclusive = true }
                        }
                    },
                    onUpdateBackgroundVisibility = { isVisible ->
                        onUpdateAlpha(if (isVisible) 1f else 0f)
                    },
                )
            }
        }

        navigation<MainGraph>(startDestination = Home) {
            mainGraph(navController, backdrop)
        }
    }
}

private fun androidx.navigation.NavGraphBuilder.welcomeGraph(
    navController: NavHostController,
    onLogoReady: () -> Unit,
) {
    composable<Splash> {
        welcomeSplashScreen(
            onNavigateToPage1 = {
                navController.navigate(WelcomePage1) {
                    popUpTo<Splash> { inclusive = true }
                }
            },
            onLogoReady = onLogoReady,
        )
    }

    composable<WelcomePage1> {
        welcomePage1Screen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateNext = { navController.navigate(WelcomePage2) },
        )
    }

    composable<WelcomePage2> {
        welcomePage2Screen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateNext = { navController.navigate(WelcomePage3) },
        )
    }

    composable<WelcomePage3> {
        welcomePage3Screen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateNext = {
                navController.navigate(Login)
            },
        )
    }
}

private fun androidx.navigation.NavGraphBuilder.mainGraph(
    navController: NavHostController,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop? = null,
) {
    composable<Home> {
        val viewModel: HomeViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()

        homeScreen(
            uiState = uiState,
            onAction = { action: HomeAction -> viewModel.onAction(action) },
            navController = navController,
            backdrop = backdrop,
        )
    }

    composable<Perfil> {
        perfilScreen(backdrop = backdrop)
    }

    composable<Update> {
        downloadUpdateScreen(
            onBackClick = { navController.popBackStack() },
        )
    }
}
