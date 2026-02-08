package com.luxury.cheats.features.update.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.luxury.cheats.core.theme.LuxuryCheatsTheme
import com.luxury.cheats.features.update.logic.UpdateAction
import com.luxury.cheats.features.update.logic.UpdateViewModel
import com.luxury.cheats.features.download.ui.DownloadArchivoBottomSheet

private const val DATE_LENGTH = 10

/**
 * Pantalla completa de descarga de actualización.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadUpdateScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val viewModel: UpdateViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val appContext = context.applicationContext
                val prefs = com.luxury.cheats.services.UserPreferencesService(appContext)
                val service = com.luxury.cheats.features.update.service.UpdateService()
                @Suppress("UNCHECKED_CAST")
                return UpdateViewModel(updateService = service, preferencesService = prefs) as T
            }
        }
    )
    val uiState = viewModel.uiState.collectAsState().value
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DownloadHeader(onBackClick)
        Spacer(modifier = Modifier.height(20.dp))
        DownloadMainCard(
            uiState = uiState,
            onDownloadClick = {
                viewModel.onAction(UpdateAction.DownloadClicked)
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
        DownloadNewsSection(uiState.appUpdate?.description ?: "")
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (uiState.showDownloadSheet) {
        DownloadArchivoBottomSheet(
            params = com.luxury.cheats.features.download.logic.DownloadParams(
                cheatName = "LuxuryUpdate.apk",
                directUrl = uiState.appUpdate?.downloadLink,
                directPath = "",
                preloadedWeight = ""
            ),
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            onDismissRequest = { viewModel.onAction(UpdateAction.DismissDownloadSheet) }
        )
    }
}

@Composable
private fun DownloadHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun DownloadMainCard(
    uiState: com.luxury.cheats.features.update.logic.UpdateState,
    onDownloadClick: () -> Unit
) {
    val hasUpdate = uiState.appUpdate != null &&
        uiState.appUpdate.version.isNotEmpty() &&
        uiState.appUpdate.version != uiState.appVersion

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (hasUpdate) Modifier.height(521.dp) else Modifier.wrapContentHeight())
            .clip(RoundedCornerShape(50.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.46f),
                shape = RoundedCornerShape(50.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DownloadAppIcon()
            Spacer(modifier = Modifier.height(24.dp))
            DownloadVersionInfo(uiState.appVersion, uiState.releaseDate)

            if (hasUpdate) {
                Spacer(modifier = Modifier.height(24.dp))
                DownloadDivider()
                Spacer(modifier = Modifier.height(24.dp))
                DownloadStatusInfo(uiState.appUpdate)
                Spacer(modifier = Modifier.weight(1f))
                DownloadActionButton(onClick = onDownloadClick)
            } else {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Tu sistema está actualizado",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DownloadAppIcon() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )
    }
}


@Composable
private fun DownloadVersionInfo(appVersion: String, releaseDate: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "v$appVersion",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Release $releaseDate",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DownloadDivider() {
    HorizontalDivider(
        modifier = Modifier.width(300.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    )
}

@Composable
private fun DownloadStatusInfo(update: com.luxury.cheats.features.update.logic.AppUpdate?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.Update,
            contentDescription = null,
            modifier = Modifier.size(30.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = "Actualizacion disponible",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Version ${update?.version ?: "..."}",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        val releaseText = if (update?.timestamp?.length ?: 0 >= DATE_LENGTH) {
            update?.timestamp?.substring(0, DATE_LENGTH)
        } else "..."

        Text(
            text = "Release $releaseText",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DownloadActionButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 300.dp, height = 60.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Text(
            text = "DESCARGAR",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DownloadNewsSection(description: String) {
    if (description.isBlank()) return

    // Dividimos por saltos de línea para crear puntos de novedades
    val lines = description.split("\n").filter { it.isNotBlank() }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Novedades",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp) // Altura mínima de 100dp, pero crece si es necesario
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                lines.forEach { line ->
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .size(4.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = line,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

/** Preview de la pantalla de descarga en tema oscuro. */
@Preview(name = "Download Update Dark", showSystemUi = true)
@Composable
fun DownloadUpdateScreenPreviewDark() {
    LuxuryCheatsTheme(darkTheme = true) {
        DownloadUpdateScreen()
    }
}

/** Preview de la pantalla de descarga en tema claro. */
@Preview(name = "Download Update Light", showSystemUi = true)
@Composable
fun DownloadUpdateScreenPreviewLight() {
    LuxuryCheatsTheme(darkTheme = false) {
        DownloadUpdateScreen()
    }
}
