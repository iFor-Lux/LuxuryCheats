@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.luxury.cheats.features.welcome.page1.bienvenida.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.R

/**
 * Botón de Lenguaje (Top Right) usando Material Design 3 Expressive (Diseño Suavizado)
 */
private object LanguageConstants {
    val TOP_PADDING = 60.dp
    val HORIZONTAL_PADDING = 16.dp
    val FONT_SIZE = 14.sp

    const val PERCENT_50 = 50
    const val MENU_ITEM_WIDTH = 140
    const val MENU_ITEM_HEIGHT = 36
    const val MENU_PADDING = 4
    val MENU_SPACING = 8.dp

    // Escalas de presión más sutiles
    const val PRESSED_SCALE = 0.95f
    const val COLLISION_SCALE = 0.98f
    const val COLLISION_TRANSLATION_DP = 6f

    // Oscilación mínima
    const val JIGGLE_ANGLE = 1f
}

/** Configuración visual para el botón de lenguaje. */
private data class LanguageButtonColors(
    val container: Color,
    val content: Color,
)

/** Estado de interacción compartido para el botón. */
private data class LanguageButtonInteraction(
    val interactionSource: MutableInteractionSource,
    val isPressed: Boolean,
    val isOtherPressed: Boolean,
)

/** Estado compartido para la lógica de lenguaje. */
private data class LanguageState(
    val isExpanded: Boolean,
    val selectedLanguage: String,
    val onExpandedChange: (Boolean) -> Unit,
    val onLanguageSelected: (String) -> Unit,
)

/**
 * Sección de selección de idioma para la primera página de bienvenida.
 * @param modifier Modificador de layout.
 */
@Composable
fun WelcomePage1LanguageSection(
    isExpanded: Boolean,
    selectedLanguage: String,
    onExpandedChange: (Boolean) -> Unit,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors =
        LanguageButtonColors(
            container = MaterialTheme.colorScheme.surfaceContainer,
            content = MaterialTheme.colorScheme.onSurface,
        )

    val leadingInteractionSource = remember { MutableInteractionSource() }
    val trailingInteractionSource = remember { MutableInteractionSource() }

    val leadingInt =
        LanguageButtonInteraction(
            interactionSource = leadingInteractionSource,
            isPressed = leadingInteractionSource.collectIsPressedAsState().value,
            isOtherPressed = trailingInteractionSource.collectIsPressedAsState().value,
        )
    val trailingInt =
        LanguageButtonInteraction(
            interactionSource = trailingInteractionSource,
            isPressed = trailingInteractionSource.collectIsPressedAsState().value,
            isOtherPressed = leadingInteractionSource.collectIsPressedAsState().value,
        )

    val state =
        LanguageState(
            isExpanded = isExpanded,
            selectedLanguage = selectedLanguage,
            onExpandedChange = onExpandedChange,
            onLanguageSelected = onLanguageSelected,
        )

    LanguageHeaderLayout(
        modifier = modifier,
        state = state,
        colors = colors,
        leadingInt = leadingInt,
        trailingInt = trailingInt,
    )
}

@Composable
private fun LanguageHeaderLayout(
    modifier: Modifier,
    state: LanguageState,
    colors: LanguageButtonColors,
    leadingInt: LanguageButtonInteraction,
    trailingInt: LanguageButtonInteraction,
) {
    Box(
        modifier =
            modifier
                .padding(
                    top = LanguageConstants.TOP_PADDING,
                    start = LanguageConstants.HORIZONTAL_PADDING,
                    end = LanguageConstants.HORIZONTAL_PADDING,
                )
                .wrapContentSize(),
        contentAlignment = Alignment.TopEnd,
    ) {
        SplitButtonLayout(
            leadingButton = {
                LanguageLeadingButton(
                    colors = colors,
                    selectedLanguage = state.selectedLanguage,
                    interaction = leadingInt,
                    onClick = { state.onExpandedChange(!state.isExpanded) },
                )
            },
            trailingButton = {
                LanguageTrailingButton(
                    isExpanded = state.isExpanded,
                    colors = colors,
                    interaction = trailingInt,
                    onCheckedChange = state.onExpandedChange,
                )
            },
        )

        LanguageDropdownMenu(
            isExpanded = state.isExpanded,
            contentColor = colors.content,
            onDismissRequest = { state.onExpandedChange(false) },
            onLanguageSelected = {
                state.onLanguageSelected(it)
                state.onExpandedChange(false)
            },
        )
    }
}

@Composable
private fun LanguageLeadingButton(
    colors: LanguageButtonColors,
    selectedLanguage: String,
    interaction: LanguageButtonInteraction,
    onClick: () -> Unit,
) {
    val motionScheme = MaterialTheme.motionScheme
    val buttonColors =
        ButtonDefaults.buttonColors(
            containerColor = colors.container,
            contentColor = colors.content,
        )

    // Animación de escala sutil integrada
    val scale by
        animateFloatAsState(
            targetValue =
                when {
                    interaction.isPressed -> LanguageConstants.PRESSED_SCALE
                    interaction.isOtherPressed -> LanguageConstants.COLLISION_SCALE
                    else -> 1f
                },
            animationSpec = motionScheme.defaultSpatialSpec(),
            label = "LeadingScale",
        )

    SplitButtonDefaults.LeadingButton(
        onClick = onClick,
        interactionSource = interaction.interactionSource,
        colors = buttonColors,
        content = {
            LanguageLeadingContent(
                selectedLanguage = selectedLanguage,
                scale = scale,
                isOtherPressed = interaction.isOtherPressed,
                isPressed = interaction.isPressed,
            )
        },
    )
}

