package com.luxury.cheats.features.home.ui.seguridad

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.luxury.cheats.features.home.ui.seguridad.desactivado.logic.HomeSeguridadLogic
import com.luxury.cheats.features.home.ui.seguridad.desactivado.ui.HomeSeguridadInner as DesactivadoInner
import com.luxury.cheats.features.home.ui.seguridad.activado.logic.HomeSeguridadInnerMorphing
import com.luxury.cheats.features.home.ui.seguridad.cambios.HomeSeguridadOuter

/**
 * Orquestador de Seguridad con Shape Morphing.
 * - Outer: hace morphing entre Cookie7Sided (desactivado) y Circle (activado)
 * - Inner: usa forma fija cuando desactivado, cicla entre formas cuando activado
 */
@Composable
fun HomeSeguridadSection(
    modifier: Modifier = Modifier,
    isActivated: Boolean = false,
    onClick: () -> Unit = {}
) {
    val containerSize = 280.dp
    val rotation by HomeSeguridadLogic.animateRotation(isActivated)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier.size(containerSize),
        contentAlignment = Alignment.Center
    ) {
        HomeSeguridadOuter(
            size = containerSize,
            isActivated = isActivated,
            interactionSource = interactionSource,
            onClick = onClick
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isActivated) {
                    // Activado: Inner con morphing entre formas
                    HomeSeguridadInnerMorphing(
                        modifier = Modifier.fillMaxSize(),
                        rotation = rotation
                    )
                } else {
                    // Desactivado: Inner con forma fija
                    DesactivadoInner(
                        modifier = Modifier.fillMaxSize(),
                        rotation = rotation
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F7)
@Composable
private fun HomeSeguridadSectionPreview() {
    var isActivated by remember { mutableStateOf(false) }
    
    MaterialTheme {
        Box(
            modifier = Modifier
                .size(400.dp)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            HomeSeguridadSection(
                isActivated = isActivated,
                onClick = { isActivated = !isActivated }
            )
        }
    }
}
