package com.luxury.cheats.features.welcome.page2.permisos.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Representa los colores del tema para un item de permiso basándose en su estado.
 */
internal data class PermissionTheme(
    val iconBg: Color,
    val iconTint: Color,
    val border: Color,
)

/**
 * Provee el tema adecuado (colores) según si el permiso está concedido o denegado.
 */
@Composable
internal fun getSharedPermissionTheme(isGranted: Boolean, isDenied: Boolean): PermissionTheme {
    return PermissionTheme(
        iconBg = when {
            isGranted -> MaterialTheme.colorScheme.primaryContainer
            isDenied -> MaterialTheme.colorScheme.errorContainer
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        iconTint = when {
            isGranted -> MaterialTheme.colorScheme.onPrimaryContainer
            isDenied -> MaterialTheme.colorScheme.onErrorContainer
            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        },
        border = when {
            isGranted -> MaterialTheme.colorScheme.primary
            isDenied -> MaterialTheme.colorScheme.error
            else -> Color.Transparent
        }
    )
}
