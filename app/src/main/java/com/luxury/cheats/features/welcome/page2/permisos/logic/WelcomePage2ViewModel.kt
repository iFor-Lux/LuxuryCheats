package com.luxury.cheats.features.welcome.page2.permisos.logic

import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.flow.update

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import com.luxury.cheats.core.receiver.LuxuryDeviceAdminReceiver
import kotlinx.coroutines.flow.update

/**
 * ViewModel encargado de gestionar la lógica de permisos en la segunda página de bienvenida.
 * Se encarga de comprobar el estado de los permisos y lanzar los intents correspondientes para solicitarlos.
 *
 * @param application Referencia a la aplicación para acceder a servicios del sistema.
 */
class WelcomePage2ViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(WelcomePage2State())
    val uiState: StateFlow<WelcomePage2State> = _uiState.asStateFlow()

    private val adminComponent = ComponentName(application, LuxuryDeviceAdminReceiver::class.java)

    private val clickedActions = mutableSetOf<WelcomePage2Action>()

    init {
        checkAllPermissions()
    }

    /**
     * Comprueba el estado actual de todos los permisos requeridos y actualiza el estado de la UI.
     */
    fun checkAllPermissions() {
        val context = getApplication<Application>()
        _uiState.update { state ->
            val storageGranted = checkStoragePermission()
            val notificationsGranted = checkNotificationsPermission(context)
            val adminGranted = checkAdminPermission(context)
            val overlayGranted = checkOverlayPermission(context)

            state.copy(
                isStorageGranted = storageGranted,
                isStorageDenied = !storageGranted && clickedActions.contains(WelcomePage2Action.StorageClicked),
                
                isNotificationsDenied = !notificationsGranted &&
                    clickedActions.contains(WelcomePage2Action.NotificationsClicked),
                
                isAdminGranted = adminGranted,
                isAdminDenied = !adminGranted && clickedActions.contains(WelcomePage2Action.AdminClicked),
                
                isOverlayGranted = overlayGranted,
                isOverlayDenied = !overlayGranted && clickedActions.contains(WelcomePage2Action.OverlayClicked)
            )
        }
    }
    
    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            // Para versiones anteriores a Android 11, usamos el permiso estándar 
            // que normalmente se concede al instalar o mediante solicitud simple.
            true 
        }
    }

    private fun checkNotificationsPermission(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    private fun checkAdminPermission(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return dpm.isAdminActive(adminComponent)
    }

    private fun checkOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    /**
     * Maneja las acciones del usuario, como hacer clic en una sección de permiso.
     * Actualiza el registro de clics y lanza el proceso de solicitud del permiso.
     *
     * @param action El tipo de acción/permiso pulsado.
     */
    fun handleAction(action: WelcomePage2Action) {
        val context = getApplication<Application>()
        clickedActions.add(action)

        when (action) {
            WelcomePage2Action.OverlayClicked -> {
                val intent = getOverlayIntent(context)
                launchIntent(context, intent)
            }
            else -> {
                // Otros permisos se manejan por Activity o no requieren intent directo aquí
            }
        }
    }

    private fun launchIntent(context: Context, intent: Intent?) {
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }

    private fun getOverlayIntent(context: Context): Intent {
        return Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = Uri.parse("package:${context.packageName}")
        }
    }
}
