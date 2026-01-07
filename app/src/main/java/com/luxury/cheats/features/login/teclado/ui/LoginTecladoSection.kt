package com.luxury.cheats.features.login.teclado.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.luxury.cheats.features.login.teclado.logic.TecladoType

private object TecladoConstants {
    const val TRANSITION_DURATION = 150
    const val SCALE_TRANSITION = 0.95f
    const val EMPTY_BOX_HEIGHT = 200
}

/**
 * Acciones para el teclado virtual.
 */
data class TecladoActions(
    val onKeyPress: (String) -> Unit,
    val onDelete: () -> Unit,
    val onToggleCase: () -> Unit,
    val onDone: () -> Unit
)

/**
 * Sección principal del teclado virtual.
 * Orquestador con transiciones ultra fluidas estilo Pixel.
 */
@Composable
fun LoginTecladoSection(
    type: TecladoType,
    isUpperCase: Boolean,
    actions: TecladoActions,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = type != TecladoType.NONE,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        ) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consumimos el clic en los espacios entre teclas */ },
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = 8.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KeyboardTransitionContent(type, isUpperCase, actions)
            }
        }
    }
}

@Composable
private fun KeyboardTransitionContent(
    type: TecladoType,
    isUpperCase: Boolean,
    actions: TecladoActions
) {
    AnimatedContent(
        targetState = type,
        transitionSpec = {
            val isFlowingRight = targetState == TecladoType.NUMERIC
            val fadeTween = tween<Float>(TecladoConstants.TRANSITION_DURATION)
            val springMedium = spring<androidx.compose.ui.unit.IntOffset>(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
            val springSlow = spring<androidx.compose.ui.unit.IntOffset>(
                stiffness = Spring.StiffnessMediumLow
            )

            val enterSlide = slideInHorizontally(
                initialOffsetX = { if (isFlowingRight) it / 2 else -it / 2 },
                animationSpec = springMedium
            )
            val exitSlide = slideOutHorizontally(
                targetOffsetX = { if (isFlowingRight) -it / 2 else it / 2 },
                animationSpec = springSlow
            )

            val enterAni = fadeIn(fadeTween) + enterSlide + 
                scaleIn(initialScale = TecladoConstants.SCALE_TRANSITION)
            val exitAni = fadeOut(fadeTween) + exitSlide + 
                scaleOut(targetScale = TecladoConstants.SCALE_TRANSITION)

            enterAni togetherWith exitAni
        },
        label = "keyboardLayoutTransition"
    ) { targetType ->
        when (targetType) {
            TecladoType.ALPHABETIC -> AlphabeticTeclado(
                isUpperCase = isUpperCase,
                onKeyPress = actions.onKeyPress,
                onDelete = actions.onDelete,
                onToggleCase = actions.onToggleCase
            )
            TecladoType.NUMERIC -> NumericTeclado(
                onKeyPress = actions.onKeyPress,
                onDelete = actions.onDelete
            )
            TecladoType.NONE -> Box(
                Modifier
                    .fillMaxWidth()
                    .height(TecladoConstants.EMPTY_BOX_HEIGHT.dp)
            )
        }
    }
}
