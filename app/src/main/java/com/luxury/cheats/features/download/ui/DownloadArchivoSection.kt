package com.luxury.cheats.features.download.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luxury.cheats.features.download.logic.DownloadAction
import com.luxury.cheats.features.download.logic.DownloadParams
import com.luxury.cheats.features.download.logic.DownloadStatus
import com.luxury.cheats.features.download.logic.DownloadViewModel

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
    params: DownloadParams,
    onDismissRequest: () -> Unit,
    viewModel: DownloadViewModel = viewModel(),
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Al abrir el bottom sheet, iniciamos la configuración si es un cheat nuevo
    androidx.compose.runtime.LaunchedEffect(params.cheatName) {
        viewModel.onAction(
            DownloadAction.StartSetup(
                params.cheatName,
                params.directUrl,
                params.directPath,
                params.preloadedWeight
            )
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.5.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            )
        }
    ) {
        DownloadArchivoContent(
            fileName = uiState.fileName,
            fileWeight = uiState.fileWeight,
            progress = uiState.progress,
            status = uiState.status
        )
    }
}

/**
 * Contenido interno de la sección de descarga.
 * @param fileName Nombre del archivo.
 * @param fileWeight Tamaño del archivo.
 * @param progress Progreso actual (0.0 a 1.0).
 * @param status Estado de la descarga.
 * @param modifier Modificador de layout.
 */
@Composable
fun DownloadArchivoContent(
    fileName: String,
    fileWeight: String,
    progress: Float,
    status: DownloadStatus,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Espacio garantizado entre el tirador y el box para máxima separación (Premium spacing)
        Spacer(modifier = Modifier.height(15.dp))

        // Contenedor principal 340x223
        Box(
            modifier = Modifier
                .size(width = 340.dp, height = 223.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val statusText = when (status) {
                    DownloadStatus.COMPLETED -> "Listo"
                    else -> "Descargando"
                }

                FileInfoRow(
                    fileName = fileName,
                    fileWeight = fileWeight
                )

                DownloadProgressBox(
                    progress = progress,
                    statusText = statusText
                )
            }
        }
    }
}

@Composable
private fun FileInfoRow(
    fileName: String,
    fileWeight: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Columna 1: Placeholder 101x101 (Removed local resource)
        Box(
            modifier = Modifier
                .size(101.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        )

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
