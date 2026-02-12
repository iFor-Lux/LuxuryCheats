package com.luxury.cheats.features.home.floating.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kyant.backdrop.backdrops.LayerBackdrop

/**
 * Envoltorio del Panel de Control para uso en modo flotante.
 * Ya no incluye fondo atenuado para permitir ver y tocar lo que hay detrás.
 */
@Composable
fun HomeControlPanelSection(
    visible: Boolean,
    backdrop: LayerBackdrop?,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (visible) {
        // En modo service, este Box ocupará el tamaño de la ventana (340dp x 480dp)
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically { it / 3 },
                exit = fadeOut() + slideOutVertically { it / 3 }
            ) {
                HomeFloatingPanel(
                    backdrop = backdrop,
                    onDismissRequest = onDismissRequest
                )
            }
        }
    }
}
