package com.luxury.cheats.features.login.pantalla.logic

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luxury.cheats.services.firebase.AuthService
import com.luxury.cheats.services.storage.UserPreferencesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar la lógica y el estado de la pantalla de login.
 */
class LoginPantallaViewModel(
    private val authService: AuthService,
    private val preferencesService: UserPreferencesService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginPantallaState())
    val uiState: StateFlow<LoginPantallaState> = _uiState.asStateFlow()

    private var saveDebounceJob: kotlinx.coroutines.Job? = null

    init {
        // Cargar preferencias locales al iniciar
        val isSaveEnabled = preferencesService.saveUserFeature()
        if (isSaveEnabled) {
            val saved = preferencesService.accessCredentials()
            val isLicenseMode = preferencesService.accessLicenseMode()
            
            if (saved != null) {
                if (isLicenseMode) {
                    val licenseTFV = TextFieldValue(text = saved.first, selection = TextRange(saved.first.length))
                    _uiState.update {
                        it.copy(
                            saveUser = true,
                            isLicenseMode = true,
                            licenseKey = licenseTFV
                        )
                    }
                } else {
                    val userTFV = TextFieldValue(text = saved.first, selection = TextRange(saved.first.length))
                    val passTFV = TextFieldValue(text = saved.second, selection = TextRange(saved.second.length))

                    _uiState.update {
                        it.copy(
                            saveUser = true,
                            username = userTFV,
                            password = passTFV,
                        )
                    }
                    updateDebugMessage(saved.first, saved.second)
                }
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
            is LoginPantallaAction.OnLicenseChange -> handleLicenseChange(action.license)
            is LoginPantallaAction.OnLicenseModeToggle -> handleLicenseModeToggle(action.enabled)
            LoginPantallaAction.OnLoginWithLicenseClick -> performLicenseLogin()
            LoginPantallaAction.OnGetLicenseClick -> handleGetLicense()
            is LoginPantallaAction.OnFocusFieldChange -> handleFocusFieldChange(action.field)
        }
    }


    private fun handleLicenseChange(license: TextFieldValue) {
        _uiState.update { it.copy(licenseKey = license) }
        triggerDebouncedSave()
    }

    private fun handleLicenseModeToggle(enabled: Boolean) {
        _uiState.update {
            it.copy(
                isLicenseMode = enabled,
                focusedField = LoginField.NONE,
                tecladoType = com.luxury.cheats.features.login.teclado.logic.TecladoType.NONE
            )
        }
        preferencesService.accessLicenseMode(enabled)
        triggerDebouncedSave()
    }


    private fun handleGetLicense() {
        // En una app real, aquí dispararíamos un evento para que la UI abra el navegador.
        // Por ahora lo manejaremos en la UI directamente o via un SideEffect si fuera necesario.
    }

    private fun performLicenseLogin() {
        val key = _uiState.value.licenseKey.text.trim()

        if (key.isBlank()) {
            showNotification("Por favor ingrese su licencia", LoginNotificationType.ERROR)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            showNotification("Validando licencia...", LoginNotificationType.INFO)

            when (val result = authService.validateLicense(key)) {
                is AuthService.LoginResult.Success -> {
                    showNotification("¡Licencia activada!", LoginNotificationType.SUCCESS)
                    
                    // Guardar datos en caché del perfil
                    result.userData?.let { data ->
                        preferencesService.accessProfileCache(
                            mapOf(
                                "id" to data.optString("_key"),
                                "created" to data.optString("createdAt"),
                                "expiry" to data.optString("expirationDate"),
                                "device" to data.optString("device"),
                            )
                        )
                    }
                    preferencesService.accessLicenseMode(true) // Activar modo licencia
                    // Guardamos la clave como nombre de usuario para el perfil
                    preferencesService.accessCredentials(data = key to "LICENSE_MODE")

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccessful = true,
                        )
                    }
                }
                is AuthService.LoginResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    showNotification(result.message, LoginNotificationType.ERROR)
                }
            }
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
        preferencesService.saveUserFeature(save)
        if (save) {
            preferencesService.accessCredentials(
                data = _uiState.value.username.text to _uiState.value.password.text,
            )
        } else {
            preferencesService.accessCredentials(clear = true)
        }
        updateDebugMessage(_uiState.value.username.text, _uiState.value.password.text)
    }

    private fun handleFocusFieldChange(field: LoginField) {
        _uiState.update { it.copy(focusedField = field) }
    }

    private fun handleInteractionStateChange(state: LoginInteractionState) {

        _uiState.update {
            it.copy(
                interactionState = state,
                focusedField = if (state == LoginInteractionState.COMPACT) LoginField.NONE else it.focusedField,
                tecladoType =
                    if (state == LoginInteractionState.COMPACT) {
                        com.luxury.cheats.features.login.teclado.logic.TecladoType.NONE
                    } else {
                        it.tecladoType
                    },
            )
        }
    }

    private fun handleTecladoTypeChange(type: com.luxury.cheats.features.login.teclado.logic.TecladoType) {
        _uiState.update {
            it.copy(
                tecladoType = type,
                focusedField = when (type) {
                    com.luxury.cheats.features.login.teclado.logic.TecladoType.ALPHABETIC -> LoginField.USERNAME
                    com.luxury.cheats.features.login.teclado.logic.TecladoType.NUMERIC -> LoginField.PASSWORD
                    else -> it.focusedField
                }
            )
        }
    }

    private fun handleToggleCase() {
        _uiState.update { it.copy(isUpperCase = !it.isUpperCase) }
    }

    private fun handleKeyClick(key: String) {
        _uiState.update { state ->
            val currentFieldValue = when (state.focusedField) {
                LoginField.USERNAME -> state.username
                LoginField.PASSWORD -> state.password
                LoginField.LICENSE -> state.licenseKey
                LoginField.NONE -> TextFieldValue("")
            }


            val text = currentFieldValue.text
            val selection = currentFieldValue.selection

            // Si hay selección, la reemplazamos. Si no, insertamos en la posición del cursor.
            val newText = if (selection.collapsed) {
                StringBuilder(text).insert(selection.min, key).toString()
            } else {
                StringBuilder(text).replace(selection.min, selection.max, key).toString()
            }
            
            val newSelection = TextRange(selection.min + key.length)
            val newValue = currentFieldValue.copy(text = newText, selection = newSelection)

            val newState = when (state.focusedField) {
                LoginField.USERNAME -> state.copy(username = newValue)
                LoginField.PASSWORD -> state.copy(password = newValue)
                LoginField.LICENSE -> state.copy(licenseKey = newValue)
                LoginField.NONE -> state
            }
            updateDebugMessage(newState.username.text, newState.password.text)
            newState
        }.also { triggerDebouncedSave() }
    }


    private fun handleKeyDelete() {
        _uiState.update { state ->
            val currentFieldValue = when (state.focusedField) {
                LoginField.USERNAME -> state.username
                LoginField.PASSWORD -> state.password
                LoginField.LICENSE -> state.licenseKey
                LoginField.NONE -> TextFieldValue("")
            }

            val text = currentFieldValue.text
            val selection = currentFieldValue.selection

            val newValue =
                if (selection.collapsed) {
                    if (selection.min > 0) {
                        val newText = StringBuilder(text).deleteCharAt(selection.min - 1).toString()
                        val newSelection = TextRange(selection.min - 1)
                        currentFieldValue.copy(text = newText, selection = newSelection)
                    } else {
                        null
                    }
                } else {
                    val newText = StringBuilder(text).delete(selection.min, selection.max).toString()
                    val newSelection = TextRange(selection.min)
                    currentFieldValue.copy(text = newText, selection = newSelection)
                }

            if (newValue != null) {
                val newState = when (state.focusedField) {
                    LoginField.USERNAME -> state.copy(username = newValue)
                    LoginField.PASSWORD -> state.copy(password = newValue)
                    LoginField.LICENSE -> state.copy(licenseKey = newValue)
                    LoginField.NONE -> state
                }
                updateDebugMessage(newState.username.text, newState.password.text)
                newState
            } else {
                state
            }
        }.also { triggerDebouncedSave() }
    }


    private fun triggerDebouncedSave() {
        if (!_uiState.value.saveUser) return

        saveDebounceJob?.cancel()
        saveDebounceJob =
            viewModelScope.launch {
                kotlinx.coroutines.delay(LoginConstants.SAVE_DEBOUNCE_MILLIS)
                val state = _uiState.value
                val dataToSave = if (state.isLicenseMode) {
                    state.licenseKey.text to "LICENSE_MODE"
                } else {
                    state.username.text to state.password.text
                }
                preferencesService.accessCredentials(data = dataToSave)
            }

    }

    private fun updateDebugMessage(
        user: String,
        pass: String,
    ) {
        val message =
            if (_uiState.value.saveUser) {
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
                        preferencesService.accessCredentials(
                            data = username to password,
                        )
                    } else {
                        preferencesService.accessCredentials(clear = true)
                    }

                    // Guardar datos en caché del perfil
                    result.userData?.let { data ->
                        preferencesService.accessProfileCache(
                            mapOf(
                                "id" to data.optString("_key"),
                                "created" to data.optString("createdAt"),
                                "expiry" to data.optString("expirationDate"),
                                "device" to data.optString("device"),
                            )
                        )
                    }
                    preferencesService.accessLicenseMode(false) // Desactivar modo licencia

                    showNotification("¡Acceso concedido!", LoginNotificationType.SUCCESS)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccessful = true,
                        )
                    }
                }
                is AuthService.LoginResult.Error -> {
                    _uiState.update { it.copy(isLoading = false) }
                    val type =
                        when {
                            result.message.contains("Contraseña", ignoreCase = true) -> LoginNotificationType.WARNING
                            else -> LoginNotificationType.ERROR
                        }
                    showNotification(result.message, type)
                }
            }
        }
    }

    private fun showNotification(
        message: String,
        type: LoginNotificationType,
    ) {
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
