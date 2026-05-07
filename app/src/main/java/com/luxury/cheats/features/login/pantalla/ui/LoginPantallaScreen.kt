@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
package com.luxury.cheats.features.login.pantalla.ui


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luxury.cheats.core.theme.LuxuryCheatsTheme
import com.luxury.cheats.features.login.pantalla.logic.LoginCredentials
import com.luxury.cheats.features.login.pantalla.logic.LoginDisplayOptions
import com.luxury.cheats.features.login.pantalla.logic.LoginInteractionState
import com.luxury.cheats.features.login.pantalla.logic.LoginPantallaAction
import com.luxury.cheats.features.login.pantalla.logic.LoginPantallaState
import com.luxury.cheats.features.login.pantalla.logic.LoginPantallaViewModel

@Composable
fun LoginPantallaScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    onUpdateBackgroundVisibility: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val viewModel: LoginPantallaViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val authService = com.luxury.cheats.services.firebase.AuthService()
                val prefsService = com.luxury.cheats.services.storage.UserPreferencesService(context)
                val firebaseService = com.luxury.cheats.services.firebase.FirebaseService()
                return LoginPantallaViewModel(authService, prefsService, firebaseService) as T
            }
        }
    )
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) onLoginSuccess()
    }

    LaunchedEffect(state.interactionState) {
        onUpdateBackgroundVisibility(state.interactionState != LoginInteractionState.EXPANDED)
    }

    LoginScreenContent(
        state = state,
        onAction = viewModel::onAction,
        onLoginSuccess = onLoginSuccess,
        clearFocus = { focusManager.clearFocus() },
        modifier = modifier,
    )
}

