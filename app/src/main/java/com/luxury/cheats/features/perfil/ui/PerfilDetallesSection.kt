package com.luxury.cheats.features.perfil.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Sección que muestra los detalles técnicos y de cuenta del usuario.
 *
 * @param modifier Modificador de Compose.
 * @param state Estado actual del perfil con los datos a mostrar.
 */
@Composable
fun perfilDetallesSection(
    modifier: Modifier = Modifier,
    state: com.luxury.cheats.features.perfil.logic.PerfilState = com.luxury.cheats.features.perfil.logic.PerfilState(),
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Detalles",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 12.dp, start = 8.dp),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 12.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                detailItem(label = "FECHA DE CREACION", value = state.creationDate)
                detailItem(label = "HORA DE CREACION", value = state.creationHour)
                detailItem(label = "FECHA DE VENCIMIENTO", value = state.expiryDate)
                detailItem(label = "MODELO", value = state.model)
                detailItem(label = "RAM", value = state.ram)
                detailItem(label = "TARGET SDK", value = state.targetSdk)
                detailItem(label = "ARQUITECTURA", value = state.architecture, isLast = true)
            }
        }
    }
}

/**
 * Item individual de la lista de detalles.
 *
 * @param label Etiqueta o nombre del detalle.
 * @param value Valor del detalle.
 * @param isLast Indica si es el último elemento para ocultar el divisor.
 */
@Composable
fun detailItem(
    label: String,
    value: String = "---",
    isLast: Boolean = false,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        if (!isLast) {
            HorizontalDivider(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                thickness = 0.8.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            )
        }
    }
}
