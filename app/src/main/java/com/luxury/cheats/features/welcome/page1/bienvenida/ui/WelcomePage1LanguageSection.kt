package com.luxury.cheats.features.welcome.page1.bienvenida.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
 * Botón de Lenguaje (Top Right) usando Material Design 3 Expressive (Variant 2)
 */
private object LanguageConstants {
    val TOP_PADDING = 60.dp
    val HORIZONTAL_PADDING = 16.dp
    val ICON_ROTATION_DURATION = 150
    val FONT_SIZE = 14.sp
    val DIVIDER_ALPHA = 0.1f
}

/**
 * Sección de selección de idioma para la primera página de bienvenida.
 * @param modifier Modificador de layout.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WelcomePage1LanguageSection(
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val initialLanguage = stringResource(R.string.welcome_page1_language)
    var selectedLanguage by remember { mutableStateOf(initialLanguage) }

    val containerColor = MaterialTheme.colorScheme.surfaceContainer
    val contentColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .padding(
                top = LanguageConstants.TOP_PADDING,
                start = LanguageConstants.HORIZONTAL_PADDING,
                end = LanguageConstants.HORIZONTAL_PADDING
            )
            .wrapContentSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        SplitButtonLayout(
            leadingButton = {
                LanguageLeadingButton(
                    containerColor = containerColor,
                    contentColor = contentColor,
                    selectedLanguage = selectedLanguage,
                    onClick = { isExpanded = !isExpanded }
                )
            },
            trailingButton = {
                LanguageTrailingButton(
                    isExpanded = isExpanded,
                    containerColor = containerColor,
                    contentColor = contentColor,
                    onCheckedChange = { isExpanded = it }
                )
            },
        )

        LanguageDropdownMenu(
            isExpanded = isExpanded,
            contentColor = contentColor,
            onDismissRequest = { isExpanded = false },
            onLanguageSelected = {
                selectedLanguage = it
                isExpanded = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LanguageLeadingButton(
    containerColor: Color,
    contentColor: Color,
    selectedLanguage: String,
    onClick: () -> Unit
) {
    SplitButtonDefaults.LeadingButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        content = {
            Icon(
                imageVector = Icons.Default.Language,
                modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                contentDescription = null,
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = selectedLanguage,
                fontSize = LanguageConstants.FONT_SIZE,
                fontWeight = FontWeight.Medium
            )
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LanguageTrailingButton(
    isExpanded: Boolean,
    containerColor: Color,
    contentColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    SplitButtonDefaults.TrailingButton(
        checked = isExpanded,
        onCheckedChange = onCheckedChange,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = Modifier.semantics {
            stateDescription = if (isExpanded) "Expanded" else "Collapsed"
        },
        content = {
            val rotation: Float by animateFloatAsState(
                targetValue = if (isExpanded) 180f else 0f,
                animationSpec = tween(durationMillis = LanguageConstants.ICON_ROTATION_DURATION),
                label = "Trailing Icon Rotation",
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                modifier = Modifier
                    .size(SplitButtonDefaults.TrailingIconSize)
                    .graphicsLayer {
                        this.rotationZ = rotation
                    },
                contentDescription = stringResource(R.string.welcome_page1_select_language),
            )
        }
    )
}

@Composable
private fun LanguageDropdownMenu(
    isExpanded: Boolean,
    contentColor: Color,
    onDismissRequest: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        val spanish = stringResource(R.string.welcome_page1_spanish)
        val english = stringResource(R.string.welcome_page1_english)

        DropdownMenuItem(
            text = { Text(spanish, color = contentColor) },
            onClick = { onLanguageSelected(spanish) }
        )
        HorizontalDivider(color = contentColor.copy(alpha = LanguageConstants.DIVIDER_ALPHA))
        DropdownMenuItem(
            text = { Text(english, color = contentColor) },
            onClick = { onLanguageSelected(english) }
        )
    }
}
