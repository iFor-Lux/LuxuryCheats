package com.luxury.cheats.features.update.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.core.theme.luxuryCheatsTheme

/**
 * Widget de anuncio de actualización diseñado con estética Premium.
 * - Dimensiones fijas: 350dp x 394dp.
 * - Soporte para Material You y temas Claro/Oscuro.
 */
@Composable
fun updateAnuncioSection(
    modifier: Modifier = Modifier,
    title: String = "NUEVA ACTUALIZACION",
    description: String =
        "Actualiza la aplicación hoy mismo para desbloquear funciones increíbles, " +
            "disfrutar de mejoras significativas en el rendimiento y descubrir una " +
            "experiencia de usuario optimizada en toda la plataforma.",
    onUpdateClick: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .width(350.dp)
                .heightIn(min = 394.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.46f),
                    shape = RoundedCornerShape(30.dp),
                )
                .padding(24.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            updateBanner()
            updateTitle(title)
            updateDescription(description)
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            updateButton(onUpdateClick)
        }
    }
}

@Composable
private fun updateBanner() {
    val isPreview = LocalInspectionMode.current
    Box(
        modifier =
            Modifier
                .size(width = 300.dp, height = 140.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (isPreview) {
            // Placeholder para evitar errores de renderizado con AVIF en el Preview
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Banner Placeholder",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            // Placeholder Box (Removed local resource)
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
            )
        }
    }
}

@Composable
private fun updateTitle(title: String) {
    Text(
        text = title.uppercase(),
        color = MaterialTheme.colorScheme.primary,
        // Material You impact
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun updateDescription(description: String) {
    Text(
        text = description,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        textAlign = TextAlign.Start,
        // Align to left
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
    )
}

@Composable
private fun updateButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 187.dp, height = 40.dp),
        shape = RoundedCornerShape(25.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(
            text = "IR A ACTUALIZAR",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

/** Preview del anuncio de actualización en tema oscuro. */
@Preview(name = "Update Anuncio Dark", showBackground = true)
@Composable
fun updateAnuncioSectionPreviewDark() {
    luxuryCheatsTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(Modifier.padding(20.dp)) {
                updateAnuncioSection()
            }
        }
    }
}

/** Preview del anuncio de actualización en tema claro. */
@Preview(name = "Update Anuncio Light", showBackground = true)
@Composable
fun updateAnuncioSectionPreviewLight() {
    luxuryCheatsTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(Modifier.padding(20.dp)) {
                updateAnuncioSection()
            }
        }
    }
}
