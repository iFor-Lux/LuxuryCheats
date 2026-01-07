package com.luxury.cheats.core.ui

/**
 * Tipos de notificación para el sistema de Toast
 */
enum class NotificationType {
    ERROR,
    WARNING,
    INFO,
    SUCCESS
}

/**
 * Modelo de notificación genérico para mostrar mensajes toast
 */
data class AppNotification(
    val message: String,
    val type: NotificationType,
    val id: Long = System.currentTimeMillis()
)
