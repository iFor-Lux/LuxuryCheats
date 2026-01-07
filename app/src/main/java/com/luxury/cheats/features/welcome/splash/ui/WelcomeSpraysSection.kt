package com.luxury.cheats.features.welcome.splash.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.luxury.cheats.R
import com.luxury.cheats.features.welcome.splash.logic.sprays.WelcomeSpraysAnimations

/**
 * Sección de Sprays decorativos
 * - Muestra sprites decorativos en el background
 * - Cada sprite tiene su propia animación de movimiento
 * - No intercepta toques (solo visual)
 * - Diseño premium y minimalista
 * - Lógica de animaciones separada en WelcomeSpraysAnimations
 */
@Composable
fun WelcomeSpraysSection(
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthDp = with(density) { constraints.maxWidth.toDp() }
        val heightDp = with(density) { constraints.maxHeight.toDp() }
        val spriteSize = 200.dp
        
        // Sprite 1: Superior izquierda
        SpraySprite(
            drawableRes = R.drawable.sprit1,
            size = spriteSize,
            offsetX = 3.dp,
            offsetY = 40.dp,
            animationOffset = WelcomeSpraysAnimations.getSprite1AnimationOffset()
        )
        
        // Sprite 2: Centro derecha
        SpraySprite(
            drawableRes = R.drawable.sprit2,
            size = spriteSize,
            offsetX = widthDp * 0.60f,
            offsetY = heightDp * 0.25f,
            animationOffset = WelcomeSpraysAnimations.getSprite2AnimationOffset()
        )
        
        // Sprite 3: Inferior izquierda
        SpraySprite(
            drawableRes = R.drawable.sprit3,
            size = spriteSize,
            offsetX = 5.dp,
            offsetY = heightDp * 0.70f,
            animationOffset = WelcomeSpraysAnimations.getSprite3AnimationOffset()
        )
    }
}

@Composable
private fun SpraySprite(
    drawableRes: Int,
    size: androidx.compose.ui.unit.Dp,
    offsetX: androidx.compose.ui.unit.Dp,
    offsetY: androidx.compose.ui.unit.Dp,
    animationOffset: Pair<androidx.compose.ui.unit.Dp, androidx.compose.ui.unit.Dp>
) {
    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .offset(
                x = offsetX + animationOffset.first,
                y = offsetY + animationOffset.second
            ),
        contentScale = ContentScale.Fit
    )
}


