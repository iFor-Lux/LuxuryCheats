package com.luxury.cheats.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.features.home.logic.HomeAction

private const val SECTION_WIDTH = 340
private const val WIDE_CARD_HEIGHT = 80

/**
 * Sección del Panel de Control
 * - W: 340
 * - Contiene una tarjeta ancha para ajustes avanzados
 */
@Composable
fun HomePanelControlSection(
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier =
            modifier
                .width(SECTION_WIDTH.dp)
                .padding(horizontal = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            WideOptionCard(
                title = "PANEL DE CONTROL",
                description = "Ajustes avanzados y configuración",
                onClick = { onAction(HomeAction.ToggleFloatingWidget) }
            )
        }
    }
}

/**
 * Tarjeta de opción ancha para el panel de control.
 *
 * @param title Título de la opción.
 * @param description Descripción detallada de la opción.
 */
@Composable
private fun WideOptionCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(WIDE_CARD_HEIGHT.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(20.dp))
                .clickable { onClick() }
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp),
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                fontSize = 10.sp,
            )
        }
    }
}
