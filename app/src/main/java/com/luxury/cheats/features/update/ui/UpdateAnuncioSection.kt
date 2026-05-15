package com.luxury.cheats.features.update.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.luxury.cheats.core.theme.LuxuryCheatsTheme

private const val INDICATOR_HEIGHT_RATIO = 0.3f
private const val SCROLL_MOVEMENT_APPROX = 300f

/**
 * Widget de anuncio de actualización diseñado con estética Premium.
 * - Dimensiones fijas: 350dp x 394dp.
 * - Soporte para Material You y temas Claro/Oscuro.
 */
@Composable
fun UpdateAnuncioSection(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    imageUrl: String = "",
    onUpdateClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .width(350.dp)
                .heightIn(min = 200.dp, max = 600.dp)
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
            modifier =
                Modifier
                    .heightIn(max = 550.dp) // Límite de altura para que el peso funcione
                    .padding(bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            UpdateBanner(imageUrl)
            UpdateTitle(title)
            UpdateDescription(
                description = description,
                modifier = Modifier.weight(1f, fill = false),
            )
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            UpdateButton(onUpdateClick)
        }
    }
}

@Composable
private fun UpdateBanner(imageUrl: String) {
    val isPreview = LocalInspectionMode.current
    Box(
        modifier =
            Modifier
                .size(width = 300.dp, height = 140.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (isPreview || imageUrl.isEmpty()) {
            // Placeholder para evitar errores de renderizado con AVIF en el Preview o si no hay URL
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
            AsyncImage(
                model = imageUrl,
                contentDescription = "Update Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun UpdateTitle(title: String) {
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
private fun UpdateDescription(
    description: String,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    // Formatear cada línea con el símbolo de check premium
    val formattedDescription =
        description.lines()
            .filter { it.isNotBlank() }
            .joinToString("\n") { "✓ $it" }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
    ) {
        Text(
            text = formattedDescription,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Start,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(end = 12.dp),
        )

        // Barra de desplazamiento minimalista (Estética Premium)
        if (scrollState.maxValue > 0) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd)
                        .width(4.dp)
                        .fillMaxHeight()
                        .padding(vertical = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(2.dp),
                        ),
            ) {
                val scrollRatio =
                    if (scrollState.maxValue > 0) {
                        scrollState.value.toFloat() / scrollState.maxValue.toFloat()
                    } else {
                        0f
                    }

                // Indicador de posición
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(INDICATOR_HEIGHT_RATIO) // Tamaño fijo del 30% para el indicador
                            .align(Alignment.TopCenter)
                            .graphicsLayer(
                                translationY = scrollRatio * SCROLL_MOVEMENT_APPROX, // Aproximación de movimiento
                            )
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(2.dp),
                            ),
                )
            }
        }
    }
}

@Composable
private fun UpdateButton(onClick: () -> Unit) {
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
fun UpdateAnuncioSectionPreviewDark() {
    LuxuryCheatsTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(Modifier.padding(20.dp)) {
                UpdateAnuncioSection(
                    title = "NUEVA ACTUALIZACION",
                    description = "Actualiza la aplicación hoy mismo para desbloquear " +
                        "funciones increíbles y disfrutar de mejoras significativas.",
                    onUpdateClick = {},
                )
            }
        }
    }
}

/** Preview del anuncio de actualización en tema claro. */
@Preview(name = "Update Anuncio Light", showBackground = true)
@Composable
fun UpdateAnuncioSectionPreviewLight() {
    LuxuryCheatsTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(Modifier.padding(20.dp)) {
                UpdateAnuncioSection(
                    title = "NUEVA ACTUALIZACION",
                    description = "Actualiza la aplicación hoy mismo para desbloquear " +
                        "funciones increíbles y disfrutar de mejoras significativas.",
                    imageUrl = "",
                    onUpdateClick = {},
                )
            }
        }
    }
}
