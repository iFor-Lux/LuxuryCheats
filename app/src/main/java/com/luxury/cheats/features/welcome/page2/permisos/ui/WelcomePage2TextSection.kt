package com.luxury.cheats.features.welcome.page2.permisos.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Sección de texto descriptivo para la página de permisos.
 */
@Composable
fun welcomePage2TextSection() {
    val titleColor = MaterialTheme.colorScheme.onSurface
    val descriptionColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier = Modifier.width(260.dp)) {
        Text(
            text = "PERMISOS",
            color = titleColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text =
                "Para utilizar completamente las funciones de Luxury, necesitamos " +
                    "tu permiso para acceder a funciones del dispositivo.",
            color = descriptionColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.sp,
        )
    }
}
