package com.luxury.cheats.features.tools.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

private object SliderConstants {
    const val CENTER_THRESHOLD_MIN = 48
    const val CENTER_THRESHOLD_MAX = 52
    const val SNAP_CENTER_MIN = 47.5f
    const val SNAP_CENTER_MAX = 52.5f
    const val CENTER_VALUE = 50f
}

@Composable
fun ConfigSlider(
    label: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    maxAllowedValue: Float? = null,
    decreaseIcon: ImageVector = Icons.Default.KeyboardArrowLeft,
    increaseIcon: ImageVector = Icons.Default.KeyboardArrowRight,
    arrowStep: Float = 1f,
    onValueChange: (Float) -> Unit,
) {
    var sliderValue by remember(value) { mutableStateOf(value) }

    // Sincronizar valor externo si cambia
    LaunchedEffect(value) {
        sliderValue = value
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = valueText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            IconButton(
                onClick = {
                    val targetValue = (sliderValue - arrowStep).coerceIn(range.start, range.endInclusive)
                    val constrainedValue = maxAllowedValue?.let { minOf(targetValue, it) } ?: targetValue
                    onValueChange(constrainedValue)
                },
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = decreaseIcon,
                    contentDescription = "Menos",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                // Punto central decorativo para sliders continuos
                if (steps == 0) {
                    val isCentered = sliderValue.roundToInt() in
                        SliderConstants.CENTER_THRESHOLD_MIN..SliderConstants.CENTER_THRESHOLD_MAX
                    val dotSize by animateDpAsState(
                        targetValue = if (isCentered) 10.dp else 6.dp,
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                        label = "DotSize",
                    )

                    Box(
                        modifier =
                            Modifier
                                .size(dotSize)
                                .background(
                                    color =
                                        if (isCentered) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                        },
                                    shape = CircleShape,
                                ),
                    )
                }

                Slider(
                    value = sliderValue,
                    onValueChange = { newValue ->
                        val constrainedValue = maxAllowedValue?.let { minOf(newValue, it) } ?: newValue

                        // Actualizamos visualmente al límite
                        sliderValue =
                            if (steps == 0) {
                                if (constrainedValue in
                                    SliderConstants.SNAP_CENTER_MIN..SliderConstants.SNAP_CENTER_MAX
                                ) {
                                    SliderConstants.CENTER_VALUE
                                } else {
                                    constrainedValue
                                }
                            } else {
                                constrainedValue
                            }

                        // Notificamos el valor real (incluyendo el exceso) para que el ViewModel
                        // pueda detectar el intento y mostrar el aviso VIP
                        onValueChange(newValue)
                    },
                    valueRange = range,
                    steps = steps,
                    colors =
                        SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            activeTickColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                            inactiveTickColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        ),
                )
            }

            IconButton(
                onClick = {
                    val targetValue = (sliderValue + arrowStep).coerceIn(range.start, range.endInclusive)
                    val constrainedValue = maxAllowedValue?.let { minOf(targetValue, it) } ?: targetValue

                    // Si el valor intentado es mayor al permitido, disparamos el cambio
                    // para que el padre decida (mostrar aviso)
                    // Pero visualmente lo mantenemos bloqueado
                    if (maxAllowedValue != null && targetValue > maxAllowedValue) {
                        onValueChange(targetValue)
                    } else {
                        onValueChange(constrainedValue)
                    }
                },
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = increaseIcon,
                    contentDescription = "Más",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
fun VipRestrictionDialog(
    title: String = "FUNCIÓN LIMITADA",
    message: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        text = {
            Text(
                text = message,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            val context = androidx.compose.ui.platform.LocalContext.current
            Button(
                onClick = {
                    onDismiss()
                    try {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://luxurycheats.pages.dev/")).apply {
                            addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Evita caídas silenciosamente si el contexto no permite iniciar la actividad
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
            ) {
                Text("COMPRAR VIP", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
    )
}
