package com.luxury.cheats.features.perfil.logic

/**
 * Estado de la interfaz de usuario para la pantalla de Perfil.
 * Versión Estricta: Sin placeholders ni campos innecesarios.
 */
data class PerfilState(
    val username: String = "",
    val isVip: Boolean = false,
    val tier: String = "free",
    val userId: String = "",
    val model: String = "",
    val ram: String = "",
    val targetSdk: String = "",
    val architecture: String = "",
    val androidVersion: String = "",
    val appVersion: String = "",
    val creationDate: String = "",
    val creationHour: String = "",
    val expiryDate: String = "",
    val remainingDays: String = "",
    val profileImageUri: String? = null,
    val bannerImageUri: String? = null,
)
