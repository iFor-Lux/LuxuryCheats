package com.luxury.cheats.features.welcome.page1.bienvenida.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
}

/**
 * Sección del botón principal de la primera página de bienvenida.
 *
 * @param onNavigateNext Callback para navegar a la siguiente página.
 * @param modifier Modificador de Compose.
 */
@Composable
fun WelcomePage1ButtonSection(
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val radius by animateDpAsState(
        if (pressed) ButtonConstants.PRESSED_RADIUS else ButtonConstants.DEFAULT_RADIUS,
        label = "radius"
    )

    Button(
        onClick = onNavigateNext,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ButtonConstants.HORIZONTAL_PADDING)
            .height(ButtonConstants.HEIGHT),
        shape = RoundedCornerShape(radius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(ButtonConstants.BORDER_WIDTH, MaterialTheme.colorScheme.tertiary)
    ) {
        Text(
            text = stringResource(R.string.welcome_page1_start),
            fontSize = ButtonConstants.FONT_SIZE,
            fontWeight = FontWeight.SemiBold
        )
    }
}


