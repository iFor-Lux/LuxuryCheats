package com.luxury.cheats.features.login.pantalla.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val BORDER_GRAY_HEX = Color(0xFF303030)
private val TEXT_GRAY_HEX = Color(0xFF777777)

/**
 * Sección reservada para una imagen o banner en la pantalla de login.
 *
 * @param imageUrl URL de la imagen a mostrar.
 * @param modifier Modificador de Compose.
 */
@Composable
fun LoginPantallaImagenSection(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(140.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Login",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
            )
        } else {
            // Placeholder para la imagen
            Text(
                text = "Error al Cargar",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                color = TEXT_GRAY_HEX,
            )
        }
    }
}
