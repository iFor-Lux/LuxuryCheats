package com.luxury.cheats.features.archivoplus.logic

import android.net.Uri

/**
 * Acciones de usuario para la pantalla Archivo Plus
 */
sealed class ArchivoPlusAction {
    object SelectFile : ArchivoPlusAction()
    object SelectZip : ArchivoPlusAction()
    object SelectMultipleFiles : ArchivoPlusAction()
    object SelectPath : ArchivoPlusAction()
    
    data class OnFileSelected(val uri: Uri, val name: String) : ArchivoPlusAction()
    data class OnMultipleFilesSelected(val uris: List<Uri>, val names: List<String>) : ArchivoPlusAction()
    data class OnZipSelected(val uri: Uri, val name: String) : ArchivoPlusAction()
    data class OnPathChanged(val path: String) : ArchivoPlusAction()

    // Acciones para el Mini Gestor de Archivos
    data class ToggleBrowser(val show: Boolean, val mode: BrowserMode = BrowserMode.SELECT_PATH) : ArchivoPlusAction()
    data class NavigateToFolder(val folderName: String) : ArchivoPlusAction()
    object NavigateUp : ArchivoPlusAction()
    data class ToggleItemSelection(val itemName: String) : ArchivoPlusAction()
    data class ConfirmBrowserSelection(val path: String) : ArchivoPlusAction()
    
    // Ejecución
    object ExecuteOperation : ArchivoPlusAction()
    data class OnPasswordChange(val password: String) : ArchivoPlusAction()
    object ConfirmPassword : ArchivoPlusAction()
    object CancelPassword : ArchivoPlusAction()
    object DismissSuccess : ArchivoPlusAction()

    data class ConfirmSelectedPath(val path: String) : ArchivoPlusAction()
}
