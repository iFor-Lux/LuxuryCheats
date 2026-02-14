package com.luxury.cheats.features.login.pantalla.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BORDER_GRAY_HEX = Color(0xFF303030)
private val TEXT_GRAY_HEX = Color(0xFF777777)

/**
 * Sección reservada para una imagen o banner en la pantalla de login.
 *
 * @param modifier Modificador de Compose.
 */
@Composable
fun loginPantallaImagenSection(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .width(412.dp)
                .height(115.dp)
                .background(BORDER_GRAY_HEX, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        // Placeholder para la imagen
        Text(
            text = "Imagen pendiente",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = TEXT_GRAY_HEX,
        )
    }
}
