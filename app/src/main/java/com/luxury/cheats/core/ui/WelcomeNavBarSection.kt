package com.luxury.cheats.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Componente de barra de navegación para el flujo de bienvenida.
 * Permite navegar hacia adelante, atrás y muestra el título de la página actual.
 *
 * @param currentPage Título o identificador de la página actual.
 * @param onBack Callback para navegar atrás.
 * @param onNext Callback para navegar adelante.
 * @param modifier Modificador de Compose.
 */
@Composable
fun welcomeNavBarSection(
    currentPage: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    isNextEnabled: Boolean = true,
) {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface

    // Interaction sources to detect pressure
    val backInteractionSource = remember { MutableInteractionSource() }
    val nextInteractionSource = remember { MutableInteractionSource() }
    val backPressed by backInteractionSource.collectIsPressedAsState()
    val nextPressed by nextInteractionSource.collectIsPressedAsState()

    // Spring animation for the "push" effect on the central text
    val springSpec = spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    val textOffset by animateFloatAsState(
        targetValue =
            when {
                backPressed -> 20f // Empuja a la derecha
                nextPressed -> -20f // Empuja a la izquierda
                else -> 0f
            },
        animationSpec = springSpec,
        label = "TextPushOffset",
    )

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(42.dp))
                .background(containerColor)
                .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        welcomeNavBarContent(
            currentPage = currentPage,
            theme = WelcomeNavBarTheme(textOffset, textColor),
            backAction = WelcomeButtonState(onBack, backInteractionSource),
            nextAction = WelcomeButtonState(onNext, nextInteractionSource, isNextEnabled),
        )
    }
}

@Composable
private fun welcomeNavBarContent(
    currentPage: String,
    theme: WelcomeNavBarTheme,
    backAction: WelcomeButtonState,
    nextAction: WelcomeButtonState,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        welcomeNavBarButton(
            params = WelcomeButtonParams(
                text = "Back",
                onClick = backAction.onClick,
                interactionSource = backAction.interactionSource,
                style = WelcomeNavBarButtonStyle(
                    containerColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                    borderColor = MaterialTheme.colorScheme.outline,
                    contentColor = theme.textColor,
                ),
            ),
            modifier = Modifier.width(106.dp),
        )

        welcomePageTicker(currentPage, theme.textOffset, theme.textColor)

        welcomeNavBarButton(
            params = WelcomeButtonParams(
                text = "Siguiente",
                onClick = nextAction.onClick,
                interactionSource = nextAction.interactionSource,
                enabled = nextAction.enabled,
                style = WelcomeNavBarButtonStyle(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    borderColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = theme.textColor,
                ),
            ),
            modifier = Modifier.width(125.dp),
        )
    }
}

private data class WelcomeNavBarTheme(
    val textOffset: Float,
    val textColor: androidx.compose.ui.graphics.Color,
)

private data class WelcomeButtonState(
    val onClick: () -> Unit,
    val interactionSource: MutableInteractionSource,
    val enabled: Boolean = true,
)

/**
 * Parámetros para configurar el botón de la barra de navegación.
 */
private data class WelcomeButtonParams(
    val text: String,
    val onClick: () -> Unit,
    val style: WelcomeNavBarButtonStyle,
    val enabled: Boolean = true,
    val interactionSource: MutableInteractionSource? = null,
)

@Composable
private fun welcomePageTicker(
    currentPage: String,
    textOffset: Float,
    textColor: androidx.compose.ui.graphics.Color,
) {
    AnimatedContent(
        targetState = currentPage,
        modifier = Modifier.graphicsLayer { translationX = textOffset },
        transitionSpec = {
            (slideInVertically { height -> -height } + fadeIn())
                .togetherWith(slideOutVertically { height -> height } + fadeOut())
                .using(SizeTransform(clip = false))
        },
        label = "PageIndicatorAnimation",
    ) { targetPage ->
        Text(
            text = targetPage,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

/**
 * Botón interno para la barra de navegación de bienvenida.
 * @param params Parámetros de configuración del botón.
 * @param modifier Modificador de Compose para el layout.
 */
@Composable
private fun welcomeNavBarButton(
    params: WelcomeButtonParams,
    modifier: Modifier = Modifier,
) {
    val interactionSource = params.interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val style = params.style

    // Resorte táctico (MD3 Expressive)
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "ButtonScale",
    )

    Button(
        onClick = params.onClick,
        interactionSource = interactionSource,
        enabled = params.enabled,
        modifier =
            modifier
                .height(46.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .border(1.dp, style.borderColor, RoundedCornerShape(30.dp)),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = style.containerColor,
                disabledContainerColor = style.containerColor.copy(alpha = 0.5f),
                disabledContentColor = style.contentColor.copy(alpha = 0.5f),
            ),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(30.dp),
    ) {
        Text(text = params.text, color = style.contentColor, fontSize = 16.sp)
    }
}
