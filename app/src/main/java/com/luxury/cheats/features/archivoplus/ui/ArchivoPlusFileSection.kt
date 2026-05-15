package com.luxury.cheats.features.archivoplus.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.core.theme.newsreaderItalicFamily

@Composable
fun ArchivoPlusFileSection(
    selectedFileName: String?,
    onFileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Sube tu archivo externo",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                .clickable { onFileClick() }
                .padding(2.dp)
        ) {
            // Dibujamos el borde punteado
            val stroke = Stroke(
                width = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
            val borderColor = MaterialTheme.colorScheme.outlineVariant

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = borderColor,
                    style = stroke,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx())
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer, // Usamos secondaryContainer para variar
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (selectedFileName != null) {
                        buildAnnotatedString {
                            append("Archivo seleccionado: ")
                            withStyle(SpanStyle(
                                color = MaterialTheme.colorScheme.secondary, // Color resaltado diferenciado
                                fontWeight = FontWeight.Bold,
                                fontFamily = newsreaderItalicFamily
                            )) {
                                append(selectedFileName)
                            }
                        }
                    } else {
                        buildAnnotatedString {
                            append("Coloca 1 ")
                            withStyle(SpanStyle(
                                color = MaterialTheme.colorScheme.secondary, 
                                fontWeight = FontWeight.Bold,
                                fontFamily = newsreaderItalicFamily
                            )) {
                                append("Archivo")
                            }
                            append(" aqui")
                        }
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
