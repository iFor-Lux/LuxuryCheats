package com.luxury.cheats.features.welcome.page2.permisos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Sección de permiso de Administrador de Dispositivo.
 * Muestra el estado y permite solicitar el permiso al hacer clic.
 *
 * @param isGranted Indica si el administrador está activo.
 * @param isDenied Indica si fue denegado explícitamente.
 * @param onClick Callback para disparar la solicitud del permiso.
 */
@Composable
fun WelcomePage2Permission4Section(
    isGranted: Boolean = false,
    isDenied: Boolean = false,
    onClick: () -> Unit = {}
) {
    val containerColor = MaterialTheme.colorScheme.surfaceContainer
    val iconBgColor = when {
        isGranted -> MaterialTheme.colorScheme.primaryContainer
        isDenied -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val iconTint = when {
        isGranted -> MaterialTheme.colorScheme.primary
        isDenied -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    }
    val titleColor = MaterialTheme.colorScheme.onSurface
    val descriptionColor = MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = when {
        isGranted -> MaterialTheme.colorScheme.primary
        isDenied -> MaterialTheme.colorScheme.error
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PermissionIcon(
                iconBgColor = iconBgColor,
                iconTint = iconTint
            )
            Spacer(modifier = Modifier.width(16.dp))
            PermissionInfo(
                titleColor = titleColor,
                descriptionColor = descriptionColor
            )
        }
    }
}

@Composable
private fun PermissionIcon(
    iconBgColor: Color,
    iconTint: Color
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(iconBgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun PermissionInfo(
    titleColor: Color,
    descriptionColor: Color
) {
    Column {
        Text(
            text = "Permisos Administrador",
            color = titleColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Ocultar la app de la deteccion de Garena Free Fire y eliminar rastros de la app",
            color = descriptionColor,
            fontSize = 8.sp,
            lineHeight = 10.sp
        )
    }
}
