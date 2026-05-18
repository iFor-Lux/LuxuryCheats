package com.luxury.cheats.features.home.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Sección de Consola Rediseñada de Alta Tecnología.
 * Integrada dinámicamente con Material You y el tema del sistema.
 * - W: 341, H: 215 (o expansible), BG: surfaceContainer, Corner: 30
 */
@Composable
fun HomeConsoleSection(
    modifier: Modifier = Modifier,
    consoleText: String = "",
    isExpanded: Boolean = false,
    onExpandClick: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .width(341.dp)
                .then(
                    if (isExpanded) Modifier.height(450.dp) else Modifier.height(215.dp),
                )
                .animateContentSize(
                    animationSpec =
                        androidx.compose.animation.core.spring(
                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy,
                            stiffness = androidx.compose.animation.core.Spring.StiffnessLow,
                        ),
                )
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(30.dp))
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                    RoundedCornerShape(30.dp),
                ),
    ) {
        val scrollState = rememberScrollState()

        // Auto-scroll al final cuando llega nuevo texto
        androidx.compose.runtime.LaunchedEffect(consoleText) {
            if (consoleText.isNotEmpty()) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            ConsoleHeader(consoleText = consoleText)
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            ConsoleContent(
                text = consoleText,
                scrollState = scrollState,
                modifier = Modifier.weight(1f),
            )
        }

        ExpandButton(
            isExpanded = isExpanded,
            onExpandClick = onExpandClick,
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

@Composable
private fun ExpandButton(
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .padding(12.dp)
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                    RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onExpandClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "Colapsar" else "Expandir",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp),
        )
    }
}

/**
 * Cabecera de la sección de consola de diagnóstico.
 */
@Composable
private fun ConsoleHeader(consoleText: String) {
    val isSearching = consoleText.contains("INICIANDO PROTOCOLO") || consoleText.contains("RASTREANDO REGIÓN")
    val hasError = consoleText.contains("[ERROR") || consoleText.contains("SIN RESULTADOS")
    val hasSuccess = consoleText.contains("👤 PERFIL")

    val statusText = when {
        isSearching && !hasSuccess && !hasError -> "DIAGNOSING"
        hasSuccess -> "ONLINE"
        hasError -> "FAILED"
        else -> "STANDBY"
    }

    val ledColor = when {
        isSearching && !hasSuccess && !hasError -> Color(0xFF00E676)
        hasSuccess -> Color(0xFF00E676)
        hasError -> Color(0xFFFF5252)
        else -> Color(0xFFFFB74D)
    }

    val isBlinking = isSearching && !hasSuccess && !hasError

    val infiniteTransition = rememberInfiniteTransition(label = "LedBlink")
    val alphaAnim = if (isBlinking) {
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500),
                repeatMode = RepeatMode.Reverse
            ),
            label = "LedAlpha"
        ).value
    } else {
        1.0f
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "CONSOLE DIAGNOSTICS",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            letterSpacing = 1.5.sp,
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .alpha(alphaAnim)
                    .background(ledColor, RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = statusText,
                color = ledColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}

/**
 * Contenido principal de la consola con salida de texto formateada.
 */
@Composable
private fun ConsoleContent(
    text: String,
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier,
) {
    val formattedText = rememberFormattedConsoleOutput(text)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(scrollState),
    ) {
        Text(
            text = formattedText,
            fontSize = 11.5.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            lineHeight = 17.sp,
        )
    }
}

/**
 * Analizador y formateador de texto de consola.
 */
@Composable
private fun rememberFormattedConsoleOutput(text: String): AnnotatedString {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    return remember(text, onSurfaceColor) {
        buildAnnotatedString {
            val lines = text.split("\n")
            lines.forEachIndexed { index, line ->
                when {
                    line.startsWith("🔥") -> {
                        withStyle(style = SpanStyle(
                            color = Color(0xFF00E676),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )) {
                            append(line)
                        }
                    }
                    line.startsWith("👤") || line.startsWith("🏆") || line.startsWith("🛡️") || line.startsWith("🌐") || line.startsWith("🐾") || line.startsWith("📈") -> {
                        val color = when {
                            line.startsWith("👤") -> Color(0xFF81D4FA)
                            line.startsWith("🏆") -> Color(0xFFFFD54F)
                            line.startsWith("🛡️") -> Color(0xFF80CBC4)
                            line.startsWith("🐾") -> Color(0xFFFFAB91)
                            line.startsWith("📈") -> Color(0xFFE1BEE7)
                            else -> Color(0xFF80DEEA)
                        }
                        withStyle(style = SpanStyle(
                            color = color,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )) {
                            append(line)
                        }
                    }
                    line.startsWith("•") -> {
                        val parts = line.split(":", limit = 2)
                        if (parts.size == 2) {
                            withStyle(style = SpanStyle(
                                color = onSurfaceColor.copy(alpha = 0.5f),
                                fontWeight = FontWeight.Normal
                            )) {
                                append(parts[0])
                                append(":")
                            }
                            withStyle(style = SpanStyle(
                                color = onSurfaceColor,
                                fontWeight = FontWeight.SemiBold
                            )) {
                                append(parts[1])
                            }
                        } else {
                            append(line)
                        }
                    }
                    line.contains("RASTREANDO REGIÓN:") -> {
                        withStyle(style = SpanStyle(
                            color = Color(0xFF29B6F6),
                            fontWeight = FontWeight.Medium
                        )) {
                            append(line)
                        }
                    }
                    line.contains("FALLO REGIÓN") || line.contains("ERROR DE RED") || line.startsWith("[ERROR") -> {
                        withStyle(style = SpanStyle(
                            color = Color(0xFFFF5252),
                            fontWeight = FontWeight.Bold
                        )) {
                            append(line)
                        }
                    }
                    line.contains("INICIANDO PROTOCOLO") || line.contains("SISTEMA OPERATIVO LUXURY CARGADO") || line.contains("ESPERANDO ENTRADA DE DATOS") -> {
                        withStyle(style = SpanStyle(
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Medium
                        )) {
                            append(line)
                        }
                    }
                    line.startsWith("----------------") -> {
                        withStyle(style = SpanStyle(
                            color = onSurfaceColor.copy(alpha = 0.1f),
                            fontWeight = FontWeight.Light
                        )) {
                            append(line)
                        }
                    }
                    else -> {
                        withStyle(style = SpanStyle(
                            color = onSurfaceColor.copy(alpha = 0.85f)
                        )) {
                            append(line)
                        }
                    }
                }
                if (index < lines.lastIndex) {
                    append("\n")
                }
            }
        }
    }
}
