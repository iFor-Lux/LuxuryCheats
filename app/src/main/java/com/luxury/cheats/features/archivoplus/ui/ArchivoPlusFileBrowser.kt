package com.luxury.cheats.features.archivoplus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.features.archivoplus.logic.BrowserMode

@Composable
fun ArchivoPlusFileBrowser(
    currentPath: String,
    items: List<String>,
    isLoading: Boolean,
    selectedItems: Set<String> = emptySet(),
    mode: BrowserMode = BrowserMode.SELECT_PATH,
    onFolderClick: (String) -> Unit,
    onItemClick: (String) -> Unit, // Para seleccionar archivos
    onBackClick: () -> Unit,
    onConfirmClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight(0.85f)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Cabecera del Navegador
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Atrás",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = when(mode) {
                        BrowserMode.SELECT_FILE -> "Seleccionar Archivo"
                        BrowserMode.SELECT_ZIP -> "Seleccionar ZIP/RAR"
                        BrowserMode.SELECT_MULTIPLE -> "Seleccionar Archivos"
                        BrowserMode.SELECT_PATH -> "Seleccionar Destino"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = currentPath,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Lista de Archivos
        Box(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (items.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Carpeta vacía o sin acceso",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(items) { item ->
                        FileItem(
                            name = item,
                            isSelected = selectedItems.contains(item),
                            isMultipleMode = mode == BrowserMode.SELECT_MULTIPLE,
                            onClick = {
                                if (item.endsWith("/")) {
                                    onFolderClick(item)
                                } else if (mode != BrowserMode.SELECT_PATH) {
                                    onItemClick(item)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Botón de Confirmación
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = { onConfirmClick(currentPath) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = when(mode) {
                    BrowserMode.SELECT_PATH -> true
                    else -> selectedItems.isNotEmpty()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when(mode) {
                        BrowserMode.SELECT_PATH -> "SELECCIONAR ESTA RUTA"
                        BrowserMode.SELECT_MULTIPLE -> "CONFIRMAR SELECCIÓN (${selectedItems.size})"
                        else -> "CONFIRMAR ARCHIVO"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FileItem(
    name: String,
    isSelected: Boolean,
    isMultipleMode: Boolean,
    onClick: () -> Unit
) {
    val isFolder = name.endsWith("/")
    val displayName = name.removeSuffix("/")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = when {
                isSelected -> MaterialTheme.colorScheme.primary
                isFolder -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = when {
                        isSelected && isMultipleMode -> Icons.Default.CheckBox
                        isSelected -> Icons.Default.CheckCircle
                        isFolder -> Icons.Default.Folder
                        else -> Icons.Default.Description
                    },
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (isSelected) Color.White else if (isFolder) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (isSelected && isMultipleMode) {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
