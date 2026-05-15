package com.luxury.cheats.features.welcome.page3.shizuku.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luxury.cheats.services.firebase.FirebaseService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la tercera página de bienvenida (Shizuku/ADB).
 * Gestiona el estado de Shizuku y las acciones del usuario.
 */
@HiltViewModel
class WelcomePage3ViewModel
    @Inject
    constructor(
        private val firebaseService: FirebaseService,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(WelcomePage3State())

        /** Estado de la UI expuesto como StateFlow. */
        val uiState: StateFlow<WelcomePage3State> = _uiState.asStateFlow()

        init {
            fetchShizukuImage()
        }

        private fun fetchShizukuImage() {
            viewModelScope.launch {
                // Intentar con mayúscula inicial primero
                var url = firebaseService.fetchImageUrl("Shizuku")

                // Si falla, intentar en minúsculas por si acaso
                if (url == null) {
                    url = firebaseService.fetchImageUrl("shizuku")
                }

                _uiState.update { it.copy(imageUrl = url) }
                android.util.Log.d("WelcomePage3ViewModel", "Shizuku imageUrl: $url")
            }
        }

        /**
         * Procesa las acciones enviadas desde la interfaz de usuario.
         *
         * @param action La acción a ejecutar.
         */
        fun handleAction(action: WelcomePage3Action) {
            when (action) {
                WelcomePage3Action.DownloadShizukuClicked -> {
                    // Lógica para descargar Shizuku
                }
                WelcomePage3Action.StartShizukuClicked -> {
                    // Lógica para intentar iniciar el servicio
                }
                WelcomePage3Action.CheckStatusClicked -> {
                    checkShizukuStatus()
                }
            }
        }

        /** Verifica el estado actual de Shizuku en el dispositivo. */
        fun checkShizukuStatus() {
            _uiState.update {
                it.copy(statusMessage = "Verificando estado de Shizuku...")
            }
            // Simulación de verificación
        }
    }
