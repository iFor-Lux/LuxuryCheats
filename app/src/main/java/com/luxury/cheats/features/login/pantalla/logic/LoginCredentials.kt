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
    val debugMessage: String,
    val onUsernameChange: (TextFieldValue) -> Unit,
    val onPasswordChange: (TextFieldValue) -> Unit,
)
