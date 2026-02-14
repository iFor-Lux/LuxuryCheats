package com.luxury.cheats.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Escala de formas (Shapes) para Luxury Cheats
 * Siguiendo la estética Material 3 Expressive:
 * - Esquinas más redondeadas para una sensación orgánica y premium.
 * - Jerarquía clara entre componentes pequeños y grandes.
 */
val LuxuryShapes =
    Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(28.dp),
    )
