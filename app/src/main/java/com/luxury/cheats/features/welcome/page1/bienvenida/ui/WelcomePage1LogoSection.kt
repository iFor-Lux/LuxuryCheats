package com.luxury.cheats.features.welcome.page1.bienvenida.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Sección reservada para el logo en la primera página de bienvenida.
 *
 * @param modifier Modificador de Compose.
 */
@Composable
fun welcomePage1LogoSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter,
    ) {
        // Reservar espacio para el logo (500dp base)
        // Solo necesitamos el tope para que otros elementos se posicionen debajo
        Box(modifier = Modifier.size(280.dp).offset(y = 30.dp))
    }
}
