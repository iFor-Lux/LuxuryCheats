package com.luxury.cheats.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

private object ToastConstants {
    const val MAX_VISIBLE = 4
    const val OFFSET_STEP = 12
    const val ANIM_DURATION = 400
    const val SCALE_DEC = 0.04f
    const val ALPHA_BASE = 0.9f
    const val ALPHA_DEC = 0.1f
    const val Z_INDEX_BASE = 5
    const val MAX_WIDTH = 340
    
    // Hex Colors
    const val RED_LIGHT = 0xFFFFEBEE
    const val YELLOW_LIGHT = 0xFFFFFDE7
    const val GREEN_LIGHT = 0xFFE8F5E9
    const val RED_DARK = 0xFFC62828
    const val YELLOW_DARK = 0xFFF9A825
    const val GREEN_DARK = 0xFF2E7D32
    const val STROKE_ALPHA = 0.3f
}

/**
 * Componente Toast genérico para mostrar notificaciones en la app
 * Muestra hasta 4 notificaciones apiladas con efecto de profundidad
 */
@Composable
fun AppToast(
    notifications: List<AppNotification>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        val displayed = notifications.takeLast(ToastConstants.MAX_VISIBLE).reversed()
        
        displayed.forEachIndexed { index, notification ->
            val isTop = index == 0
            val offsetInDp = (index * ToastConstants.OFFSET_STEP).dp
            
            val scale by animateFloatAsState(
                targetValue = if (isTop) 1f else 1f - index * ToastConstants.SCALE_DEC,
                animationSpec = tween(ToastConstants.ANIM_DURATION),
                label = "toast_scale"
            )
            
            val alpha by animateFloatAsState(
                targetValue = if (isTop) 1f else ToastConstants.ALPHA_BASE - index * ToastConstants.ALPHA_DEC,
                animationSpec = tween(ToastConstants.ANIM_DURATION),
                label = "toast_alpha"
            )

            ToastCard(
                notification = notification,
                isTop = isTop,
                modifier = Modifier
                    .offset(y = offsetInDp)
                    .scale(scale)
                    .alpha(alpha)
                    .zIndex((ToastConstants.Z_INDEX_BASE - index).toFloat())
            )
        }
    }
}

@Composable
private fun ToastCard(
    notification: AppNotification,
    isTop: Boolean,
    modifier: Modifier = Modifier
) {
    val (containerColor, contentColor, icon) = getNotificationTheme(notification.type)
    val strokeAlpha = ToastConstants.STROKE_ALPHA
    val strokeColor = contentColor.copy(alpha = strokeAlpha)
    val shape = RoundedCornerShape(32.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = ToastConstants.MAX_WIDTH.dp)
            .graphicsLayer {
                this.shape = shape
                this.clip = true
            },
        color = containerColor,
        contentColor = contentColor,
        shape = shape,
        border = BorderStroke(1.dp, strokeColor),
        shadowElevation = 4.dp
    ) {
        ToastCardContent(notification.message, icon, isTop)
    }
}

@Composable
private fun getNotificationTheme(
    type: NotificationType
): Triple<Color, Color, androidx.compose.ui.graphics.vector.ImageVector> {
    val containerColor = when (type) {
        NotificationType.ERROR -> Color(ToastConstants.RED_LIGHT)
        NotificationType.WARNING -> Color(ToastConstants.YELLOW_LIGHT)
        NotificationType.INFO -> Color(ToastConstants.GREEN_LIGHT)
        NotificationType.SUCCESS -> Color(ToastConstants.GREEN_LIGHT)
    }
    
    val contentColor = when (type) {
        NotificationType.ERROR -> Color(ToastConstants.RED_DARK)
        NotificationType.WARNING -> Color(ToastConstants.YELLOW_DARK)
        NotificationType.INFO -> Color(ToastConstants.GREEN_DARK)
        NotificationType.SUCCESS -> Color(ToastConstants.GREEN_DARK)
    }

    val icon = when (type) {
        NotificationType.ERROR -> Icons.Default.Warning
        NotificationType.WARNING -> Icons.Default.Info
        NotificationType.INFO -> Icons.Default.Info
        NotificationType.SUCCESS -> Icons.Default.CheckCircle
    }
    
    return Triple(containerColor, contentColor, icon)
}

@Composable
private fun ToastCardContent(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isTop: Boolean
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 14.dp)
            .alpha(if (isTop) 1f else 0f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
