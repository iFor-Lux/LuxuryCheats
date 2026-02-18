package com.luxury.cheats.services.security

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.luxury.cheats.MainActivity
import com.luxury.cheats.services.shizuku.ShizukuFileUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

/**
 * Servicio de primer plano que mantiene la app activa y limpia archivos al cerrar.
 */
@AndroidEntryPoint
class SecurityService : Service() {
    @Inject lateinit var securityRepository: SecurityRepository

    @Inject lateinit var shizukuFileUtil: ShizukuFileUtil

    @Inject lateinit var shizukuService: com.luxury.cheats.services.shizuku.ShizukuService

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Constantes internas del servicio de seguridad.
     */
    companion object {
        private const val CHANNEL_ID = "security_channel"
        private const val NOTIFICATION_ID = 1337
        private const val CLEANUP_TIMEOUT_MS = 5000L
    }

    @Suppress("TooGenericExceptionCaught")
    override fun onCreate() {
        super.onCreate()
        try {
            createNotificationChannel()
        } catch (e: IllegalArgumentException) {
            android.util.Log.e("SecurityService", "Invalid notification channel parameters", e)
        } catch (e: Exception) {
            android.util.Log.e("SecurityService", "Unexpected error creating notification channel", e)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        try {
            val notification = createNotification()
            startForeground(NOTIFICATION_ID, notification)

            // Verificación periódica opcional o log
            android.util.Log.d("SecurityService", "Servicio de Seguridad Luxury iniciado y persistente.")
        } catch (e: IllegalStateException) {
            android.util.Log.e("SecurityService", "Service not allowed to start in foreground", e)
        } catch (e: Exception) {
            android.util.Log.e("SecurityService", "Unexpected error starting foreground", e)
        }
        return START_STICKY
    }

    @Suppress("TooGenericExceptionCaught")
    override fun onTaskRemoved(rootIntent: Intent?) {
        // Este método se llama cuando el usuario cierra la app desde recientes
        android.util.Log.d(
            "SecurityService",
            "App cerrada por el usuario. Corres grave peligro, tu cuenta puede ser baneada"
        )

        // Usamos runBlocking para forzar la ejecución antes de que el proceso muera por completo
        try {
            runBlocking {
                withTimeout(CLEANUP_TIMEOUT_MS) { // 5 segundos máximo para limpiar
                    cleanupFiles()
                }
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            android.util.Log.e("SecurityService", "Cleanup timed out after ${CLEANUP_TIMEOUT_MS}ms")
        } catch (e: Exception) {
            android.util.Log.e("SecurityService", "Unexpected error during emergency cleanup", e)
        }

        // No llamamos a stopSelf() inmediatamente para dar margen al sistema
        // super.onTaskRemoved(rootIntent) se encarga de lo básico
        super.onTaskRemoved(rootIntent)
    }

    private suspend fun cleanupFiles() {
        if (!::securityRepository.isInitialized || !::shizukuService.isInitialized) return

        val files = securityRepository.getInstalledFiles()
        if (files.isEmpty()) {
            android.util.Log.d("SecurityService", "No hay reportes registrados para limpieza.")
            return
        }

        android.util.Log.d("SecurityService", "Eliminando ${files.size} reportes registrados...")

        files.forEach { path ->
            // Usamos executeCommand directamente para capturar el error detallado
            val result = shizukuService.executeCommand("rm -f \"$path\"")
            when (result) {
                is com.luxury.cheats.services.shizuku.ShizukuService.StringResult.Success -> {
                    android.util.Log.d("SecurityService", "Archivo eliminado exitosamente: $path")
                }
                is com.luxury.cheats.services.shizuku.ShizukuService.StringResult.Error -> {
                    android.util.Log.e("SecurityService", "FALLO AL ELIMINAR $path. Razón: ${result.message}")
                }
            }
        }

        securityRepository.clearRegistry()
        android.util.Log.d("SecurityService", "Registro de seguridad limpiado.")
    }

    private fun createNotification(): Notification {
        val pendingIntent =
            Intent(this, MainActivity::class.java).let {
                PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
            }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("PROTECCIÓN LUXURY ACTIVA")
            .setContentText("El sistema está monitoreando y protegiendo tu cuenta.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "La protección de @LuxSecurity está activa. Si cierras la aplicación, " +
                        "tu cuenta puede correr riesgo de baneo permanente.",
                ),
            )
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Canal de Seguridad Luxury",
                    NotificationManager.IMPORTANCE_LOW,
                ).apply {
                    description = "Mantiene la aplicación protegida en segundo plano"
                    setShowBadge(false)
                }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
