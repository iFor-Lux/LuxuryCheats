package com.luxury.cheats.features.home.floating.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Build
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.BlendMode
import com.kyant.backdrop.backdrops.LayerBackdrop
import kotlin.random.Random

import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

/**
 * El Panel de Control Flotante con Glassmorphism "Ultra Premium".
 * Utiliza efectos de lente, dispersión cromática y texturas para simular cristal real.
 */
@Composable
fun HomeFloatingPanel(
    backdrop: LayerBackdrop?,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .width(320.dp)
            .height(420.dp)
            .clickable(enabled = false) {}
            .clip(shape)
            // Ya no usamos un fondo negro opaco, dejamos que el blur del sistema trabaje.
            // Solo añadimos un tinte sutil y el borde.
            .drawBackdrop(
                backdrop = backdrop ?: rememberLayerBackdrop(),
                shape = { shape },
                effects = {
                    // Refinamiento: Lente con refracción y aberración cromática sutil
                    lens(
                        refractionHeight = 20f,
                        refractionAmount = 0.05f,
                        chromaticAberration = true
                    )
                }
            )
            .border(1.5.dp, Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.3f),
                    Color.White.copy(alpha = 0.05f),
                    Color(0xFFBB86FC).copy(alpha = 0.15f)
                )
            ), shape)
    ) {
        // --- 1. CAPA DE TINTE Y BLUR INTERNO (FROSTED FALLBACK) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Modifier.graphicsLayer {
                            // Blureamos el degradado interno para dar suavidad
                            renderEffect = android.graphics.RenderEffect.createBlurEffect(
                                30f, 30f, android.graphics.Shader.TileMode.CLAMP
                            ).asComposeRenderEffect()
                        }
                    } else Modifier
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A1A1A).copy(alpha = 0.5f), 
                            Color(0xFF0A0A0A).copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // --- 2. TEXTURA DE RUIDO (NOISE GRAIN) ---
        Canvas(modifier = Modifier.fillMaxSize().graphicsLayer(alpha = 0.12f)) {
            val random = Random(42)
            for (i in 0..1200) { 
                drawCircle(
                    color = Color.White,
                    radius = random.nextFloat() * 1.5f,
                    center = androidx.compose.ui.geometry.Offset(
                        random.nextFloat() * size.width,
                        random.nextFloat() * size.height
                    ),
                    alpha = random.nextFloat() * 0.4f,
                    blendMode = BlendMode.Overlay
                )
            }
        }

        // --- 3. BRILLO ESPECULAR (TOP SHINE) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        0.0f to Color.White.copy(alpha = 0.06f),
                        0.3f to Color.Transparent,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(500f, 500f)
                    )
                )
        )

        // --- 4. CONTENIDO NÍTIDO (FRONT LAYER) ---
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp)
        ) {
            ControlPanelHeader(onDismissRequest)
            Spacer(modifier = Modifier.height(24.dp))
            ControlPanelBody()
            Spacer(modifier = Modifier.weight(1f))
            ControlPanelFooter()
        }
    }
}

@Composable
private fun ControlPanelHeader(onDismissRequest: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "PANEL DE CONTROL",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "ESTADO: POSITIVO",
                color = Color(0xFFBB86FC),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        IconButton(
            onClick = onDismissRequest,
            modifier = Modifier
                .size(32.dp)
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun ControlPanelBody() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ControlItem("SEGURIDAD ACTIVA", "Protección Bypass 2026", Icons.Default.Shield, true)
        ControlItem("OPTIMIZACIÓN", "Kernel inyectado correctamente", Icons.Default.Settings, true)
        ControlItem("MODO OCULTO", "Invisible para el sistema", Icons.Default.Info, false)
    }
}

@Composable
private fun ControlItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (isActive) Color(0xFFBB86FC).copy(alpha = 0.15f) 
                    else Color.White.copy(alpha = 0.06f),
                    RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) Color(0xFFBB86FC) else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ControlPanelFooter() {
    Button(
        onClick = { /* Próximamente */ },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFBB86FC).copy(alpha = 0.25f),
            contentColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBB86FC).copy(alpha = 0.3f))
    ) {
        Text(
            text = "SISTEMA SEGURO",
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
