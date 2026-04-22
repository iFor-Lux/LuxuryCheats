package com.luxury.cheats.features.login.pantalla.logic

import androidx.compose.ui.text.input.TextFieldValue

/**
 * Credenciales de acceso y sus callbacks de modificación.
 *
 * @property username Nombre de usuario.
 * @property password Contraseña.
 * @property onUsernameChange Callback para actualizar el usuario.
 * @property onPasswordChange Callback para actualizar la contraseña.
 */
data class LoginCredentials(
    val username: TextFieldValue,
    val password: TextFieldValue,
    val licenseKey: TextFieldValue,
    val isLicenseMode: Boolean,
    val focusedField: com.luxury.cheats.features.login.pantalla.logic.LoginField,
    val debugMessage: String,

    val onUsernameChange: (TextFieldValue) -> Unit,
    val onPasswordChange: (TextFieldValue) -> Unit,
    val onLicenseChange: (TextFieldValue) -> Unit,
    val onLicenseModeToggle: (Boolean) -> Unit,
    val onGetLicenseClick: () -> Unit,
    val onFocusFieldChange: (LoginField) -> Unit,
)


