package com.luxury.cheats.features.login.pantalla.logic

import androidx.compose.ui.text.input.TextFieldValue
import com.luxury.cheats.features.login.teclado.logic.TecladoType

/**
 * Representa los estados de la interacción visual del login.
 */
enum class LoginInteractionState {
    COMPACT,
    EXPANDED,
}

/**
 * Tipos de notificaciones disponibles en la pantalla de login.
 */
enum class LoginNotificationType {
    INFO,
    ERROR,
    WARNING,
    SUCCESS,
}

/**
 * Representa una notificación temporal que se muestra al usuario.
 *
 * @property id Identificador único de la notificación.
 * @property message Mensaje a mostrar.
 * @property type Tipo de severidad de la notificación.
 */
data class LoginNotification(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val type: LoginNotificationType,
)

/**
 * Estado global de la pantalla de login.
 */
data class LoginPantallaState(
    val username: TextFieldValue = TextFieldValue(""),
    val password: TextFieldValue = TextFieldValue(""),
    val saveUser: Boolean = false,
    val debugMessage: String = "",
    val notifications: List<LoginNotification> = emptyList(),
    val interactionState: LoginInteractionState = LoginInteractionState.COMPACT,
    val tecladoType: TecladoType = TecladoType.NONE,
    val isUpperCase: Boolean = false,
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
)
