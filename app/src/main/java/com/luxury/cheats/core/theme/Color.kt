package com.luxury.cheats.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Colores base de la aplicación - Luxury Theme
 *
 * Monocromo Premium
 */
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

private const val HSV_COMPONENTS = 3
private const val ALPHA_SHIFT = 24
private const val COLOR_MASK = 0xFF

/**
 * Armoniza un color con el color primario del esquema actual.
 * Utilizamos el espacio de color HSL (Hue, Saturation, Lightness) para adaptar el matiz
 * al tema dinámico mientras mantenemos la saturación y el tono original del color de marca.
 */
@Composable
fun Color.harmonizeWithPrimary(): Color {
    val primary = MaterialTheme.colorScheme.primary
    return remember(this, primary) {
        val hsvOriginal = FloatArray(HSV_COMPONENTS)
        val hsvPrimary = FloatArray(HSV_COMPONENTS)

        android.graphics.Color.colorToHSV(this.toArgb(), hsvOriginal)
        android.graphics.Color.colorToHSV(primary.toArgb(), hsvPrimary)

        // Adoptamos el matiz (Hue) del primario dinámico para armonizar
        // Pero preservamos la saturación y el valor (brillo) del color original
        val harmonizedHsv =
            floatArrayOf(
                // Matiz del sistema
                hsvPrimary[0],
                // Saturación original
                hsvOriginal[1],
                // Brillo original
                hsvOriginal[2],
            )

        Color(android.graphics.Color.HSVToColor(this.toArgb().shr(ALPHA_SHIFT) and COLOR_MASK, harmonizedHsv))
    }
}
