package com.luxury.cheats.core.ui

import android.media.AudioAttributes
import android.media.SoundPool
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.luxury.cheats.R

private object ToastConstants {
    const val MAX_VISIBLE = 4
    const val OFFSET_STEP = 12
    const val SCALE_DEC = 0.05f
    const val ALPHA_BASE = 0.95f
    const val ALPHA_DEC = 0.15f
    const val Z_INDEX_BASE = 10f
    const val MAX_WIDTH = 300
    const val GLASS_ALPHA_LIGHT = 0.85f
    const val GLASS_ALPHA_DARK = 0.75f
    const val BORDER_ALPHA = 0.1f
    const val ENTRY_ANIM_START_OFFSET = 40f
    const val WARNING_CONTAINER_LIGHT = 0xFFFFF4E5
    const val WARNING_CONTAINER_DARK = 0xFF332B00
    const val WARNING_CONTENT_LIGHT = 0xFF663C00
    const val WARNING_CONTENT_DARK = 0xFFFFE082
    const val SUCCESS_CONTAINER_LIGHT = 0xFFE8F5E9
    const val SUCCESS_CONTAINER_DARK = 0xFF00390A
    const val SUCCESS_CONTENT_LIGHT = 0xFF2E7D32
    const val SUCCESS_CONTENT_DARK = 0xFFA5D6A7
}

private data class ToastTheme(
    val containerColor: Color,
    val contentColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

/**
 * Componente Toast con sonido de notificación integrado.
 */
@Composable
fun AppToast(
    notifications: List<AppNotification>,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    
    // Gestión de sonido local
    val soundPool = remember {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        SoundPool.Builder().setMaxStreams(2).setAudioAttributes(attributes).build()
    }
    val soundId = remember { soundPool.load(context, R.raw.notify, 1) }

    DisposableEffect(Unit) {
        onDispose { soundPool.release() }
    }

    val playedIds = remember { mutableSetOf<Long>() }

    // Sonido automático solo al recibir NUEVAS notificaciones
    LaunchedEffect(notifications) {
        val currentIds = notifications.map { it.id }
        val hasNew = currentIds.any { it !in playedIds }

        if (hasNew) {
            soundPool.play(soundId, 0.5f, 0.5f, 1, 0, 1.0f)
            playedIds.addAll(currentIds)
        }

        // Limpiar IDs antiguos para optimizar memoria
        playedIds.retainAll(currentIds.toSet())
    }


    Box(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        val displayed = notifications.takeLast(ToastConstants.MAX_VISIBLE).reversed()
        displayed.forEachIndexed { index, notification ->
            ToastItem(index = index, notification = notification, isTop = index == 0)
        }
    }
}

@Composable
private fun ToastItem(index: Int, notification: AppNotification, isTop: Boolean) {
    val springSpec = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    val entryProgress by animateFloatAsState(targetValue = 1f, animationSpec = springSpec, label = "entry")
    val scale by animateFloatAsState(targetValue = (if (isTop) 1f else 1f - index * ToastConstants.SCALE_DEC) * entryProgress, animationSpec = springSpec)
    val alphaTarget = if (isTop) entryProgress else (ToastConstants.ALPHA_BASE - index * ToastConstants.ALPHA_DEC).coerceAtLeast(0f) * entryProgress
    val alpha by animateFloatAsState(targetValue = alphaTarget, animationSpec = springSpec)
    val yOffset by animateFloatAsState(targetValue = (index * ToastConstants.OFFSET_STEP).toFloat() - (1f - entryProgress) * ToastConstants.ENTRY_ANIM_START_OFFSET, animationSpec = springSpec)

    ToastCard(
        notification = notification,
        isTop = isTop,
        modifier = Modifier.offset(y = yOffset.dp).scale(scale).alpha(alpha).zIndex(ToastConstants.Z_INDEX_BASE - index),
    )
}

@Composable
private fun ToastCard(notification: AppNotification, isTop: Boolean, modifier: Modifier = Modifier) {
    val isDark = isSystemInDarkTheme()
    val theme = getNotificationTheme(notification.type, isDark)
    val shape = RoundedCornerShape(24.dp)
    val glassAlpha = if (isDark) ToastConstants.GLASS_ALPHA_DARK else ToastConstants.GLASS_ALPHA_LIGHT

    Surface(
        modifier = modifier.fillMaxWidth().widthIn(max = ToastConstants.MAX_WIDTH.dp).shadow(elevation = if (isTop) 8.dp else 0.dp, shape = shape).clip(shape).background(theme.containerColor.copy(alpha = glassAlpha)).border(0.5.dp, theme.contentColor.copy(alpha = ToastConstants.BORDER_ALPHA), shape = shape),
        color = Color.Transparent,
        contentColor = theme.contentColor,
    ) {
        ToastCardContent(notification.message, theme.icon, theme.contentColor, isTop)
    }
}

@Composable
private fun getNotificationTheme(type: NotificationType, isDark: Boolean): ToastTheme {
    val colorScheme = MaterialTheme.colorScheme
    return when (type) {
        NotificationType.ERROR -> ToastTheme(colorScheme.errorContainer, colorScheme.onErrorContainer, Icons.Default.Warning)
        NotificationType.WARNING -> ToastTheme(if (isDark) Color(ToastConstants.WARNING_CONTAINER_DARK) else Color(ToastConstants.WARNING_CONTAINER_LIGHT), if (isDark) Color(ToastConstants.WARNING_CONTENT_DARK) else Color(ToastConstants.WARNING_CONTENT_LIGHT), Icons.Default.Info)
        NotificationType.SUCCESS -> ToastTheme(if (isDark) Color(ToastConstants.SUCCESS_CONTAINER_DARK) else Color(ToastConstants.SUCCESS_CONTAINER_LIGHT), if (isDark) Color(ToastConstants.SUCCESS_CONTENT_DARK) else Color(ToastConstants.SUCCESS_CONTENT_LIGHT), Icons.Default.CheckCircle)
        NotificationType.INFO -> ToastTheme(colorScheme.secondaryContainer, colorScheme.onSecondaryContainer, Icons.Default.Info)
    }
}

@Composable
private fun ToastCardContent(message: String, icon: androidx.compose.ui.graphics.vector.ImageVector, contentColor: Color, isVisible: Boolean) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp).alpha(if (isVisible) 1f else 0f), verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = contentColor.copy(alpha = 0.8f))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = message, style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp, letterSpacing = 0.4.sp), color = contentColor, fontWeight = FontWeight.Medium, textAlign = TextAlign.Start, modifier = Modifier.weight(1f))
    }
}
