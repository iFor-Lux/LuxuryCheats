package com.luxury.cheats.features.home.logic

/**
 * Modelo para las notificaciones in-app de Firebase.
 */
data class InAppNotification(
    val id: String = "",
    val active: Boolean = false,
    val title: String = "",
    val description: String = "",
    // "always", "once"
    val frequency: String = "always",
    val image: String = "",
    val type: String = "in-app",
    val timestamp: String = "",
    val sent: Boolean = false,
)
