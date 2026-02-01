package com.luxury.cheats.features.login.pantalla.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luxury.cheats.services.AuthService
import com.luxury.cheats.services.UserPreferencesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * ViewModel para gestionar la lógica y el estado de la pantalla de login.
 */
class LoginPantallaViewModel(
    private val authService: AuthService,
    private val preferencesService: UserPreferencesService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginPantallaState())
    val uiState: StateFlow<LoginPantallaState> = _uiState.asStateFlow()

    private var saveDebounceJob: kotlinx.coroutines.Job? = null

    init {
        // Cargar preferencias locales al iniciar
        val isSaveEnabled = preferencesService.isSaveUserEnabled()
        if (isSaveEnabled) {
            val saved = preferencesService.getCredentials()
            if (saved != null) {
                val userTFV = TextFieldValue(text = saved.first, selection = TextRange(saved.first.length))
                val passTFV = TextFieldValue(text = saved.second, selection = TextRange(saved.second.length))
                
                _uiState.update { 
                    it.copy(
                        saveUser = true,
                        username = userTFV,
                        password = passTFV
                    ) 
                }
                updateDebugMessage(saved.first, saved.second)
            } else {
                _uiState.update { it.copy(saveUser = true) }
            }
        }
    }

    /**
     * Procesa las acciones emitidas desde la UI.
     *
     * @param action Acción a procesar.
     */
    fun onAction(action: LoginPantallaAction) {
        when (action) {
            is LoginPantallaAction.OnUsernameChange -> handleUsernameChange(action.username)
            is LoginPantallaAction.OnPasswordChange -> handlePasswordChange(action.password)
            is LoginPantallaAction.OnSaveUserToggle -> handleSaveUserToggle(action.save)
            is LoginPantallaAction.OnInteractionStateChange -> handleInteractionStateChange(action.state)
            is LoginPantallaAction.OnTecladoTypeChange -> handleTecladoTypeChange(action.type)
            is LoginPantallaAction.OnToggleCase -> handleToggleCase()
            is LoginPantallaAction.OnKeyClick -> handleKeyClick(action.key)
            LoginPantallaAction.OnKeyDelete -> handleKeyDelete()
            LoginPantallaAction.OnLoginClick -> performLogin()
        }
    }

    private fun handleUsernameChange(username: TextFieldValue) {
        _uiState.update { it.copy(username = username) }
        updateDebugMessage(username.text, _uiState.value.password.text)
        triggerDebouncedSave()
    }

    private fun handlePasswordChange(password: TextFieldValue) {
        _uiState.update { it.copy(password = password) }
        updateDebugMessage(_uiState.value.username.text, password.text)
        triggerDebouncedSave()
    }

    private fun handleSaveUserToggle(save: Boolean) {
        _uiState.update { it.copy(saveUser = save) }
        preferencesService.setSaveUserEnabled(save)
        if (save) {
            preferencesService.saveCredentials(
                _uiState.value.username.text,
                _uiState.value.password.text
            )
        } else {
            preferencesService.clearCredentials()
        }
        updateDebugMessage(_uiState.value.username.text, _uiState.value.password.text)
    }

    private fun handleInteractionStateChange(state: LoginInteractionState) {
        _uiState.update { 
            it.copy(
                interactionState = state,
                tecladoType = if (state == LoginInteractionState.COMPACT) {
                    com.luxury.cheats.features.login.teclado.logic.TecladoType.NONE
                } else it.tecladoType
            )
        }
    }

    private fun handleTecladoTypeChange(type: com.luxury.cheats.features.login.teclado.logic.TecladoType) {
        _uiState.update { it.copy(tecladoType = type) }
    }

    private fun handleToggleCase() {
        _uiState.update { it.copy(isUpperCase = !it.isUpperCase) }
    }

    private fun handleKeyClick(key: String) {
        _uiState.update { state ->
            val isUserField = state.tecladoType == com.luxury.cheats.features.login.teclado.logic.TecladoType.ALPHABETIC
            val currentFieldValue = if (isUserField) state.username else state.password
            
            val text = currentFieldValue.text
            val selection = currentFieldValue.selection
            
            val newText = StringBuilder(text).insert(selection.min, key).toString()
            val newSelection = TextRange(selection.min + key.length)
            val newValue = currentFieldValue.copy(text = newText, selection = newSelection)
            
            val newState = if (isUserField) state.copy(username = newValue) else state.copy(password = newValue)
            updateDebugMessage(newState.username.text, newState.password.text)
            newState
        }.also { triggerDebouncedSave() }
    }

    private fun handleKeyDelete() {
        _uiState.update { state ->
            val isUserField = state.tecladoType == com.luxury.cheats.features.login.teclado.logic.TecladoType.ALPHABETIC
            val currentFieldValue = if (isUserField) state.username else state.password
            val text = currentFieldValue.text
            val selection = currentFieldValue.selection
            
            val newValue = if (selection.collapsed) {
                if (selection.min > 0) {
                    val newText = StringBuilder(text).deleteCharAt(selection.min - 1).toString()
                    val newSelection = TextRange(selection.min - 1)
                    currentFieldValue.copy(text = newText, selection = newSelection)
                } else null
            } else {
                val newText = StringBuilder(text).delete(selection.min, selection.max).toString()
                val newSelection = TextRange(selection.min)
                currentFieldValue.copy(text = newText, selection = newSelection)
            }

            if (newValue != null) {
                val newState = if (isUserField) state.copy(username = newValue) else state.copy(password = newValue)
                updateDebugMessage(newState.username.text, newState.password.text)
                newState
            } else state
        }.also { triggerDebouncedSave() }
    }

    private fun triggerDebouncedSave() {
        if (!_uiState.value.saveUser) return
        
        saveDebounceJob?.cancel()
        saveDebounceJob = viewModelScope.launch {
            kotlinx.coroutines.delay(LoginConstants.SAVE_DEBOUNCE_MILLIS)
            preferencesService.saveCredentials(
                _uiState.value.username.text,
                _uiState.value.password.text
            )
        }
    }

    private fun autoSaveIfEnabled() {
        // Redundante con triggerDebouncedSave, se mantiene por compatibilidad si es necesario
        // pero se prefiere el debounced save para evitar I/O excesivo.
        triggerDebouncedSave()
    }

    private fun updateDebugMessage(user: String, pass: String) {
        val message = if (_uiState.value.saveUser) {
            "Usuario=\"$user\" | Contraseña=\"$pass\""
        } else {
            ""
        }
        _uiState.update { it.copy(debugMessage = message) }
    }

    private fun performLogin() {
        val username = _uiState.value.username.text.trim()
        val password = _uiState.value.password.text.trim()

        if (username.isBlank() || password.isBlank()) {
            showNotification("Por favor ingrese usuario y contraseña", LoginNotificationType.ERROR)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Verificando debe ser Verde (INFO en este mapeo)
            showNotification("Verificando credenciales...", LoginNotificationType.INFO)
            
            when (val result = authService.loginWithFirebase(username, password)) {
                is AuthService.LoginResult.Success -> {
                    if (_uiState.value.saveUser) {
                        preferencesService.saveCredentials(username, password)
                    } else {
                        preferencesService.clearCredentials()
                    }

                    showNotification("¡Acceso concedido!", LoginNotificationType.SUCCESS)
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            isLoginSuccessful = true
                        ) 
                    }
                }
                is AuthService.LoginResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    val type = when {
                        result.message.contains("Contraseña", ignoreCase = true) -> LoginNotificationType.WARNING
                        else -> LoginNotificationType.ERROR
                    }
                    showNotification(result.message, type)
                }
            }
        }
    }

    private fun showNotification(message: String, type: LoginNotificationType) {
        val newNotification = LoginNotification(message = message, type = type)
        _uiState.update { it.copy(notifications = it.notifications + newNotification) }
        
        viewModelScope.launch {
            kotlinx.coroutines.delay(LoginConstants.NOTIFICATION_DISMISS_DELAY)
            _uiState.update { state ->
                state.copy(notifications = state.notifications.filter { it.id != newNotification.id })
            }
        }
    }
}

private object LoginConstants {
    const val NOTIFICATION_DISMISS_DELAY = 4000L
    const val SAVE_DEBOUNCE_MILLIS = 1000L
}
