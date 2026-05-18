package com.luxury.cheats.services.floating.ui.sections

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.services.floating.ui.components.LuxuryMenuButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Paleta de Colores de Neón Premium de la Terminal
private val COLOR_TEXT_SECONDARY = Color(0xFF8E8E93)
private val COLOR_CONSOLE_BG = Color(0xFF0D0E11)
private val COLOR_CARD_BG = Color(0xFF18181C)
private val COLOR_BORDER_DEFAULT = Color(0xFF222228)
private val COLOR_BORDER_SUCCESS = Color(0xFF00FF66)

@Composable
fun OptimizerSection() {
    // Estados de optimización
    var isOptimized by remember { mutableStateOf(false) }

    // Objetivos de progreso iniciales y finales
    var ramTarget by remember { mutableFloatStateOf(0.62f) }
    var cpuTarget by remember { mutableFloatStateOf(0.55f) }
    var gpuTarget by remember { mutableFloatStateOf(0.58f) }

    // Animación fluida de Física de Resortes estilo iOS
    val animatedRAM by animateFloatAsState(
        targetValue = ramTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "RamSpring"
    )

    val animatedCPU by animateFloatAsState(
        targetValue = cpuTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CpuSpring"
    )

    val animatedGPU by animateFloatAsState(
        targetValue = gpuTarget,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "GpuSpring"
    )

    val handleOptimization = {
        if (!isOptimized) {
            ramTarget = 1.00f
            cpuTarget = 1.00f
            gpuTarget = 1.00f
            isOptimized = true
            // Reproducir sonido de éxito inmediato
            AiSoundHelper.playReceived()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        // 1. Textos de Cabecera
        Text(
            text = "Game Booster",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Optimiza la RAM, CPU y GPU para un rendimiento extremo.",
            color = COLOR_TEXT_SECONDARY,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 2. Tablero de Consolas / Métricas macOS Style (Tres círculos)
        val borderHighlightColor = if (isOptimized) COLOR_BORDER_SUCCESS.copy(alpha = 0.4f) else COLOR_BORDER_DEFAULT
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(COLOR_CONSOLE_BG)
                .border(1.dp, borderHighlightColor, RoundedCornerShape(16.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tarjeta RAM (Neon Violeta)
            ResourceMetricCard(
                progress = animatedRAM,
                label = "RAM",
                valueText = "${(animatedRAM * 100).toInt()}%",
                colorStart = Color(0xFF8B5CF6),
                colorEnd = Color(0xFFD946EF),
                icon = Icons.Default.Memory,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Tarjeta CPU (Neon Naranja)
            ResourceMetricCard(
                progress = animatedCPU,
                label = "CPU",
                valueText = "${(animatedCPU * 100).toInt()}%",
                colorStart = Color(0xFFF97316),
                colorEnd = Color(0xFFEF4444),
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Tarjeta GPU (Neon Cyan)
            ResourceMetricCard(
                progress = animatedGPU,
                label = "GPU",
                valueText = "${(animatedGPU * 100).toInt()}%",
                colorStart = Color(0xFF06B6D4),
                colorEnd = Color(0xFF3B82F6),
                icon = Icons.Default.Bolt,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Botón Optimizar Grande (LuxuryMenuButton sencillo como el de Gloo)
        val buttonText = if (isOptimized) "SISTEMA OPTIMIZADO" else "OPTIMIZAR"

        LuxuryMenuButton(
            text = buttonText,
            icon = Icons.Default.Bolt,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (!isOptimized) {
                    handleOptimization()
                }
            }
        )
    }
}

@Composable
fun ResourceMetricCard(
    progress: Float,
    label: String,
    valueText: String,
    colorStart: Color,
    colorEnd: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(COLOR_CARD_BG)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Círculo de Progreso Personalizado
        Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 4.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2

                // Pista de fondo
                drawCircle(
                    color = Color(0xFF222228),
                    radius = radius,
                    style = Stroke(width = strokeWidth)
                )

                // Arco de Progreso Neón con bordes circulares
                drawArc(
                    brush = Brush.linearGradient(listOf(colorStart, colorEnd)),
                    startAngle = -90f,
                    sweepAngle = progress * 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Ícono de recurso centrado
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = colorStart,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Valor numérico (Porcentaje)
        Text(
            text = valueText,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Etiqueta identificadora
        Text(
            text = label,
            color = COLOR_TEXT_SECONDARY,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
