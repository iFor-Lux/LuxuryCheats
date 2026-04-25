package com.luxury.cheats.features.login.teclado.ui

import android.media.AudioAttributes
import android.media.SoundPool
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.R
import kotlinx.coroutines.delay

private object KeyConstants {
    const val INITIAL_REPEAT_DELAY = 500L
    const val REPEAT_TICK_DELAY = 60L
    const val KEY_HEIGHT = 48
    const val ROUNDED_CORNER = 12
    const val FONT_SIZE_TEXT = 20
}

/**
 * Proveedor local para el sonido del teclado, compartido entre todas las teclas.
 */
val LocalTecladoSound = staticCompositionLocalOf<(() -> Unit)?> { null }

/**
 * Componente raíz que inicializa el sonido para el teclado.
 * Debe envolver a NumericTeclado, SymbolsTeclado, etc.
 */
@Composable
fun TecladoSoundProvider(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val soundPool = remember {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        SoundPool.Builder().setMaxStreams(5).setAudioAttributes(attributes).build()
    }
    val soundId = remember { soundPool.load(context, R.raw.key, 1) }
    
    val play = remember { 
        { 
            soundPool.play(soundId, 0.3f, 0.3f, 1, 0, 1.0f)
            Unit
        }
    }


    DisposableEffect(Unit) {
        onDispose { soundPool.release() }
    }

    androidx.compose.runtime.CompositionLocalProvider(LocalTecladoSound provides play) {
        content()
    }
}

data class TecladoKeyStyle(
    val itemColor: Color,
    val contentColor: Color,
    val enableRepeat: Boolean = false,
)

@Composable
fun TecladoKey(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color? = null,
    contentColor: Color? = null,
) {
    val playSound = LocalTecladoSound.current
    val defaultColor = if (text == " ") MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceVariant
    val defaultContentColor = MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        onClick = {
            playSound?.invoke()
            onClick()
        },
        modifier = modifier.height(KeyConstants.KEY_HEIGHT.dp),
        shape = RoundedCornerShape(KeyConstants.ROUNDED_CORNER.dp),
        color = color ?: defaultColor,
        tonalElevation = 2.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = if (text == " ") "Espacio" else text,
                fontSize = if (text == " ") 14.sp else KeyConstants.FONT_SIZE_TEXT.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor ?: defaultContentColor,
            )
        }
    }
}

@Composable
fun TecladoIconKey(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: TecladoKeyStyle = TecladoKeyStyle(
        itemColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ),
) {
    val playSound = LocalTecladoSound.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    if (style.enableRepeat && isPressed) {
        LaunchedEffect(Unit) {
            playSound?.invoke()
            onClick()
            delay(KeyConstants.INITIAL_REPEAT_DELAY)
            while (true) {
                playSound?.invoke()
                onClick()
                delay(KeyConstants.REPEAT_TICK_DELAY)
            }
        }
    }

    Surface(
        onClick = {
            if (!style.enableRepeat) {
                playSound?.invoke()
                onClick()
            }
        },
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
