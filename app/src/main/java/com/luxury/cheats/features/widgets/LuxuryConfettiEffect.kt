package com.luxury.cheats.features.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.random.Random

/**
 * Modelo de datos optimizado para partículas de confeti de disparo único (One-shot).
 */
private class ConfettiParticle(
    var xPercent: Float, // Posición horizontal (0.0f a 1.0f de la pantalla)
    var y: Float,        // Posición vertical real en píxeles
    val color: Color,
    val width: Float,
    val height: Float,
    val speedY: Float,
    var rotation: Float,
    val rotationSpeed: Float,
    val swingSpeed: Float,
    val swingWidth: Float,
    var swingAngle: Float,
    var alpha: Float = 1.0f
) {
    fun update(deltaTime: Float, screenHeight: Float) {
        // Movimiento de caída libre
        y += speedY * deltaTime * 60f
        
        // Rotación
        rotation = (rotation + rotationSpeed * deltaTime * 60f) % 360f
        
        // Oscilación horizontal senoidal
        swingAngle += swingSpeed * deltaTime * 60f

        // EFECTO PEDIDO POR EL USUARIO: Desvanecimiento gradual después de pasar la mitad de la pantalla
        val halfHeight = screenHeight / 2f
        if (y > halfHeight) {
            // El alpha disminuye linealmente desde la mitad hasta el final
            alpha = (1.0f - (y - halfHeight) / halfHeight).coerceIn(0.0f, 1.0f)
        } else {
            alpha = 1.0f
        }
    }

    // Resetear las partículas arriba de la pantalla al inicio de la animación
    fun reset() {
        y = Random.nextFloat() * -300f - 50f // Nacen dispersas por encima del límite superior
        xPercent = Random.nextFloat()
        swingAngle = Random.nextFloat() * 360f
        rotation = Random.nextFloat() * 360f
        alpha = 1.0f
    }
}

/**
 * Efecto de Confeti minimalista, simple y de disparo único (One-Shot).
 * Nace arriba, cae de forma fluida y se desvanece mágicamente a partir de la mitad de la pantalla.
 */
@Composable
fun LuxuryConfettiEffect(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val colorScheme = MaterialTheme.colorScheme
    
    // Paleta de colores alegres de confeti basada en Material You
    val confettiColors = remember(colorScheme) {
        listOf(
            colorScheme.primary,
            colorScheme.secondary,
            colorScheme.tertiary,
            colorScheme.error,
            Color(0xFF4CAF50), // Verde brillante
            Color(0xFFFFEB3B), // Amarillo
            Color(0xFF00BCD4), // Cian
            Color(0xFFE91E63), // Magenta
            Color(0xFFFF9800)  // Naranja
        )
    }

    // Creamos 60 partículas (cantidad justa y minimalista para una cascada sutil y limpia)
    val particles = remember {
        mutableStateListOf<ConfettiParticle>().apply {
            repeat(60) {
                add(
                    ConfettiParticle(
                        xPercent = Random.nextFloat(),
                        y = -100f,
                        color = confettiColors[Random.nextInt(confettiColors.size)],
                        width = Random.nextFloat() * 12f + 10f,  // Tamaño de confeti clásico sutil
                        height = Random.nextFloat() * 18f + 10f,
                        speedY = Random.nextFloat() * 3.0f + 2.5f, // Caída fluida a velocidades variadas
                        rotation = Random.nextFloat() * 360f,
                        rotationSpeed = (Random.nextFloat() - 0.5f) * 10f,
                        swingSpeed = Random.nextFloat() * 0.06f + 0.03f,
                        swingWidth = Random.nextFloat() * 18f + 8f,
                        swingAngle = Random.nextFloat() * 360f
                    )
                )
            }
        }
    }

    var frameTick by remember { mutableStateOf(0L) }

    LaunchedEffect(visible) {
        if (!visible) return@LaunchedEffect

        // Reiniciamos todas las partículas arriba de la pantalla al activarse
        for (particle in particles) {
            particle.reset()
        }

        var lastTime = withFrameMillis { it }
        while (true) {
            withFrameMillis { frameTime ->
                val deltaTime = ((frameTime - lastTime) / 1000f).coerceAtMost(0.03f)
                lastTime = frameTime
                
                // Usamos una altura de pantalla de referencia antes de medir
                for (particle in particles) {
                    particle.update(deltaTime, 2500f)
                }

                frameTick = frameTime
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val tick = frameTick // Suscripción al frame loop
        
        val screenWidth = size.width
        val screenHeight = size.height

        for (particle in particles) {
            // Si el alpha ya es cero, no gastamos ciclos dibujándolo
            if (particle.alpha <= 0.0f) continue

            // Actualizamos la física basándonos en la altura real de este Canvas
            particle.update(0.016f, screenHeight)

            val swingOffset = kotlin.math.sin(particle.swingAngle) * particle.swingWidth
            val realX = (particle.xPercent * screenWidth) + swingOffset

            withTransform({
                translate(realX, particle.y)
                rotate(particle.rotation)
            }) {
                drawRoundRect(
                    color = particle.color.copy(alpha = particle.alpha),
                    topLeft = Offset(-particle.width / 2f, -particle.height / 2f),
                    size = Size(particle.width, particle.height),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
                )
            }
        }
    }
}
