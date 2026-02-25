@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.luxury.cheats.features.welcome.page1.bienvenida.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.R

private object ButtonConstants {
    val HEIGHT = 66.dp
    val HORIZONTAL_PADDING = 32.dp
    val PRESSED_RADIUS = 16.dp
    val DEFAULT_RADIUS = 30.dp
    val BORDER_WIDTH = 1.dp
    val FONT_SIZE = 24.sp
    const val PRESSED_SCALE = 0.95f
    const val DEFAULT_SCALE = 1f
}

/**
 * Sección del botón principal de la primera página de bienvenida.
 * Actualizado con interactividad Material 3 Expressive (rebotes y escala física).
 *
 * @param onNavigateNext Callback para navegar a la siguiente página.
 * @param modifier Modificador de Compose.
 */
@Composable
fun welcomePage1ButtonSection(
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    // Animación de morphing del radio con física de resorte
    val radius by animateDpAsState(
        targetValue = if (pressed) ButtonConstants.PRESSED_RADIUS else ButtonConstants.DEFAULT_RADIUS,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "radiusExpressive",
    )

    // Animación de escala sutil al presionar (M3 Expressive feel)
    val scale by animateFloatAsState(
        targetValue = if (pressed) ButtonConstants.PRESSED_SCALE else ButtonConstants.DEFAULT_SCALE,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
        label = "scaleExpressive",
    )

    Button(
        onClick = onNavigateNext,
        interactionSource = interactionSource,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = ButtonConstants.HORIZONTAL_PADDING)
                .height(ButtonConstants.HEIGHT)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
        shape = RoundedCornerShape(radius),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        border = BorderStroke(ButtonConstants.BORDER_WIDTH, MaterialTheme.colorScheme.tertiary),
    ) {
        Text(
            text = stringResource(R.string.welcome_page1_start),
            fontSize = ButtonConstants.FONT_SIZE,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
