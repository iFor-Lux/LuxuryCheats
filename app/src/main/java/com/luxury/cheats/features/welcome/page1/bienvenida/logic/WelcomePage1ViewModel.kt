package com.luxury.cheats.features.welcome.page1.bienvenida.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para Welcome Page 1 (Bienvenida)
 * - Maneja la lógica y estado de la pantalla
 * - Separación de lógica de UI (cumple AGENTS.md)
 */
class WelcomePage1ViewModel : ViewModel() {

    private val _state = MutableStateFlow(WelcomePage1State())
    val state: StateFlow<WelcomePage1State> = _state.asStateFlow()

    /**
     * Procesa acciones del usuario
     */
    fun handleAction(action: WelcomePage1Action) {
        when (action) {
            is WelcomePage1Action.ContinueClicked -> {
                handleContinue()
            }
            is WelcomePage1Action.BackClicked -> {
                handleBack()
            }
        }
    }

    /**
     * Maneja la acción de continuar
     */
    private fun handleContinue() {
        viewModelScope.launch {
            // TODO: Implementar lógica de navegación a la siguiente pantalla
            // Por ahora solo actualizamos el estado
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    /**
     * Maneja la acción de atrás
     */
    private fun handleBack() {
        viewModelScope.launch {
            // TODO: Implementar lógica de navegación hacia atrás
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}

