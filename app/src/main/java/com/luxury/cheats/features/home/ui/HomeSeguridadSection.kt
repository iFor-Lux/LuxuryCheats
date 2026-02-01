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
    val infiniteTransition = rememberInfiniteTransition(label = "morph")

    val morphProgressRaw by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(SeguridadConstants.MORPH_DURATION, easing = LinearEasing)
        ),
        label = "morphProgress"
    )

    val rotationRaw by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = SeguridadConstants.ROTATION_END,
        animationSpec = infiniteRepeatable(
            tween(SeguridadConstants.ROTATION_DURATION, easing = LinearEasing)
        ),
        label = "rotation"
    )

    val config = remember(isUnlocked, morphProgressRaw, rotationRaw) {
        val morphProgress = if (isUnlocked) morphProgressRaw else 0f
        SecurityConfig(
            morphProgress = morphProgress,
            rotation = if (isUnlocked) rotationRaw else 0f,
            outerShape = if (isUnlocked) CircleShape else HomeCookieShape,
            innerShape = if (isUnlocked) HomeMorphingShape(morphProgress) else HomeCookieShape,
            sizeOuter = if (isUnlocked) 160.dp else 280.dp,
            sizeInner = if (isUnlocked) 140.dp else 220.dp
        )
    }

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
            SecurityButton(config)
        }
    }
}

private data class SecurityConfig(
    val morphProgress: Float,
    val rotation: Float,
    val outerShape: androidx.compose.ui.graphics.Shape,
    val innerShape: androidx.compose.ui.graphics.Shape,
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
private fun SecurityButton(config: SecurityConfig) {
    Box(
        modifier = Modifier
            .requiredSize(config.sizeInner)
            .graphicsLayer { rotationZ = config.rotation }
            .clip(config.innerShape)
            .background(MaterialTheme.colorScheme.tertiary),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = "Seguridad",
            tint = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier
                .size(SeguridadConstants.ICON_SIZE.dp)
                .graphicsLayer { rotationZ = -config.rotation }
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
