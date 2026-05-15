package com.luxury.cheats.services.floating.ui.sections

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.luxury.cheats.services.floating.ui.components.LuxuryMenuButton

private const val URL_GLOO_BASE = "https://images.guns.lol/31c0364b9e0a58a873bbe97f0378895eefe318fa/ByhxQk.webp"
private const val URL_GLOO_ROTATED = "https://images.guns.lol/31c0364b9e0a58a873bbe97f0378895eefe318fa/z6BbMG.webp"
private const val ANIM_DURATION = 500
private val COLOR_TEXT_SECONDARY = Color(0xFF8E8E8E)

@Composable
fun GlooSection(
    isRotated: Boolean,
    onRotate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // Parte Superior: Textos (Izquierda) + Imagen (Derecha)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Textos a la izquierda
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Gloo Wall Rotation",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Seleccione la dirección de la pared de acuerdo a su necesidad",
                    color = COLOR_TEXT_SECONDARY,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Imagen a la derecha
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(
                    targetState = if (isRotated) URL_GLOO_ROTATED else URL_GLOO_BASE,
                    animationSpec = tween(durationMillis = ANIM_DURATION),
                    label = "GlooImageTransition"
                ) { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "Gloo Wall",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Parte Inferior: Un solo botón grande
        LuxuryMenuButton(
            text = "GIRAR",
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.ChangeCircle,
            onClick = onRotate
        )
    }
}
