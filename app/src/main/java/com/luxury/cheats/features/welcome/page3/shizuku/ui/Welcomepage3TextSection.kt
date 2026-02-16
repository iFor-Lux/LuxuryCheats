package com.luxury.cheats.features.welcome.page3.shizuku.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.core.theme.newsreaderItalicFamily

/**
 * Sección de texto (título y descripción inicial) para la página de Shizuku.
 */
@Composable
fun welcomePage3TextSection() {
    val titleColor = MaterialTheme.colorScheme.onSurface
    val highlightColor = MaterialTheme.colorScheme.tertiary
    val descriptionColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)

    Column(
        modifier = Modifier.width(300.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
    ) {
        // Título
        Text(
            text = "SHIZUKU",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = titleColor,
            modifier = Modifier.align(androidx.compose.ui.Alignment.Start),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción principal
        Text(
            text =
                buildAnnotatedString {
                    append("Antes de utilizar la aplicación por favor asegurece de tener instalado y activado ")
                    withStyle(style = SpanStyle(color = highlightColor, fontWeight = FontWeight.SemiBold, fontFamily = newsreaderItalicFamily, fontSize = 14.sp)) {
                        append("Shizuku")
                    }
                    append(" en tu dispositivo.")
                },
            fontSize = 13.sp,
            color = descriptionColor,
            lineHeight = 18.sp,
            modifier = Modifier.align(androidx.compose.ui.Alignment.Start),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Nota adicional
        Text(
            text = "Es necesario para el correcto funcionamiento de los módulos.",
            fontSize = 13.sp,
            color = descriptionColor,
            lineHeight = 18.sp,
            modifier = Modifier.align(androidx.compose.ui.Alignment.Start),
        )
    }
}
