package com.luxury.cheats.features.archivoplus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.core.theme.newsreaderItalicFamily

@Composable
fun ArchivoPlusZipSection(
    isVip: Boolean,
    selectedZipName: String?,
    onZipClick: () -> Unit,
    onMultipleFilesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text(
                text = "Exclusivo VIP",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = newsreaderItalicFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.tertiary // Usamos Tertiary para contraste
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Stars,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
        }

        VipButton(
            text = buildAnnotatedString {
                append("Coloca 1 o más ")
                withStyle(SpanStyle(
                    color = MaterialTheme.colorScheme.secondary, // Diferenciamos el color del resaltado
                    fontWeight = FontWeight.Bold,
                    fontFamily = newsreaderItalicFamily
                )) {
                    append("Archivo")
                }
                append(" aqui")
            },
            onClick = { onMultipleFilesClick() },
            isLocked = !isVip
        )

        Spacer(modifier = Modifier.height(12.dp))

        VipButton(
            text = if (selectedZipName != null) {
                buildAnnotatedString {
                    append("ZIP seleccionado: ")
                    withStyle(SpanStyle(
                        color = MaterialTheme.colorScheme.secondary, 
                        fontWeight = FontWeight.Bold,
                        fontFamily = newsreaderItalicFamily
                    )) {
                        append(selectedZipName)
                    }
                }
            } else {
                buildAnnotatedString {
                    append("Coloca un ")
                    withStyle(SpanStyle(
                        color = MaterialTheme.colorScheme.secondary, 
                        fontWeight = FontWeight.Bold,
                        fontFamily = newsreaderItalicFamily
                    )) {
                        append("Zip o Rar")
                    }
                    append(" aqui")
                }
            },
            onClick = { onZipClick() },
            isLocked = !isVip
        )
    }
}

@Composable
private fun VipButton(
    text: CharSequence,
    onClick: () -> Unit,
    isLocked: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isLocked) 0.3f else 1f),
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            if (isLocked) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text as? androidx.compose.ui.text.AnnotatedString ?: buildAnnotatedString { append(text.toString()) },
                style = MaterialTheme.typography.bodyLarge,
                color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
