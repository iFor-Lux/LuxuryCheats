package com.luxury.cheats.features.welcome.page1.bienvenida.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Texto "LUXURY" con estilo específico para la primera página de bienvenida.
 *
 * @param modifier Modificador de Compose.
 */
@Composable
fun welcomePage1LuxuryText(modifier: Modifier = Modifier) {
    Text(
        text = "LUXURY",
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        modifier = modifier,
    )
}