@Composable
private fun LoginScreenContent(
    state: LoginPantallaState,
    onAction: (LoginPantallaAction) -> Unit,
    onLoginSuccess: () -> Unit,
    clearFocus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
            if (state.interactionState == LoginInteractionState.EXPANDED) {
                onAction(LoginPantallaAction.OnInteractionStateChange(LoginInteractionState.COMPACT))
                clearFocus()
            }
        },
        contentAlignment = Alignment.Center,
    ) {
        LoginPantallaImagenSection(
            imageUrl = state.loginImageUrl,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        LoginMainCard(state, onAction, onLoginSuccess)

        com.luxury.cheats.features.login.teclado.ui.LoginTecladoSection(
            type = state.tecladoType,
            isUpperCase = state.isUpperCase,
            actions = com.luxury.cheats.features.login.teclado.ui.TecladoActions(
                onKeyPress = { onAction(LoginPantallaAction.OnKeyClick(it)) },
                onDelete = { onAction(LoginPantallaAction.OnKeyDelete) },
                onToggleCase = { onAction(LoginPantallaAction.OnToggleCase) },
                onDone = {
                    onAction(LoginPantallaAction.OnInteractionStateChange(LoginInteractionState.COMPACT))
                    onAction(LoginPantallaAction.OnLoginClick)
                },
                onTecladoTypeChange = { onAction(LoginPantallaAction.OnTecladoTypeChange(it)) }
            ),
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        LoginPantallaMessageSection(
            notifications = state.notifications,
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 16.dp),
        )

        AnimatedVisibility(
            visible = state.isWaitingFreeQueue,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f))
                    .clickable(enabled = true, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .width(300.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f), RoundedCornerShape(28.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material3.CircularWavyProgressIndicator()
                    Spacer(modifier = Modifier.height(24.dp))
                    androidx.compose.material3.Text(
                        text = "SERVIDORES SATURADOS...",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.Text(
                        text = "Los servidores gratuitos están saturados. Los usuarios VIP tienen acceso prioritario. Por favor, espera tu turno.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    androidx.compose.material3.Text(
                        text = "Posición en cola: ${state.queuePosition}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    androidx.compose.material3.Text(
                        text = "Tiempo estimado: ${state.queueTimeRemaining}s",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    androidx.compose.material3.Button(
                        onClick = {
                            // Abre canal de pago o similar
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.width(200.dp)
                    ) {
                        androidx.compose.material3.Text("ADQUIRIR VIP")
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginMainCard(state: LoginPantallaState, onAction: (LoginPantallaAction) -> Unit, onLoginSuccess: () -> Unit) {
    val isExpanded = state.interactionState == LoginInteractionState.EXPANDED
    val premiumBounce = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    val premiumBounceDp = spring<androidx.compose.ui.unit.Dp>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)

    val cardScale by animateFloatAsState(targetValue = if (isExpanded) 1.1f else 1f, animationSpec = premiumBounce)
    val secondaryAlpha by animateFloatAsState(targetValue = if (isExpanded) 0f else 1f, animationSpec = premiumBounce)
    val contentOffset by animateDpAsState(targetValue = if (isExpanded) (-100).dp else 0.dp, animationSpec = premiumBounceDp)

    Box(
        modifier = Modifier.width(270.dp).graphicsLayer { translationY = contentOffset.toPx(); scaleX = cardScale; scaleY = cardScale }
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = secondaryAlpha * 0.9f), RoundedCornerShape(32.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = secondaryAlpha), RoundedCornerShape(32.dp)),
        contentAlignment = Alignment.Center
    ) {
        LoginCardContent(state, isExpanded, onAction)
    }
}

@Composable
private fun LoginCardContent(state: LoginPantallaState, isExpanded: Boolean, onAction: (LoginPantallaAction) -> Unit) {
    val premiumBounce = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)

    Column(modifier = Modifier.width(270.dp).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedVisibility(
            visible = !isExpanded,
            enter = fadeIn() + scaleIn(initialScale = 0.8f, animationSpec = premiumBounce),
            exit = fadeOut() + scaleOut(targetScale = 0.8f, animationSpec = premiumBounce)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(4.dp))
                LoginPantallaTextSection()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        LoginPantallaUserPasswordSection(
            credentials = LoginCredentials(
                username = state.username, password = state.password, licenseKey = state.licenseKey, isLicenseMode = state.isLicenseMode, focusedField = state.focusedField, debugMessage = state.debugMessage,
                onUsernameChange = { onAction(LoginPantallaAction.OnUsernameChange(it)) }, onPasswordChange = { onAction(LoginPantallaAction.OnPasswordChange(it)) }, onLicenseChange = { onAction(LoginPantallaAction.OnLicenseChange(it)) }, onLicenseModeToggle = { onAction(LoginPantallaAction.OnLicenseModeToggle(it)) }, onGetLicenseClick = {}, onFocusFieldChange = { onAction(LoginPantallaAction.OnFocusFieldChange(it)) }
            ),
            options = LoginDisplayOptions(saveUser = state.saveUser, onSaveUserChange = { onAction(LoginPantallaAction.OnSaveUserToggle(it)) }),
            displayState = LoginDisplayState(interactionState = state.interactionState, tecladoType = state.tecladoType),
            actions = LoginFieldActions(onInteractionStateChange = { onAction(LoginPantallaAction.OnInteractionStateChange(it)) }, onTecladoTypeChange = { onAction(LoginPantallaAction.OnTecladoTypeChange(it)) }),
        )

        AnimatedVisibility(
            visible = !isExpanded,
            enter = fadeIn() + scaleIn(initialScale = 0.8f, animationSpec = premiumBounce),
            exit = fadeOut() + scaleOut(targetScale = 0.8f, animationSpec = premiumBounce)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(16.dp))
                LoginPantallaButtonSection(onLoginClick = { if (state.isLicenseMode) onAction(LoginPantallaAction.OnLoginWithLicenseClick) else onAction(LoginPantallaAction.OnLoginClick) })
            }
        }
    }
}

@Preview(name = "Login Light")
@Composable
fun LoginPantallaScreenPreviewLight() {
    LuxuryCheatsTheme(darkTheme = false) { Surface(color = MaterialTheme.colorScheme.background) { LoginPantallaScreen(onLoginSuccess = {}) } }
}

@Preview(name = "Login Dark")
@Composable
fun LoginPantallaScreenPreviewDark() {
    LuxuryCheatsTheme(darkTheme = true) { Surface(color = MaterialTheme.colorScheme.background) { LoginPantallaScreen(onLoginSuccess = {}) } }
}
