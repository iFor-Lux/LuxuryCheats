package com.luxury.cheats.features.archivoplus.logic

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luxury.cheats.services.shizuku.ShizukuFileUtil
import com.luxury.cheats.services.shizuku.ShizukuService
import com.luxury.cheats.services.storage.UserPreferencesService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ArchivoPlusViewModel @Inject constructor(
    private val preferencesService: UserPreferencesService,
    private val shizukuFileUtil: ShizukuFileUtil,
    private val shizukuService: ShizukuService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArchivoPlusState())
    val uiState: StateFlow<ArchivoPlusState> = _uiState.asStateFlow()

    private val restrictedExtensions = listOf("zip", "rar", "7z")

    init {
        loadUserTier()
    }

    private fun loadUserTier() {
        val cache = preferencesService.accessProfileCache()
        val isLicenseMode = preferencesService.accessLicenseMode()
        val tier = cache?.get("tier") ?: if (isLicenseMode) "free" else "vip"
        val hasPremiumAccess = tier.equals("vip", ignoreCase = true) || tier.equals("plus", ignoreCase = true)
        _uiState.update { it.copy(isVip = hasPremiumAccess) }
    }

    fun onAction(action: ArchivoPlusAction) {
        when (action) {
            is ArchivoPlusAction.SelectFile -> onAction(ArchivoPlusAction.ToggleBrowser(true, BrowserMode.SELECT_FILE))
            is ArchivoPlusAction.SelectZip -> onAction(ArchivoPlusAction.ToggleBrowser(true, BrowserMode.SELECT_ZIP))
            is ArchivoPlusAction.SelectMultipleFiles -> onAction(ArchivoPlusAction.ToggleBrowser(true, BrowserMode.SELECT_MULTIPLE))
            is ArchivoPlusAction.SelectPath -> onAction(ArchivoPlusAction.ToggleBrowser(true, BrowserMode.SELECT_PATH))
            
            is ArchivoPlusAction.ToggleBrowser -> {
                if (action.show) {
                    _uiState.update { it.copy(browserMode = action.mode, selectedBrowserItems = emptySet()) }
                    checkShizukuAndOpenBrowser()
                } else {
                    _uiState.update { it.copy(showBrowser = false) }
                }
            }

            is ArchivoPlusAction.ToggleItemSelection -> handleToggleItemSelection(action.itemName)
            is ArchivoPlusAction.ConfirmBrowserSelection -> handleConfirmBrowserSelection(action.path)
            
            is ArchivoPlusAction.NavigateToFolder -> {
                val cleanFolderName = action.folderName.removeSuffix("/")
                val currentPath = _uiState.value.currentBrowserPath.removeSuffix("/")
                val newPath = "$currentPath/$cleanFolderName".replace("//", "/")
                loadBrowserPath(newPath)
            }
            is ArchivoPlusAction.NavigateUp -> handleNavigateUp()

            // Ejecución
            is ArchivoPlusAction.ExecuteOperation -> prepareExecution()
            is ArchivoPlusAction.OnPasswordChange -> _uiState.update { it.copy(zipPassword = action.password) }
            is ArchivoPlusAction.ConfirmPassword -> runExecution(true)
            is ArchivoPlusAction.CancelPassword -> _uiState.update { it.copy(showPasswordDialog = false, zipPassword = "") }
            is ArchivoPlusAction.DismissSuccess -> _uiState.update { it.copy(successMessage = null) }

            // Internals
            is ArchivoPlusAction.OnFileSelected -> _uiState.update { it.copy(selectedFileUri = action.uri, selectedFileName = action.name) }
            is ArchivoPlusAction.OnMultipleFilesSelected -> _uiState.update { it.copy(selectedFilesUris = action.uris, selectedFilesNames = action.names) }
            is ArchivoPlusAction.OnZipSelected -> _uiState.update { it.copy(selectedZipUri = action.uri, selectedZipName = action.name) }
            is ArchivoPlusAction.OnPathChanged -> _uiState.update { it.copy(destinationPath = action.path) }
            is ArchivoPlusAction.ConfirmSelectedPath -> onAction(ArchivoPlusAction.ConfirmBrowserSelection(action.path))
        }
    }

    private fun prepareExecution() {
        val state = _uiState.value
        if (state.destinationPath.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Por favor selecciona una ruta de destino.", isVipError = false) }
            return
        }

        val hasNormalSelection = state.selectedFileName != null || state.selectedFilesNames.isNotEmpty()
        val hasZipSelection = state.selectedZipName != null

        if (!hasNormalSelection && !hasZipSelection) {
            _uiState.update { it.copy(errorMessage = "No has seleccionado ningún archivo para mover.", isVipError = false) }
            return
        }

        // Si hay un ZIP, verificamos si necesita contraseña antes de ejecutar todo
        if (hasZipSelection) {
            checkZipPasswordAndRun()
        } else {
            runExecution()
        }
    }

    private fun checkZipPasswordAndRun() {
        viewModelScope.launch {
            val zipPath = _uiState.value.selectedZipUri?.path ?: return@launch
            // Comando para probar si tiene contraseña: unzip -t -P "" (test con pass vacío)
            val result = shizukuService.executeCommand("unzip -t -P \"\" \"$zipPath\"")
            
            when (result) {
                is ShizukuService.StringResult.Success -> {
                    // Si unzip -t -P "" funciona (o no da error de pass), probablemente no tiene pass o pass es vacío
                    runExecution()
                }
                is ShizukuService.StringResult.Error -> {
                    // Si da error, verificamos si es por contraseña (esto depende de la versión de unzip, pero usualmente 81 o similar)
                    if (result.message.contains("password", ignoreCase = true) || result.message.contains("incorrect", ignoreCase = true)) {
                        _uiState.update { it.copy(showPasswordDialog = true) }
                    } else {
                        // Otro error técnico, intentamos ejecutar igual o mostramos error
                        runExecution()
                    }
                }
            }
        }
    }

    private fun runExecution(withPassword: Boolean = false) {
        _uiState.update { it.copy(isExecuting = true, showPasswordDialog = false) }
        
        viewModelScope.launch {
            val state = _uiState.value
            val dest = state.destinationPath
            var anyError = false
            var movedCount = 0

            // 1. Mover Archivo Individual
            state.selectedFileName?.let { name ->
                val source = state.selectedFileUri?.path ?: ""
                if (shizukuFileUtil.moveFile(source, "$dest/$name")) {
                    movedCount++
                } else {
                    anyError = true
                }
            }

            // 2. Mover Múltiples Archivos
            state.selectedFilesNames.forEachIndexed { index, name ->
                val source = state.selectedFilesUris[index].path ?: ""
                if (shizukuFileUtil.moveFile(source, "$dest/$name")) {
                    movedCount++
                } else {
                    anyError = true
                }
            }

            // 3. Descomprimir ZIP
            state.selectedZipName?.let { name ->
                val source = state.selectedZipUri?.path ?: ""
                val passFlag = if (withPassword && state.zipPassword.isNotEmpty()) "-P \"${state.zipPassword}\"" else ""
                // unzip -o (overwrite) -d (dir)
                val cmd = "unzip -o $passFlag \"$source\" -d \"$dest\""
                val zipResult = shizukuService.executeCommand(cmd)
                
                if (zipResult is ShizukuService.StringResult.Success) {
                    movedCount++
                } else {
                    anyError = true
                    if (zipResult is ShizukuService.StringResult.Error) {
                         _uiState.update { it.copy(errorMessage = "Error ZIP: ${zipResult.message}", isVipError = false) }
                    }
                }
            }

            _uiState.update { it.copy(
                isExecuting = false,
                successMessage = if (!anyError) "Operación finalizada con éxito. Se procesaron $movedCount elementos." else null,
                errorMessage = if (anyError && it.errorMessage == null) "Hubo errores en algunos archivos. Verifica los permisos." else it.errorMessage,
                // Limpiar selección tras éxito total
                selectedFileName = if (!anyError) null else it.selectedFileName,
                selectedFilesNames = if (!anyError) emptyList() else it.selectedFilesNames,
                selectedZipName = if (!anyError) null else it.selectedZipName,
                zipPassword = ""
            ) }
        }
    }

    // --- Browser Helpers ---

    private fun handleToggleItemSelection(itemName: String) {
        val mode = _uiState.value.browserMode
        if (itemName.endsWith("/")) return 

        _uiState.update { state ->
            val current = state.selectedBrowserItems
            val updated = if (mode == BrowserMode.SELECT_MULTIPLE) {
                if (current.contains(itemName)) current - itemName else current + itemName
            } else {
                setOf(itemName)
            }
            state.copy(selectedBrowserItems = updated)
        }
    }

    private fun handleConfirmBrowserSelection(path: String) {
        val state = _uiState.value
        val mode = state.browserMode
        val selectedItems = state.selectedBrowserItems

        when (mode) {
            BrowserMode.SELECT_PATH -> _uiState.update { it.copy(destinationPath = path, showBrowser = false) }
            BrowserMode.SELECT_FILE -> {
                val fileName = selectedItems.firstOrNull() ?: return
                validateAndSetFileFromBrowser(path, fileName)
            }
            BrowserMode.SELECT_ZIP -> {
                val fileName = selectedItems.firstOrNull() ?: return
                validateAndSetZipFromBrowser(path, fileName)
            }
            BrowserMode.SELECT_MULTIPLE -> validateAndSetMultipleFilesFromBrowser(path, selectedItems.toList())
        }
    }

    private fun validateAndSetFileFromBrowser(path: String, fileName: String) {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        if (restrictedExtensions.contains(extension)) {
            _uiState.update { it.copy(errorMessage = "Solo los clientes VIP pueden mover muchos archivos o archivos ZIP, RAR, 7Z", isVipError = true) }
            return
        }
        _uiState.update { it.copy(selectedFileUri = Uri.fromFile(File("$path/$fileName")), selectedFileName = fileName, showBrowser = false) }
    }

    private fun validateAndSetZipFromBrowser(path: String, fileName: String) {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        if (!restrictedExtensions.contains(extension)) {
            _uiState.update { it.copy(errorMessage = "Esta sección es solo para archivos ZIP, RAR o 7Z.", isVipError = false) }
            return
        }
        if (!_uiState.value.isVip) {
            _uiState.update { it.copy(errorMessage = "Solo los clientes VIP pueden mover muchos archivos o archivos ZIP, RAR, 7Z", isVipError = true) }
            return
        }
        _uiState.update { it.copy(selectedZipUri = Uri.fromFile(File("$path/$fileName")), selectedZipName = fileName, showBrowser = false) }
    }

    private fun validateAndSetMultipleFilesFromBrowser(path: String, fileNames: List<String>) {
        if (!_uiState.value.isVip) {
            _uiState.update { it.copy(errorMessage = "Solo los clientes VIP pueden mover muchos archivos o archivos ZIP, RAR, 7Z", isVipError = true) }
            return
        }
        if (fileNames.isEmpty()) return
        val hasRestricted = fileNames.any { restrictedExtensions.contains(it.substringAfterLast('.', "").lowercase()) }
        if (hasRestricted) {
            _uiState.update { it.copy(errorMessage = "Esta sección no permite archivos ZIP, RAR o 7Z.", isVipError = false) }
            return
        }
        _uiState.update { it.copy(selectedFilesUris = fileNames.map { Uri.fromFile(File("$path/$it")) }, selectedFilesNames = fileNames, showBrowser = false) }
    }

    private fun checkShizukuAndOpenBrowser() {
        if (!shizukuService.isShizukuAvailable()) {
            _uiState.update { it.copy(errorMessage = "Shizuku no está disponible. Por favor, inícialo primero.", isVipError = false) }
            return
        }
        if (!shizukuService.hasPermission()) {
            shizukuService.requestPermission { granted ->
                if (granted) {
                    _uiState.update { it.copy(showBrowser = true) }
                    loadBrowserPath(_uiState.value.currentBrowserPath)
                } else {
                    _uiState.update { it.copy(errorMessage = "Permiso de Shizuku denegado.", isVipError = false) }
                }
            }
        } else {
            _uiState.update { it.copy(showBrowser = true) }
            if (_uiState.value.browserItems.isEmpty()) loadBrowserPath(_uiState.value.currentBrowserPath)
        }
    }

    private fun loadBrowserPath(path: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isBrowserLoading = true, currentBrowserPath = path, selectedBrowserItems = emptySet()) }
            shizukuFileUtil.listFiles(path).onSuccess { items ->
                _uiState.update { it.copy(browserItems = items, isBrowserLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isBrowserLoading = false, browserItems = emptyList(), errorMessage = "Error: ${error.message}", isVipError = false) }
            }
        }
    }

    private fun handleNavigateUp() {
        val currentPath = _uiState.value.currentBrowserPath.removeSuffix("/")
        if (currentPath == "/" || currentPath == "/sdcard" || currentPath.isEmpty()) return
        val parentPath = currentPath.substringBeforeLast("/")
        val finalParent = if (parentPath.isEmpty()) "/" else parentPath
        loadBrowserPath(finalParent)
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null, isVipError = false) }
    }
}
