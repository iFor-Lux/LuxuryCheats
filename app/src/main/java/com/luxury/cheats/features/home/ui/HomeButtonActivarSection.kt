package com.luxury.cheats.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Sección de Botón Activar
 * - W: 228, H: 58, Corner: 25
 * - Borde naranja (FFAE00)
 */
@Composable
fun HomeButtonActivarSection(
    modifier: Modifier = Modifier,
    onActivarClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .width(228.dp)
            .height(58.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(25.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(25.dp)
            )
            .clickable { onActivarClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Activar",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
