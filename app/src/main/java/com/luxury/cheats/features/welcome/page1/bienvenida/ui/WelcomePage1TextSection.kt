package com.luxury.cheats.features.welcome.page1.bienvenida.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SUBTITLE_HORIZONTAL_PADDING = 20.dp

/**
 * Sección de texto de bienvenida (título y subtítulo) para la primera página.
 *
 * @param modifier Modificador de Compose.
 */
@Composable
fun welcomePage1TextSection(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        @Suppress("MagicNumber")
        Text(
            text = "Bienvenido a\nLuxury.Reg !!!",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            lineHeight = 48.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "La mejor herramienta para dominar Free Fire\n con cheats avanzados y seguros",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = SUBTITLE_HORIZONTAL_PADDING),
        )
    }
}
