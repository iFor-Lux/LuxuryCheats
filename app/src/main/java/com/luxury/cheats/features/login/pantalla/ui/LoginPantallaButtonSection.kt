package com.luxury.cheats.features.login.pantalla.ui

import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.R

/**
 * Sección del botón de inicio de sesión con sonido de carga integrado.
 */
@Composable
fun LoginPantallaButtonSection(
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    // Gestión de sonido local
    val soundPool = remember {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        SoundPool.Builder().setMaxStreams(1).setAudioAttributes(attributes).build()
    }
    val soundId = remember { soundPool.load(context, R.raw.loading, 1) }

    DisposableEffect(Unit) {
        onDispose { soundPool.release() }
    }

    val radius by animateDpAsState(
        if (pressed) 16.dp else 30.dp,
        label = "radius",
    )

    Button(
        onClick = {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
            onLoginClick()
        },
        interactionSource = interactionSource,
        modifier = modifier.width(240.dp).height(55.dp),
        shape = RoundedCornerShape(radius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
    ) {
        Text(
            text = "Entrar",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
