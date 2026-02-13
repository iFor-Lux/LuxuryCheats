package com.luxury.cheats.features.home.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.luxury.cheats.features.home.ui.shapes.HomeCookieShape
import com.luxury.cheats.features.home.ui.shapes.HomeMorphingShape

private object SeguridadConstants {
    const val MORPH_DURATION = 10000
    const val ROTATION_DURATION = 4000
    const val ROTATION_END = 360f
    const val ICON_SIZE = 40
}

/**
 * Sección de seguridad refactorizada.
 */
@Composable
fun HomeSeguridadSection(
    modifier: Modifier = Modifier,
    isUnlocked: Boolean = false,
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    // Animación simple de rotación solo cuando está desbloqueado
    val rotation by animateFloatAsState(
        targetValue = if (isUnlocked) 360f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "rotation"
    )
    
    // Shapes estáticas - sin morphing para evitar recomposiciones infinitas
    val outerCookieShape = remember { HomeCookieShape() }
    val innerCookieShape = remember { HomeCookieShape() }

    // Valores directos sin data class para máxima estabilidad
    val outerShape = remember(isUnlocked) { if (isUnlocked) CircleShape else outerCookieShape }
    val innerShape = remember(isUnlocked) { if (isUnlocked) CircleShape else innerCookieShape }
    val sizeOuter = remember(isUnlocked) { if (isUnlocked) 160.dp else 280.dp }
    val sizeInner = remember(isUnlocked) { if (isUnlocked) 140.dp else 220.dp }

    Box(
        modifier = modifier.requiredSize(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .requiredSize(sizeOuter)
                .clip(outerShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = androidx.compose.material3.ripple(bounded = true),
                    onClick = onClick,
                    enabled = !isUnlocked
                ),
            contentAlignment = Alignment.Center
        ) {
            // Background
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f))
                    .border(2.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f), outerShape)
            )
            
            // Button
            Box(
                modifier = Modifier
                    .requiredSize(sizeInner)
                    .graphicsLayer { rotationZ = rotation }
                    .clip(innerShape)
                    .background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Seguridad",
                    tint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                        .size(SeguridadConstants.ICON_SIZE.dp)
                        .graphicsLayer { rotationZ = -rotation }
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, backgroundColor = 0xFF1C1B1F)
@Composable
internal fun HomeSeguridadSectionPreview() {
    MaterialTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(24.dp)
        ) {
            HomeSeguridadSection(isUnlocked = false)
            HomeSeguridadSection(isUnlocked = true)
        }
    }
}
