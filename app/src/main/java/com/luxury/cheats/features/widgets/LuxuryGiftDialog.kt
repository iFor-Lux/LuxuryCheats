package com.luxury.cheats.features.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.luxury.cheats.core.theme.LuxuryCheatsTheme

/**
 * Diálogo interactivo y dinámico diseñado con Material You y M3 Expressive.
 * Se adapta perfectamente al tema (Claro / Oscuro) y a la paleta de colores dinámicos del sistema.
 * Elimina colores dorados y oscuros fijos en favor del esquema dinámico del sistema operativo.
 */
@Composable
fun LuxuryGiftDialog(
    giftDays: Int,
    onClaim: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        var visible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            visible = true
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) +
                    scaleIn(
                        initialScale = 0.82f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
        ) {
            LuxuryGiftContent(
                giftDays = giftDays,
                onClaim = onClaim,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun LuxuryGiftContent(
    giftDays: Int,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Obtenemos los tokens de color del esquema de Material You actual
    val colorScheme = MaterialTheme.colorScheme

    // Gradiente sutil dinámico de Material You para la tarjeta (usa el contenedor de superficie y fondo)
    val cardBackground = colorScheme.surfaceContainerHigh
    val cardOutline = colorScheme.primary.copy(alpha = 0.15f)

    // Gradiente dinámico de acento usando colores primario y terciario para la cápsula de días
    val accentGradient = Brush.horizontalGradient(
        colors = listOf(
            colorScheme.primary,
            colorScheme.tertiary
        )
    )

    Box(
        modifier = modifier
            .size(width = 330.dp, height = 430.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = colorScheme.primary.copy(alpha = 0.2f),
                spotColor = colorScheme.primary.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(cardBackground)
            .border(
                width = 1.dp,
                color = cardOutline,
                shape = RoundedCornerShape(32.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Cabecera: Pill decorativa al estilo de Material M3 Expressive
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(colorScheme.primaryContainer.copy(alpha = 0.35f))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "REGALO EXCLUSIVO",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Icono Central de Regalo con Círculo Dinámico de Doble Tono
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primaryContainer.copy(alpha = 0.6f))
                    .border(1.dp, colorScheme.primary.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = "Regalo",
                    tint = colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Título Principal
            // Días de regalo en tamaño Gigante e impactante
            Text(
                text = "+$giftDays DIAS",
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = colorScheme.primary,
                textAlign = TextAlign.Center,
                letterSpacing = (-1).sp
            )

            // Cuerpo del Mensaje
            Text(
                text = "¡Muchas felicidades! Te hemos regalado unos días por ser un cliente especial. Se sumarán a tu suscripción de inmediato.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Botón de Canje Principal de Material You
            Button(
                onClick = onClaim,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = colorScheme.primary.copy(alpha = 0.3f),
                        spotColor = colorScheme.primary.copy(alpha = 0.3f)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECLAMAR REGALO",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LuxuryGiftDialogPreview() {
    LuxuryCheatsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            LuxuryGiftContent(
                giftDays = 7,
                onClaim = {}
            )
        }
    }
}
