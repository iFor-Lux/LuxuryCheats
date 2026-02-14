package com.luxury.cheats.features.welcome.page3.shizuku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Sección informativa que explica detalladamente la función de Shizuku.
 */
@Composable
fun welcomePage3Mensaje() {
    val containerColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface
    val highlightColor = MaterialTheme.colorScheme.tertiary

    Box(
        modifier =
            Modifier
                .size(width = 260.dp, height = 138.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(containerColor)
                .padding(16.dp),
    ) {
        Column {
            Text(
                text = "Que hace shizuku?",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text =
                    buildAnnotatedString {
                        append("Shizuku nos permite obtener acceso 100% a los archivos del juego - apartir de ")
                        withStyle(style = SpanStyle(color = highlightColor)) {
                            append("Android 13")
                        }
                        append(", el sistema operativo de Android bloqueó y privatizo el acceso a carpetas como ")
                        withStyle(style = SpanStyle(color = highlightColor)) {
                            append("DATA & OBB")
                        }
                        append(" con fines de privacidad. ")
                        withStyle(style = SpanStyle(color = highlightColor)) {
                            append("SHIZUKU")
                        }
                        append(" nos otorga permisos administrativos y acceso a esos documentos ")
                        append("para poder garantizar el uso correcto de la aplicación de ")
                        withStyle(style = SpanStyle(color = highlightColor)) {
                            append("Luxury Cheat's")
                        }
                        append(".")
                    },
                fontSize = 8.sp,
                color = textColor,
                lineHeight = 10.sp,
            )
        }
    }
}
