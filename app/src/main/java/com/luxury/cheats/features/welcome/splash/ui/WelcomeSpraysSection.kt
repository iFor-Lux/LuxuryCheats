package com.luxury.cheats.features.welcome.splash.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
fun welcomeSpraysSection(modifier: Modifier = Modifier) {
    val density = LocalDensity.current

    val windowInfo = LocalWindowInfo.current

    Box(modifier = modifier.fillMaxSize()) {
        val containerSize: IntSize = windowInfo.containerSize
        val widthDp = with(density) { containerSize.width.toDp() }
        val heightDp = with(density) { containerSize.height.toDp() }
        val spriteSize = 200.dp

        // Sprite 1: Superior izquierda
        spraySprite(
            drawableRes = R.drawable.sprit1,
            size = spriteSize,
            offsetX = 3.dp,
            offsetY = 40.dp,
            animationOffset = WelcomeSpraysAnimations.getSprite1AnimationOffset(),
        )

        // Sprite 2: Centro derecha
        spraySprite(
            drawableRes = R.drawable.sprit2,
            size = spriteSize,
            offsetX = widthDp * 0.60f,
            offsetY = heightDp * 0.25f,
            animationOffset = WelcomeSpraysAnimations.getSprite2AnimationOffset(),
        )

        // Sprite 3: Inferior izquierda
        spraySprite(
            drawableRes = R.drawable.sprit3,
            size = spriteSize,
            offsetX = 5.dp,
            offsetY = heightDp * 0.70f,
            animationOffset = WelcomeSpraysAnimations.getSprite3AnimationOffset(),
        )
    }
}

@Composable
private fun spraySprite(
    drawableRes: Int,
    size: Dp,
    offsetX: Dp,
    offsetY: Dp,
    animationOffset: Pair<Dp, Dp>,
) {
    AsyncImage(
        model =
            ImageRequest.Builder(LocalContext.current)
                .data(drawableRes)
                .crossfade(true)
                .build(),
        contentDescription = null,
        modifier =
            Modifier
                .size(size)
                .offset(
                    x = offsetX + animationOffset.first,
                    y = offsetY + animationOffset.second,
                ),
        contentScale = ContentScale.Fit,
    )
}
