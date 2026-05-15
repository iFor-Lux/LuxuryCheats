package com.luxury.cheats.features.archivoplus.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ArchivoPlusExecuteButton(
    isExecuting: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    // Usamos PrimaryContainer para un look más Material You / M3 Expressive
    // que resalte sobre el fondo pero mantenga armonía.
    Button(
        onClick = onClick,
        enabled = enabled && !isExecuting,
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp), // Un poco más alto para mayor énfasis
        shape = CircleShape, // Completamente redondeado (estilo píldora)
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 12.dp,
            pressedElevation = 2.dp,
            hoveredElevation = 15.dp
        )
    ) {
        AnimatedContent(
            targetState = isExecuting,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
            },
            label = "ExecuteAnimation"
        ) { executing ->
            if (executing) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "PROCESANDO...",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else {
                Text(
                    text = "APLICAR CAMBIOS",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
