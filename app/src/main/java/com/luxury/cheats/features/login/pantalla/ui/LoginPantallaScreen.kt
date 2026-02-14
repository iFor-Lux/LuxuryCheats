package com.luxury.cheats.features.login.pantalla.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luxury.cheats.core.theme.luxuryCheatsTheme
import com.luxury.cheats.features.login.pantalla.logic.LoginCredentials
import com.luxury.cheats.features.login.pantalla.logic.LoginDisplayOptions
import com.luxury.cheats.features.login.pantalla.logic.LoginInteractionState
import com.luxury.cheats.features.login.pantalla.logic.LoginPantallaAction
import com.luxury.cheats.features.login.pantalla.logic.LoginPantallaState
import com.luxury.cheats.features.login.pantalla.logic.LoginPantallaViewModel

/**
 * Pantalla principal de inicio de sesión.
 */
@Composable
fun loginPantallaScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    onUpdateBackgroundVisibility: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val viewModel: LoginPantallaViewModel =
        viewModel(
            factory =
                object : ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        val authService = com.luxury.cheats.services.firebase.AuthService()
                        val prefsService = com.luxury.cheats.services.storage.UserPreferencesService(context)
                        val viewModel = LoginPantallaViewModel(authService, prefsService)
                        return viewModel as? T ?: error("Cannot create ViewModel")
                    }
                },
        )
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) onLoginSuccess()
    }

    // Controlar la visibilidad del fondo global basado en el estado de interacción
    LaunchedEffect(state.interactionState) {
        val isVisible = state.interactionState != LoginInteractionState.EXPANDED
        onUpdateBackgroundVisibility(isVisible)
    }

    loginScreenContent(
        state = state,
        onAction = viewModel::onAction,
        onLoginSuccess = onLoginSuccess,
        clearFocus = { focusManager.clearFocus() },
        modifier = modifier,
    )
}

@Composable
private fun loginScreenContent(
    state: LoginPantallaState,
    onAction: (LoginPantallaAction) -> Unit,
    onLoginSuccess: () -> Unit,
    clearFocus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    onAction(LoginPantallaAction.OnInteractionStateChange(LoginInteractionState.COMPACT))
                    clearFocus()
                },
        contentAlignment = Alignment.Center,
    ) {
        // Fondo local eliminado para usar el global y evitar parpadeos

        loginMainCard(state, onAction, onLoginSuccess)

        com.luxury.cheats.features.login.teclado.ui.loginTecladoSection(
            type = state.tecladoType,
            isUpperCase = state.isUpperCase,
            actions =
                com.luxury.cheats.features.login.teclado.ui.TecladoActions(
                    onKeyPress = { onAction(LoginPantallaAction.OnKeyClick(it)) },
                    onDelete = { onAction(LoginPantallaAction.OnKeyDelete) },
                    onToggleCase = { onAction(LoginPantallaAction.OnToggleCase) },
                    onDone = { onAction(LoginPantallaAction.OnInteractionStateChange(LoginInteractionState.COMPACT)) },
                ),
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        loginPantallaMessageSection(
            notifications = state.notifications,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 16.dp),
        )
    }
}

@Composable
private fun loginMainCard(
    state: LoginPantallaState,
    onAction: (LoginPantallaAction) -> Unit,
    onLoginSuccess: () -> Unit,
) {
    val isExpanded = state.interactionState == LoginInteractionState.EXPANDED
    val cardScale by animateFloatAsState(
        targetValue = if (isExpanded) 1.5f else 1f,
        animationSpec =
            spring(
                dampingRatio = if (isExpanded) Spring.DampingRatioHighBouncy else Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "cardScale",
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (isExpanded) 0f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "cardAlpha",
    )
    val contentOffset by animateDpAsState(
        targetValue = if (isExpanded) (-150).dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "contentOffset",
    )

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier =
                Modifier
                    .width(270.dp).height(412.dp)
                    .scale(cardScale).alpha(cardAlpha)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(46.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(46.dp)),
        )

        loginCardContent(state, cardAlpha, contentOffset, onAction)
    }
}

@Composable
private fun loginCardContent(
    state: LoginPantallaState,
    cardAlpha: Float,
    contentOffset: androidx.compose.ui.unit.Dp,
    onAction: (LoginPantallaAction) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .width(270.dp).height(412.dp).padding(16.dp)
                .graphicsLayer { translationY = contentOffset.toPx() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        loginPantallaTextSection(modifier = Modifier.alpha(cardAlpha))
        Spacer(modifier = Modifier.height(12.dp))

        loginPantallaUserPasswordSection(
            credentials =
                LoginCredentials(
                    username = state.username,
                    password = state.password,
                    debugMessage = state.debugMessage,
                    onUsernameChange = { onAction(LoginPantallaAction.OnUsernameChange(it)) },
                    onPasswordChange = { onAction(LoginPantallaAction.OnPasswordChange(it)) },
                ),
            options =
                LoginDisplayOptions(
                    saveUser = state.saveUser,
                    onSaveUserChange = { onAction(LoginPantallaAction.OnSaveUserToggle(it)) },
                ),
            displayState =
                LoginDisplayState(
                    interactionState = state.interactionState,
                    tecladoType = state.tecladoType,
                ),
            actions =
                LoginFieldActions(
                    onInteractionStateChange = { onAction(LoginPantallaAction.OnInteractionStateChange(it)) },
                    onTecladoTypeChange = { onAction(LoginPantallaAction.OnTecladoTypeChange(it)) },
                ),
        )

        Spacer(modifier = Modifier.height(12.dp))
        loginPantallaButtonSection(
            onLoginClick = {
                if (state.interactionState != LoginInteractionState.EXPANDED) {
                    onAction(LoginPantallaAction.OnLoginClick)
                }
            },
            modifier = Modifier.alpha(cardAlpha),
        )
    }
}

/**
 * Vista previa de la pantalla de inicio de sesión en modo claro.
 */
@Preview(name = "Login Light")
@Composable
fun loginPantallaScreenPreviewLight() {
    luxuryCheatsTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            loginPantallaScreen(onLoginSuccess = {})
        }
    }
}

/**
 * Vista previa de la pantalla de inicio de sesión en modo oscuro.
 */
@Preview(name = "Login Dark")
@Composable
fun loginPantallaScreenPreviewDark() {
    luxuryCheatsTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            loginPantallaScreen(onLoginSuccess = {})
        }
    }
}
