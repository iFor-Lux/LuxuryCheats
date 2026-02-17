package com.luxury.cheats.core.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

private object ToastConstants {
    const val MAX_VISIBLE = 4
    const val OFFSET_STEP = 12
    const val SCALE_DEC = 0.05f
    const val ALPHA_BASE = 0.95f
    const val ALPHA_DEC = 0.15f
    const val Z_INDEX_BASE = 10f
    const val MAX_WIDTH = 300 // Más minimalista

    val GLASS_ALPHA_LIGHT = 0.85f
    val GLASS_ALPHA_DARK = 0.75f
    val BORDER_ALPHA = 0.1f
}

private data class ToastTheme(
    val containerColor: Color,
    val contentColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

/**
 * Componente Toast minimalista siguiendo Material Design 3 Expressive.
 * Se adapta automáticamente a temas claros y oscuros con efectos de semitransparencia.
 */
@Composable
fun appToast(
    notifications: List<AppNotification>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        val displayed = notifications.takeLast(ToastConstants.MAX_VISIBLE).reversed()

        displayed.forEachIndexed { index, notification ->
            val isTop = index == 0
            
            // M3 Expressive Spring
            val springSpec = spring<Float>(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )

            val entryProgress by animateFloatAsState(
                targetValue = 1f,
                animationSpec = springSpec,
                label = "entry_animation"
            )

            val scale by animateFloatAsState(
                targetValue = (if (isTop) 1f else 1f - index * ToastConstants.SCALE_DEC) * entryProgress,
                animationSpec = springSpec,
                label = "toast_scale"
            )

            val alpha by animateFloatAsState(
                targetValue = if (isTop) entryProgress else (ToastConstants.ALPHA_BASE - index * ToastConstants.ALPHA_DEC).coerceAtLeast(0f) * entryProgress,
                animationSpec = springSpec,
                label = "toast_alpha"
            )

            val yOffset by animateFloatAsState(
                targetValue = (index * ToastConstants.OFFSET_STEP).toFloat() - (1f - entryProgress) * 40f,
                animationSpec = springSpec,
                label = "toast_offset"
            )

            toastCard(
                notification = notification,
                isTop = isTop,
                modifier =
                    Modifier
                        .offset(y = yOffset.dp)
                        .scale(scale)
                        .alpha(alpha)
                        .zIndex(ToastConstants.Z_INDEX_BASE - index),
            )
        }
    }
}

@Composable
private fun toastCard(
    notification: AppNotification,
    isTop: Boolean,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    val theme = getNotificationTheme(notification.type, isDark)
    val shape = RoundedCornerShape(24.dp) // M3 rounded corners
    
    val glassAlpha = if (isDark) ToastConstants.GLASS_ALPHA_DARK else ToastConstants.GLASS_ALPHA_LIGHT
    
    Surface(
        modifier =
            modifier
                .fillMaxWidth()
                .widthIn(max = ToastConstants.MAX_WIDTH.dp)
                .shadow(
                    elevation = if (isTop) 8.dp else 0.dp,
                    shape = shape,
                    spotColor = theme.containerColor.copy(alpha = 0.2f)
                )
                .clip(shape)
                .background(theme.containerColor.copy(alpha = glassAlpha))
                .border(
                    width = 0.5.dp,
                    color = theme.contentColor.copy(alpha = ToastConstants.BORDER_ALPHA),
                    shape = shape
                ),
        color = Color.Transparent,
        contentColor = theme.contentColor,
    ) {
        toastCardContent(notification.message, theme.icon, theme.contentColor, isTop)
    }
}

@Composable
private fun getNotificationTheme(type: NotificationType, isDark: Boolean): ToastTheme {
    val colorScheme = MaterialTheme.colorScheme
    
    return when (type) {
        NotificationType.ERROR -> ToastTheme(
            containerColor = colorScheme.errorContainer,
            contentColor = colorScheme.onErrorContainer,
            icon = Icons.Default.Warning
        )
        NotificationType.WARNING -> ToastTheme(
            containerColor = Color(0xFFFFF4E5).let { if (isDark) Color(0xFF332B00) else it },
            contentColor = Color(0xFF663C00).let { if (isDark) Color(0xFFFFE082) else it },
            icon = Icons.Default.Info
        )
        NotificationType.SUCCESS -> ToastTheme(
            containerColor = Color(0xFFE8F5E9).let { if (isDark) Color(0xFF00390A) else it },
            contentColor = Color(0xFF2E7D32).let { if (isDark) Color(0xFFA5D6A7) else it },
            icon = Icons.Default.CheckCircle
        )
        NotificationType.INFO -> ToastTheme(
            containerColor = colorScheme.secondaryContainer,
            contentColor = colorScheme.onSecondaryContainer,
            icon = Icons.Default.Info
        )
    }
}

@Composable
private fun toastCardContent(
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentColor: Color,
    isVisible: Boolean,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .alpha(if (isVisible) 1f else 0f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = contentColor.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 13.sp,
                letterSpacing = 0.4.sp
            ),
            color = contentColor,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Light Theme")
@Composable
fun AppToastLightPreview() {
    MaterialTheme {
        Box(modifier = Modifier.size(400.dp, 200.dp).background(Color.White)) {
            AppToastSample()
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Dark Theme")
@Composable
fun AppToastDarkPreview() {
    MaterialTheme(colorScheme = androidx.compose.material3.darkColorScheme()) {
        Box(modifier = Modifier.size(400.dp, 200.dp).background(Color.Black)) {
            AppToastSample()
        }
    }
}

@Composable
private fun AppToastSample() {
    val sampleNotifications =
        listOf(
            AppNotification("Error de conexión", NotificationType.ERROR),
            AppNotification("Activa los permisos para seguir", NotificationType.WARNING),
            AppNotification("Cambios guardados", NotificationType.SUCCESS),
        )

    appToast(notifications = sampleNotifications)
}
