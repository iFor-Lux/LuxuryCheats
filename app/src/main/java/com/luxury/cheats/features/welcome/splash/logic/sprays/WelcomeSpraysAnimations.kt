package com.luxury.cheats.features.welcome.splash.logic.sprays

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Lógica de animaciones para los sprites decorativos
 * - Cada sprite entra desde fuera de la pantalla y se detiene en su posición
 * - Animación única (no infinita) - se ejecuta una sola vez
 * - Separación de lógica de UI (cumple AGENTS.md)
 */
// Constantes de animación para Detekt
private const val OFF_SCREEN_OFFSET = 200f
private const val OFF_SCREEN_OFFSET_ALT = 150f
private const val SPRAY1_DURATION = 1500
private const val SPRAY2_DURATION = 1800
private const val SPRAY3_DURATION = 2000

/**
 * Lógica de animaciones para los sprites decorativos
 */
object WelcomeSpraysAnimations {
    
    /**
     * Animación completa para Sprite 1 (superior izquierda)
     */
    @Composable
    fun getSprite1AnimationOffset(): Pair<Dp, Dp> {
        var startAnimation by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            startAnimation = true
        }
        
        val animationProgress by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(
                durationMillis = SPRAY1_DURATION,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            ),
            label = "sprite1_animation"
        )
        
        val startOffsetX = -OFF_SCREEN_OFFSET
        val startOffsetY = -OFF_SCREEN_OFFSET
        
        val offsetX = startOffsetX * (1f - animationProgress)
        val offsetY = startOffsetY * (1f - animationProgress)
        
        return offsetX.dp to offsetY.dp
    }
    
    /**
     * Animación completa para Sprite 2 (mitad derecha)
     */
    @Composable
    fun getSprite2AnimationOffset(): Pair<Dp, Dp> {
        var startAnimation by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            startAnimation = true
        }
        
        val animationProgress by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(
                durationMillis = SPRAY2_DURATION,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            ),
            label = "sprite2_animation"
        )
        
        val startOffsetX = OFF_SCREEN_OFFSET
        val startOffsetY = -OFF_SCREEN_OFFSET_ALT
        
        val offsetX = startOffsetX * (1f - animationProgress)
        val offsetY = startOffsetY * (1f - animationProgress)
        
        return offsetX.dp to offsetY.dp
    }
    
    /**
     * Animación completa para Sprite 3 (inferior izquierda)
     */
    @Composable
    fun getSprite3AnimationOffset(): Pair<Dp, Dp> {
        var startAnimation by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            startAnimation = true
        }
        
        val animationProgress by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(
                durationMillis = SPRAY3_DURATION,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            ),
            label = "sprite3_animation"
        )
        
        val startOffsetX = -OFF_SCREEN_OFFSET
        val startOffsetY = OFF_SCREEN_OFFSET
        
        val offsetX = startOffsetX * (1f - animationProgress)
        val offsetY = startOffsetY * (1f - animationProgress)
        
        return offsetX.dp to offsetY.dp
    }
}


