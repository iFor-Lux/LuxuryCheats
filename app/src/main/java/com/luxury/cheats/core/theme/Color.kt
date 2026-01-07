package com.luxury.cheats.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

/**
 * Colores base de la aplicación - Luxury Theme
 */

// Monocromo Premium
val OnyxBlack = Color(0xFF080808)
val DarkGray = Color(0xFF121212)
val SurfaceGray = Color(0xFF1A1A1A)
val PremiumGray = Color(0xFF202020)
val Gray40 = Color(0xFF404040)
val MediumGray = Color(0xFF444444)
val LightGray = Color(0xFFF2F2F2)
val MediumGray77 = Color(0xFF777777)
val CleanWhite = Color(0xFFFFFFFF)

// Acentos (Opcionales, siguiendo minimalismo)
val AccentSilver = Color(0xFFC0C0C0)
val LuxuryOrange = Color(0xFFFFAE00)

private const val HARMONIZE_ORIGINAL_RATIO = 0.7f
private const val HARMONIZE_PRIMARY_RATIO = 0.3f
private const val COLOR_THRESHOLD = 0.01f
private const val DEFAULT_VIBRANCY_FACTOR = 1.3f

/**
 * Armoniza un color con el color primario del esquema actual.
 * Esto asegura que los colores de marca se adapten matemáticamente al Material You.
 */
@Composable
fun Color.harmonizeWithPrimary(): Color {
    val primary = MaterialTheme.colorScheme.primary
    return remember(this, primary) {
        // Mezcla: 70% original, 30% primario para mantener marca pero ser compatible
        Color(
            red = this.red * HARMONIZE_ORIGINAL_RATIO + primary.red * HARMONIZE_PRIMARY_RATIO,
            green = this.green * HARMONIZE_ORIGINAL_RATIO + primary.green * HARMONIZE_PRIMARY_RATIO,
            blue = this.blue * HARMONIZE_ORIGINAL_RATIO + primary.blue * HARMONIZE_PRIMARY_RATIO,
            alpha = this.alpha
        )
    }
}

/**
 * Aumenta la saturación de un color para hacerlo más vívido.
 * Útil para hacer que los colores dinámicos de Material You sean más impactantes.
 */
fun Color.boostVibrancy(factor: Float = DEFAULT_VIBRANCY_FACTOR): Color {
    val max = maxOf(red, green, blue)
    val min = minOf(red, green, blue)
    val delta = max - min

    if (delta < COLOR_THRESHOLD) return this // Color casi gris

    // Aumentar la diferencia entre componentes para más saturación
    val boost = (delta * (factor - 1f)).coerceAtMost(1f - max)

    return Color(
        red = if (red == max) (red + boost).coerceIn(0f, 1f) else (red - boost * 0.5f).coerceIn(0f, 1f),
        green = if (green == max) (green + boost).coerceIn(0f, 1f) else (green - boost * 0.5f).coerceIn(0f, 1f),
        blue = if (blue == max) (blue + boost).coerceIn(0f, 1f) else (blue - boost * 0.5f).coerceIn(0f, 1f),
        alpha = alpha
    )
}
