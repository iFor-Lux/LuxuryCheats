package com.luxury.cheats.features.login.pantalla.logic

import androidx.compose.ui.text.input.TextFieldValue

/**
 * Acciones de usuario para la pantalla de login.
 */
sealed class LoginPantallaAction {
    /** Acción cuando cambia el nombre de usuario. */
    data class OnUsernameChange(val username: TextFieldValue) : LoginPantallaAction()

    /** Acción cuando cambia la contraseña. */
    data class OnPasswordChange(val password: TextFieldValue) : LoginPantallaAction()

    /** Acción cuando se alterna la opción de guardar usuario. */
    data class OnSaveUserToggle(val save: Boolean) : LoginPantallaAction()

    /** Acción cuando cambia el estado de interacción (Compacto/Expandido). */
    data class OnInteractionStateChange(val state: LoginInteractionState) : LoginPantallaAction()

    /** Acción cuando cambia el tipo de teclado visible. */
    data class OnTecladoTypeChange(
        val type: com.luxury.cheats.features.login.teclado.logic.TecladoType,
    ) : LoginPantallaAction()

    /** Acción cuando se presiona una tecla del teclado personalizado. */
    data class OnKeyClick(val key: String) : LoginPantallaAction()

    /** Acción para borrar el último carácter. */
    object OnKeyDelete : LoginPantallaAction()

    /** Acción para alternar entre mayúsculas y minúsculas. */
    object OnToggleCase : LoginPantallaAction()

    /** Acción para iniciar el proceso de login. */
    object OnLoginClick : LoginPantallaAction()

    /** Acción cuando cambia la clave de licencia. */
    data class OnLicenseChange(val license: TextFieldValue) : LoginPantallaAction()

    /** Acción para alternar entre login normal y por licencia. */
    data class OnLicenseModeToggle(val enabled: Boolean) : LoginPantallaAction()

    /** Acción para iniciar sesión con licencia. */
    object OnLoginWithLicenseClick : LoginPantallaAction()

    /** Acción para abrir la web de generación de licencias. */
    object OnGetLicenseClick : LoginPantallaAction()

    /** Acción cuando cambia el campo enfocado. */
    data class OnFocusFieldChange(val field: LoginField) : LoginPantallaAction()
}


