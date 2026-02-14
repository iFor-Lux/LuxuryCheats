package com.luxury.cheats.features.login.teclado.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private object KeyConstants {
    const val INITIAL_REPEAT_DELAY = 500L
    const val REPEAT_TICK_DELAY = 60L
    const val KEY_HEIGHT = 48
    const val ROUNDED_CORNER = 12
    const val FONT_SIZE_TEXT = 20
}

/**
 * Configuración visual y de comportamiento para teclas de icono.
 */
data class TecladoKeyStyle(
    val itemColor: Color,
    val contentColor: Color,
    val enableRepeat: Boolean = false,
)

/**
 * Tecla estándar que muestra texto.
 *
 * @param text Texto a mostrar en la tecla.
 * @param onClick Callback al presionar la tecla.
 * @param modifier Modificador de Compose.
 */
@Composable
fun tecladoKey(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(KeyConstants.KEY_HEIGHT.dp),
        shape = RoundedCornerShape(KeyConstants.ROUNDED_CORNER.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = KeyConstants.FONT_SIZE_TEXT.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Tecla que muestra un icono y soporta repetición opcional.
 *
 * @param icon Icono a mostrar.
 * @param onClick Callback al presionar la tecla.
 * @param modifier Modificador de Compose.
 * @param style Estilo y comportamiento de la tecla.
 */
@Composable
fun tecladoIconKey(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: TecladoKeyStyle =
        TecladoKeyStyle(
            itemColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Lógica de repetición cuando está presionado
    if (style.enableRepeat && isPressed) {
        LaunchedEffect(Unit) {
            onClick() // Borrado inicial (1 caracter)
            delay(KeyConstants.INITIAL_REPEAT_DELAY) // Delay inicial antes de ráfaga
            while (true) {
                onClick()
                delay(KeyConstants.REPEAT_TICK_DELAY) // Ráfaga rápida
            }
        }
    }

    Surface(
        onClick = { if (!style.enableRepeat) onClick() },
        modifier = modifier.height(48.dp),
        interactionSource = interactionSource,
        shape = RoundedCornerShape(12.dp),
        color = style.itemColor,
        tonalElevation = 2.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = style.contentColor,
            )
        }
    }
}
