package com.luxury.cheats.features.welcome.splash.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel para Welcome Splash Screen
 * - Maneja la lógica y estado del splash
 * - Separación de lógica de UI (cumple AGENTS.md)
 */
class WelcomeSplashViewModel : ViewModel() {
    private val _state = MutableStateFlow(WelcomeSplashState())
    val state: StateFlow<WelcomeSplashState> = _state.asStateFlow()

    /**
     * Procesa acciones del usuario
     */
    fun handleAction(action: WelcomeSplashAction) {
        when (action) {
            is WelcomeSplashAction.NavigateToNext -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isReadyToNavigate = true)
                }
            }
        }
    }

    /**
     * Inicia la secuencia de splash:
     * 1. Espera a que el logo esté listo (SharedFlow/StateFlow)
     * 2. Notifica a la UI que muestre el logo
     * 3. Espera el tiempo de apreciación
     * 4. Navega a la siguiente pantalla
     */
    suspend fun startSplashSequence(
        isLogoReadyFlow: StateFlow<Boolean>,
        onLogoReady: () -> Unit,
        onReadyToNavigate: () -> Unit,
    ) {
        // 1. Esperar señal de WebView/Logo listo
        isLogoReadyFlow.first { it }

        // 2. Notificar UI (animaciones de entrada, etc)
        onLogoReady()

        // 3. Delay de apreciación
        kotlinx.coroutines.delay(SPLASH_DISPLAY_DURATION)

        // 4. Navegar
        onReadyToNavigate()
    }

    /**
     * Constantes de configuración para la pantalla de bienvenida.
     */
    companion object {
        private const val SPLASH_DISPLAY_DURATION = 1500L
    }
}
