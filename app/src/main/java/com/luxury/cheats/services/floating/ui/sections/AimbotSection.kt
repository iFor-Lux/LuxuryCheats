package com.luxury.cheats.services.floating.ui.sections

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.luxury.cheats.services.floating.ui.components.LuxuryMenuButton

// Variables de URL para fácil detección y mantenimiento
private const val URL_BASE = "https://images.guns.lol/31c0364b9e0a58a873bbe97f0378895eefe318fa/nvxpsp.webp"
private const val URL_CUERPO = "https://images.guns.lol/31c0364b9e0a58a873bbe97f0378895eefe318fa/2k2cZa.webp"
private const val URL_CABEZA = "https://images.guns.lol/31c0364b9e0a58a873bbe97f0378895eefe318fa/BHNyzB.webp"

private const val ANIMATION_DURATION = 500
private val TEXT_SECONDARY_COLOR = Color(0xFF8E8E8E)

@Composable
fun AimbotSection(
    target: String,
    onTargetSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Parte Superior: Imagen + Textos
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cuadro de Imagen con Transición Crossfade
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(
                    targetState = target,
                    animationSpec = tween(durationMillis = ANIMATION_DURATION),
                    label = "AimbotImageTransition"
                ) { currentTarget ->
                    val imageUrl = when (currentTarget) {
                        "Cabeza" -> URL_CABEZA
                        "Cuerpo" -> URL_CUERPO
                        else -> URL_BASE
                    }

                    // Animación de escala sutil para Cabeza
                    val scale by animateFloatAsState(
                        targetValue = if (currentTarget == "Cabeza") 1.1f else 1.0f,
                        animationSpec = tween(ANIMATION_DURATION),
                        label = "ImageScale"
                    )

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Aimbot Target",
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(scale),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Direccion del Aimbot",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Seleccione el seguimiento del aimbot de acuerdo a su necesidad",
                    color = TEXT_SECONDARY_COLOR,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Parte Inferior: Botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LuxuryMenuButton(
                text = "Cuerpo",
                modifier = Modifier.weight(1f),
                icon = androidx.compose.material.icons.Icons.Default.Accessibility,
                onClick = { onTargetSelected("Cuerpo") }
            )
            LuxuryMenuButton(
                text = "Cabeza",
                modifier = Modifier.weight(1f),
                icon = androidx.compose.material.icons.Icons.Default.Face,
                onClick = { onTargetSelected("Cabeza") }
            )
        }
    }
}
