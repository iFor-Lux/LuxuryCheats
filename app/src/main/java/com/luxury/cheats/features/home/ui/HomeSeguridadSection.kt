package com.luxury.cheats.features.home.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
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

/**
 * Sección de seguridad - Versión ultra-simple sin shapes complejas.
 * Solo usa CircleShape para evitar cualquier problema de recomposición.
 */
@Composable
fun HomeSeguridadSection(
    modifier: Modifier = Modifier,
    isUnlocked: Boolean = false,
    onClick: () -> Unit = {}
) {
    // Interacción
    val interactionSource = remember { MutableInteractionSource() }
    
    // Animación simple de rotación
    val rotation by animateFloatAsState(
        targetValue = if (isUnlocked) 360f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "rotation"
    )
    
    // Tamaños basados en estado
    val outerSize = if (isUnlocked) 160.dp else 280.dp
    val innerSize = if (isUnlocked) 140.dp else 220.dp

    Box(
        modifier = modifier.requiredSize(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .requiredSize(outerSize)
                .clip(CircleShape)
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
                    .border(2.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f), CircleShape)
            )
            
            // Button
            Box(
                modifier = Modifier
                    .requiredSize(innerSize)
                    .graphicsLayer { rotationZ = rotation }
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Seguridad",
                    tint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                        .size(40.dp)
                        .graphicsLayer { rotationZ = -rotation }
                )
            }
        }
    }
}
