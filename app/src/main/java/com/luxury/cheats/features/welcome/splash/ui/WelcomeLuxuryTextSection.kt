package com.luxury.cheats.features.welcome.splash.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.features.welcome.splash.logic.logo.WelcomeLogoAnimation
import kotlinx.coroutines.delay

/**
 * Sección de texto "LUXURY"
 * - Texto centrado en la pantalla
 * - Animación de entrada (fade in + slide up)
 * - Se activa automáticamente después de un delay
 * - No intercepta toques (solo visual)
 *
 * ═══════════════════════════════════════════════════════════════
 * ⚙️ CONFIGURACIÓN - Edita los valores por defecto aquí abajo:
 * ═══════════════════════════════════════════════════════════════
 */
@Composable
fun WelcomeLuxuryTextSection(
    modifier: Modifier = Modifier,
    text: String = "LUXURY",
    style: WelcomeTextStyle = WelcomeTextStyle(),
    animationDelay: Long = 1800,
) {

    // Estado para activar la animación
    // Esto se activa automáticamente después de un delay inicial
    var isLogoReady by remember { mutableStateOf(false) }

    // Activar automáticamente después de un delay inicial
    LaunchedEffect(Unit) {
        delay(animationDelay)
        isLogoReady = true
    }

    // Animación de entrada para el texto
    val (luxuryAlpha, luxuryOffsetY) = WelcomeLogoAnimation.getLuxuryTextAnimation(
        isLogoReady = isLogoReady,
        animationDelay = 0, // Ya aplicamos el delay arriba
        animationDuration = style.animationDuration,
        initialOffsetY = style.initialOffsetY
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = style.fontSize.sp,
                fontWeight = style.fontWeight,
                letterSpacing = style.letterSpacing.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .offset(y = 220.dp * (style.verticalPosition - 0.5f)) // Ajusta posición vertical
                .alpha(luxuryAlpha)
                .offset(y = luxuryOffsetY.dp)
        )
    }
}

/**
 * Parámetros de estilo y animación para el texto de bienvenida
 */
data class WelcomeTextStyle(
    val fontSize: Int = 48,
    val letterSpacing: Int = 8,
    val fontWeight: FontWeight = FontWeight.Bold,
    val initialOffsetY: Float = 30f,
    val verticalPosition: Float = 1.2f,
    val animationDuration: Int = 600
)


