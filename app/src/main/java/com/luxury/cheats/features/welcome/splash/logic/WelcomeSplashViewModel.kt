package com.luxury.cheats.features.welcome.splash.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
     * Marca el splash como listo para navegar
     * (llamado cuando las animaciones han terminado)
     */
    fun markAsReady() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isReadyToNavigate = true)
        }
    }
}

