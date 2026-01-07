package com.luxury.cheats.features.login.pantalla.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Sección del botón de inicio de sesión.
 * Incluye una animación de redondeo al presionar.
 *
 * @param onLoginClick Callback ejecutado al pulsar el botón.
 * @param modifier Modificador de Compose.
 */
@Composable
fun LoginPantallaButtonSection(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val radius by animateDpAsState(
        if (pressed) 16.dp else 30.dp,
        label = "radius"
    )

    Button(
        onClick = onLoginClick,
        interactionSource = interactionSource,
        modifier = modifier
            .width(240.dp)
            .height(63.dp),
        shape = RoundedCornerShape(radius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),

        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Text(
            text = "Entrar",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
