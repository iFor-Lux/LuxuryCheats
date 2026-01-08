package com.luxury.cheats.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.luxury.cheats.features.update.ui.UpdateAnuncioSection
import com.luxury.cheats.navigations.NavRoutes

private const val TEST_PANEL_ALPHA = 0.5f
private const val TEST_BUTTON_WIDTH_FRACTION = 0.8f
private const val EXTRA_TEST_BUTTONS_COUNT = 6

/**
 * Sección de pruebas para el desarrollo (No producción).
 * El primer botón abre el anuncio de actualización.
 */
@Composable
fun HomeTestSection(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var showUpdateAnuncio by remember { mutableStateOf(false) }

    if (showUpdateAnuncio) {
        UpdateAnuncioDialog(onDismiss = { showUpdateAnuncio = false }) {
            showUpdateAnuncio = false
            navController.navigate(NavRoutes.UPDATE)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = TEST_PANEL_ALPHA))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TestPanelHeader()

            // Botón 1: Abre el anuncio
            Button(
                onClick = { showUpdateAnuncio = true },
                modifier = Modifier.fillMaxWidth(TEST_BUTTON_WIDTH_FRACTION),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "ABRIR ANUNCIO UPDATE", fontSize = 12.sp)
            }

            ExtraTestButtons()
        }
    }
}

@Composable
private fun UpdateAnuncioDialog(
    onDismiss: () -> Unit,
    onUpdateClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        UpdateAnuncioSection(onUpdateClick = onUpdateClick)
    }
}

@Composable
private fun TestPanelHeader() {
    Text(
        text = "PANEL DE PRUEBAS",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun ExtraTestButtons() {
    // Los otros 6 botones de prueba
    repeat(EXTRA_TEST_BUTTONS_COUNT) { index ->
        Button(
            onClick = { /* Acción de prueba index + 2 */ },
            modifier = Modifier.fillMaxWidth(TEST_BUTTON_WIDTH_FRACTION),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "TEST BUTTON ${index + 2}", fontSize = 12.sp)
        }
    }
}
