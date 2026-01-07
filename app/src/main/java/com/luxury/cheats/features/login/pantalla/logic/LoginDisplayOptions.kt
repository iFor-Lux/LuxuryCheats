package com.luxury.cheats.features.login.pantalla.logic

/**
 * Opciones de visualización y acciones para la sección de usuario/contraseña del login.
 *
 * @property saveUser Estado actual de si se debe guardar el usuario.
 * @property debugMessage Mensaje de diagnóstico para depuración.
 * @property onSaveUserChange Callback cuando cambia el estado de guardado.
 */
data class LoginDisplayOptions(
    val saveUser: Boolean,
    val onSaveUserChange: (Boolean) -> Unit
)
