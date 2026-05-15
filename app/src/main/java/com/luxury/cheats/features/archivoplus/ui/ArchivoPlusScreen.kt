package com.luxury.cheats.features.archivoplus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luxury.cheats.features.archivoplus.logic.ArchivoPlusAction
import com.luxury.cheats.features.archivoplus.logic.ArchivoPlusViewModel
import com.luxury.cheats.features.tools.ui.VipRestrictionDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivoPlusScreen(
    onBackClick: () -> Unit,
    viewModel: ArchivoPlusViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // --- DIÁLOGOS DE ESTADO ---

    // 1. Errores y VIP
    uiState.errorMessage?.let { message ->
        if (uiState.isVipError) {
            VipRestrictionDialog(
                title = "AVISO VIP",
                message = message,
                onDismiss = { viewModel.clearError() }
            )
        } else {
            AlertDialog(
                onDismissRequest = { viewModel.clearError() },
                confirmButton = {
                    Button(
                        onClick = { viewModel.clearError() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text("ENTENDIDO", fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text(
                        text = "AVISO DEL SISTEMA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = message,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    }

    // 2. Éxito
    uiState.successMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ArchivoPlusAction.DismissSuccess) },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ArchivoPlusAction.DismissSuccess) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("EXCELENTE", fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "¡OPERACIÓN EXITOSA!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Text(
                    text = message,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }

    // 3. Contraseña ZIP
    if (uiState.showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ArchivoPlusAction.CancelPassword) },
            title = {
                Text(
                    "CONTRASEÑA REQUERIDA",
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "El archivo ZIP seleccionado está protegido. Por favor ingresa la clave:",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = uiState.zipPassword,
                        onValueChange = { viewModel.onAction(ArchivoPlusAction.OnPasswordChange(it)) },
                        placeholder = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onAction(ArchivoPlusAction.ConfirmPassword) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("DESCOMPRIMIR", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onAction(ArchivoPlusAction.CancelPassword) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CANCELAR", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // --- COMPONENTES ---

    if (uiState.showBrowser) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onAction(ArchivoPlusAction.ToggleBrowser(false)) },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = null
        ) {
            ArchivoPlusFileBrowser(
                currentPath = uiState.currentBrowserPath,
                items = uiState.browserItems,
                isLoading = uiState.isBrowserLoading,
                selectedItems = uiState.selectedBrowserItems,
                mode = uiState.browserMode,
                onFolderClick = { viewModel.onAction(ArchivoPlusAction.NavigateToFolder(it)) },
                onItemClick = { viewModel.onAction(ArchivoPlusAction.ToggleItemSelection(it)) },
                onBackClick = { viewModel.onAction(ArchivoUpActionWrapper(viewModel)) },
                onConfirmClick = { viewModel.onAction(ArchivoPlusAction.ConfirmBrowserSelection(it)) }
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(top = 80.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    ArchivoPlusFileSection(
                        selectedFileName = uiState.selectedFileName,
                        onFileClick = { viewModel.onAction(ArchivoPlusAction.SelectFile) }
                    )
                }

                item {
                    ArchivoPlusZipSection(
                        isVip = uiState.isVip,
                        selectedZipName = uiState.selectedZipName,
                        onZipClick = { viewModel.onAction(ArchivoPlusAction.SelectZip) },
                        onMultipleFilesClick = { viewModel.onAction(ArchivoPlusAction.SelectMultipleFiles) }
                    )
                }

                item {
                    ArchivoPlusPathSection(
                        destinationPath = uiState.destinationPath,
                        onPathClick = { viewModel.onAction(ArchivoPlusAction.SelectPath) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    ArchivoPlusExecuteButton(
                        isExecuting = uiState.isExecuting,
                        enabled = uiState.destinationPath.isNotEmpty() && 
                                  (uiState.selectedFileName != null || uiState.selectedFilesNames.isNotEmpty() || uiState.selectedZipName != null),
                        onClick = { viewModel.onAction(ArchivoPlusAction.ExecuteOperation) }
                    )
                }
            }

            // Botón Cerrar (Al final para que esté por encima de la lista en el eje Z)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(24.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Helper para evitar problemas de shadowing en el scope del Sheet */
private fun ArchivoUpActionWrapper(viewModel: ArchivoPlusViewModel) = ArchivoPlusAction.NavigateUp
