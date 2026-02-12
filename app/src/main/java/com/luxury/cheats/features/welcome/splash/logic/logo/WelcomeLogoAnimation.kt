package com.luxury.cheats.features.welcome.splash.logic.logo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * Lógica de animación para el logo
 * - Animación de "salto hacia adelante" cuando aparece (como abrir los ojos)
 * - Viene de atrás y salta hacia adelante
 * - Separación de lógica de UI (cumple AGENTS.md)
 */
// Constantes de animación para Detekt
private const val LOGO_ANIM_DURATION = 450
private const val LOGO_JUMP_TIME = 250
private const val SCALE_INITIAL = 0.8f
private const val SCALE_PEAK = 1.08f
private const val SCALE_FINAL = 1.0f

/**
 * Lógica de animación para el logo
 */
object WelcomeLogoAnimation {
    
    /**
     * Animación de escala para el logo
     */
    @Composable
    fun getLogoScaleAnimation(isReady: Boolean): Float {
        // Usar Animatable para mayor control
        val scale = remember { androidx.compose.animation.core.Animatable(SCALE_INITIAL) }

        LaunchedEffect(isReady) {
            if (isReady) {
                scale.animateTo(
                    targetValue = SCALE_FINAL,
                    animationSpec = keyframes {
                        durationMillis = LOGO_ANIM_DURATION
                        SCALE_INITIAL at 0
                        SCALE_PEAK at LOGO_JUMP_TIME
                        SCALE_FINAL at LOGO_ANIM_DURATION
                    }
                )
            }
        }

        return scale.value
    }

    
    /**
     * Animación de entrada para el texto "LUXURY"
     * - Fade in + slide up desde abajo
     * - Se activa después de que el logo esté listo (con delay)
     * @param isLogoReady Si el logo está listo
     * @param animationDelay Delay adicional antes de iniciar (en ms)
     * @param animationDuration Duración de la animación (en ms)
     * @param initialOffsetY Offset Y inicial para el slide up
     */
    @Composable
    fun getLuxuryTextAnimation(
        isLogoReady: Boolean,
        animationDelay: Long = 0,
        animationDuration: Int = 600,
        initialOffsetY: Float = 30f
    ): Pair<Float, Float> {
        var startAnimation by remember { mutableStateOf(false) }
        
        // Activar animación cuando el logo esté listo (con delay)
        LaunchedEffect(isLogoReady) {
            if (isLogoReady && !startAnimation) {
                if (animationDelay > 0) {
                    delay(animationDelay)
                }
                startAnimation = true
            }
        }
        
        // Animación de alpha (fade in)
        val alpha by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            ),
            label = "luxury_text_alpha"
        )
        
        // Animación de offset Y (slide up)
        val offsetY by animateFloatAsState(
            targetValue = if (startAnimation) 0f else initialOffsetY,
            animationSpec = tween(
                durationMillis = animationDuration,
                easing = FastOutSlowInEasing
            ),
            label = "luxury_text_offset"
        )
        
        return alpha to offsetY
    }
}