@Composable
private fun LanguageLeadingContent(
    selectedLanguage: String,
    scale: Float,
    isOtherPressed: Boolean,
    isPressed: Boolean,
) {
    val motionScheme = MaterialTheme.motionScheme
    val translationX by
        animateFloatAsState(
            targetValue = if (isOtherPressed) -LanguageConstants.COLLISION_TRANSLATION_DP else 0f,
            animationSpec = motionScheme.defaultSpatialSpec(),
            label = "LeadingTranslationX",
        )
    val rotationZ by
        animateFloatAsState(
            targetValue = if (isPressed) -LanguageConstants.JIGGLE_ANGLE else 0f,
            animationSpec = motionScheme.defaultSpatialSpec(),
            label = "LeadingJiggle",
        )

    Icon(
        imageVector = Icons.Default.Language,
        modifier =
            Modifier
                .size(SplitButtonDefaults.LeadingIconSize)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.translationX = translationX
                    this.rotationZ = rotationZ
                },
        contentDescription = null,
    )
    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
    Text(
        text = selectedLanguage,
        fontSize = LanguageConstants.FONT_SIZE,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun LanguageTrailingButton(
    isExpanded: Boolean,
    colors: LanguageButtonColors,
    interaction: LanguageButtonInteraction,
    onCheckedChange: (Boolean) -> Unit,
) {
    val motionScheme = MaterialTheme.motionScheme
    val buttonColors =
        ButtonDefaults.buttonColors(
            containerColor = colors.container,
            contentColor = colors.content,
        )

    val scale by
        animateFloatAsState(
            targetValue =
                when {
                    interaction.isPressed -> LanguageConstants.PRESSED_SCALE
                    interaction.isOtherPressed -> LanguageConstants.COLLISION_SCALE
                    else -> 1f
                },
            animationSpec = motionScheme.defaultSpatialSpec(),
            label = "TrailingScale",
        )

    val iconRotation by
        animateFloatAsState(
            targetValue = if (isExpanded) 180f else 0f,
            animationSpec = motionScheme.defaultEffectsSpec(),
            label = "ArrowRotation",
        )

    SplitButtonDefaults.TrailingButton(
        checked = isExpanded,
        onCheckedChange = onCheckedChange,
        interactionSource = interaction.interactionSource,
        colors = buttonColors,
        modifier =
            Modifier.semantics {
                stateDescription = if (isExpanded) "Expanded" else "Collapsed"
            },
        content = {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                modifier =
                    Modifier
                        .size(SplitButtonDefaults.TrailingIconSize)
                        .graphicsLayer {
                            this.rotationZ = iconRotation
                            scaleX = scale
                            scaleY = scale
                        },
                contentDescription = stringResource(R.string.welcome_page1_select_language),
            )
        },
    )
}

@Composable
private fun LanguageDropdownMenu(
    isExpanded: Boolean,
    contentColor: Color,
    onDismissRequest: () -> Unit,
    onLanguageSelected: (String) -> Unit,
) {
    MaterialTheme(
        colorScheme =
            MaterialTheme.colorScheme.copy(
                surface = Color.Transparent,
                surfaceContainer = Color.Transparent,
            ),
    ) {
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = onDismissRequest,
            modifier =
                Modifier
                    .background(Color.Transparent)
                    .padding(top = 4.dp),
            containerColor = Color.Transparent,
            shadowElevation = 0.dp,
        ) {
            LanguageMenuColumn(
                contentColor = contentColor,
                onLanguageSelected = onLanguageSelected,
            )
        }
    }
}

@Composable
private fun LanguageMenuColumn(
    contentColor: Color,
    onLanguageSelected: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = LanguageConstants.MENU_PADDING.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val spanish = stringResource(R.string.welcome_page1_spanish)
        val english = stringResource(R.string.welcome_page1_english)

        LanguageMenuItem(text = spanish, contentColor = contentColor) {
            onLanguageSelected(spanish)
        }
        Spacer(modifier = Modifier.size(LanguageConstants.MENU_SPACING))
        LanguageMenuItem(text = english, contentColor = contentColor) {
            onLanguageSelected(english)
        }
    }
}

@Composable
private fun LanguageMenuItem(
    text: String,
    contentColor: Color,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        modifier =
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(LanguageConstants.PERCENT_50),
                )
                .size(
                    width = LanguageConstants.MENU_ITEM_WIDTH.dp,
                    height = LanguageConstants.MENU_ITEM_HEIGHT.dp,
                ),
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = text,
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                )
            }
        },
        onClick = onClick,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
    )
}
