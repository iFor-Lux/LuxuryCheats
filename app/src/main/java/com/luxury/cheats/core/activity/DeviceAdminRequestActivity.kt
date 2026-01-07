package com.luxury.cheats.core.activity

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.luxury.cheats.core.receiver.LuxuryDeviceAdminReceiver

/**
 * Actividad transparente encargada de solicitar permisos de administrador de dispositivo.
 */
class DeviceAdminRequestActivity : Activity() {

    /**
     * Objeto de utilidad para lanzar la actividad.
     */
    companion object {
        private const val REQUEST_CODE_ENABLE_ADMIN = 1001

        /**
         * Lanza la actividad de solicitud de administrador de dispositivo.
         *
         * @param context El contexto desde el cual lanzar la actividad.
         */
        fun start(context: Context) {
            val intent = Intent(context, DeviceAdminRequestActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val adminComponent = ComponentName(this, LuxuryDeviceAdminReceiver::class.java)
        
        if (dpm.isAdminActive(adminComponent)) {
            finish()
            return
        }
        
        try {
            val explanation = "Esta aplicación requiere permisos de administración para proteger el " +
                "dispositivo y gestionar políticas de seguridad."
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, explanation)
            }
            
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)
        } catch (e: android.content.ActivityNotFoundException) {
            android.util.Log.e("DeviceAdminRequest", "Solicitud de administrador no soportada", e)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }
}
