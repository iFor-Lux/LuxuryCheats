package com.luxury.cheats.features.login.pantalla.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.luxury.cheats.core.ui.AppNotification
import com.luxury.cheats.core.ui.AppToast
import com.luxury.cheats.core.ui.NotificationType
import com.luxury.cheats.features.login.pantalla.logic.LoginNotification
import com.luxury.cheats.features.login.pantalla.logic.LoginNotificationType

/**
 * Sección de mensajes para la pantalla de Login
 * Ahora usa el componente genérico AppToast de core
 */
@Composable
fun LoginPantallaMessageSection(
    notifications: List<LoginNotification>,
    modifier: Modifier = Modifier
) {
    // Convertir LoginNotification a AppNotification genérico
    val appNotifications = notifications.map { loginNotification ->
        AppNotification(
            message = loginNotification.message,
            type = when (loginNotification.type) {
                LoginNotificationType.ERROR -> NotificationType.ERROR
                LoginNotificationType.WARNING -> NotificationType.WARNING
                LoginNotificationType.INFO -> NotificationType.INFO
                LoginNotificationType.SUCCESS -> NotificationType.SUCCESS
            },
            id = loginNotification.id
        )
    }
    
    AppToast(
        notifications = appNotifications,
        modifier = modifier
    )
}
