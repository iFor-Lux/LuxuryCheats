package com.luxury.cheats.features.widgets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.R

/**
 * DownloadArchivoSection - Widget Bottom Sheet para descargas
 * Diseño solicitado: Box 340x223 dp, info de archivo y progreso con estilo premium.
 */
/**
 * Bottom Sheet que muestra el progreso de descarga de un archivo.
 *
 * @param onDismissRequest Callback cuando se solicita cerrar el bottom sheet.
 * @param sheetState Estado del bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadArchivoBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null // Remove default handle that user doesn't like
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Minimalist Custom Handle
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 30.dp, height = 4.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            )

            DownloadArchivoContent()
        }
    }
}

/**
 * Contenido principal del widget de descarga.
 * Muestra la información del archivo, icono de descarga y barra de progreso.
 *
 * @param modifier Modificador de Compose.
 */
@Composable
fun DownloadArchivoContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Contenedor principal 340x223
        Box(
            modifier = Modifier
                .size(width = 340.dp, height = 223.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FileInfoRow(
                    fileName = "Aimbot.txt",
                    fileWeight = "97.22kb",
                    imageResId = R.drawable.sprit1
                )

                DownloadProgressBox(
                    progress = 0.75f,
                    statusText = "Descargando"
                )
            }
        }
    }
}

@Composable
private fun FileInfoRow(
    fileName: String,
    fileWeight: String,
    imageResId: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Columna 1: Imagen 101x101
        Box(
            modifier = Modifier
                .size(101.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "File Preview",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Columna 2: Textos (Nombre + Peso)
        Column {
            Text(
                text = fileName,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = fileWeight,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DownloadProgressBox(
    progress: Float,
    statusText: String
) {
    // Animación del progreso para mayor suavidad (evita saltos "pixelados")
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "wavy_progress_animation"
    )

    Box(
        modifier = Modifier
            .size(width = 300.dp, height = 75.dp) // Un poco más de altura para la onda
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            DownloadProgressHeader(
                progress = progress, // Mostramos el porcentaje real sin animación para precisión de texto
                statusText = statusText
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearWavyProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                wavelength = 25.dp,
                amplitude = { 2f } // Lambda que devuelve amplitud fija
            )
        }
    }
}

@Composable
private fun DownloadProgressHeader(
    progress: Float,
    statusText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
        Text(
            text = "${(progress * 100).toInt()}%",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
