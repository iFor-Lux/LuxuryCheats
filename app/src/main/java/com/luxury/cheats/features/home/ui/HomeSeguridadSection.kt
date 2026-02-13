package com.luxury.cheats.features.home.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
    
    // Optimización 1: Solo ejecutar animaciones si está desbloqueado
    // Si no, usamos valores estáticos para evitar recomposición infinita
    val morphProgress: Float
    val rotation: Float
    
    if (isUnlocked) {
        val infiniteTransition = rememberInfiniteTransition(label = "morph")
        
        val morphProgressAnim by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(SeguridadConstants.MORPH_DURATION, easing = LinearEasing)
            ),
            label = "morphProgress"
        )
        morphProgress = morphProgressAnim

        val rotationAnim by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = SeguridadConstants.ROTATION_END,
            animationSpec = infiniteRepeatable(
                tween(SeguridadConstants.ROTATION_DURATION, easing = LinearEasing)
            ),
            label = "rotation"
        )
        rotation = rotationAnim
    } else {
        morphProgress = 0f
        rotation = 0f
    }

    // Instancias de shapes con caché independiente
    val outerCookieShape = remember { HomeCookieShape() }
    val innerCookieShape = remember { HomeCookieShape() }

    // Optimización 2: Configuración estable (sin valores animados)
    // Esto evita que 'config' cambie en cada frame
    val config = remember(isUnlocked) {
        SecurityConfig(
            outerShape = if (isUnlocked) CircleShape else outerCookieShape,
            sizeOuter = if (isUnlocked) 160.dp else 280.dp,
            sizeInner = if (isUnlocked) 140.dp else 220.dp
        )
    }
    
    // Shape dinámico solo si es necesario (el único que depende de progreso)
    val innerShape = if (isUnlocked) HomeMorphingShape(morphProgress) else innerCookieShape

    Box(
        modifier = modifier.requiredSize(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .requiredSize(config.sizeOuter)
                .clip(config.outerShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = androidx.compose.material3.ripple(bounded = true),
                    onClick = onClick,
                    enabled = !isUnlocked
                ),
            contentAlignment = Alignment.Center
        ) {
            SecurityBackground(config.outerShape)
            SecurityButton(
                config = config, 
                innerShape = innerShape,
                rotation = rotation
            )
        }
    }
}

// Optimización 3: Data class inmutable y estable
private data class SecurityConfig(
    val outerShape: androidx.compose.ui.graphics.Shape,
    val sizeOuter: androidx.compose.ui.unit.Dp,
    val sizeInner: androidx.compose.ui.unit.Dp
)

@Composable
private fun BoxScope.SecurityBackground(shape: androidx.compose.ui.graphics.Shape) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f))
            .border(2.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f), shape)
    )
}

@Composable
private fun SecurityButton(
    config: SecurityConfig,
    innerShape: androidx.compose.ui.graphics.Shape,
    rotation: Float
) {
    Box(
        modifier = Modifier
            .requiredSize(config.sizeInner)
            // Optimización 4: Rotación en layer (GPU) sin recomposición
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
                // Contra-rotación también en layer
                .graphicsLayer { rotationZ = -rotation }
        )
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
