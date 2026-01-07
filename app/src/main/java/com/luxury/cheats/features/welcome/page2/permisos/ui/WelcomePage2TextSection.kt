package com.luxury.cheats.features.welcome.page2.permisos.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.isSystemInDarkTheme

/**
 * Sección de texto descriptivo para la página de permisos.
 */
@Composable
fun WelcomePage2TextSection() {
    val isDark = isSystemInDarkTheme()
    val titleColor = if (isDark) Color.White else Color.Black
    val descriptionColor = if (isDark) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.6f)

    Column(modifier = Modifier.width(260.dp)) {
        Text(
            text = "PERMISOS",
            color = titleColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Para utilizar completamente las funciones de Luxury, necesitamos " +
                "tu permiso para acceder a funciones del dispositivo.",
            color = descriptionColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.sp
        )
    }
}
