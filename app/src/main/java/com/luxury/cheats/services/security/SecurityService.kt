package com.luxury.cheats.services.security

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.luxury.cheats.MainActivity
import com.luxury.cheats.services.shizuku.ShizukuFileUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Servicio de primer plano que mantiene la app activa y limpia archivos al cerrar.
 */
@AndroidEntryPoint
class SecurityService : Service() {

    @Inject lateinit var securityRepository: SecurityRepository
    @Inject lateinit var shizukuFileUtil: ShizukuFileUtil

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val CHANNEL_ID = "security_channel"
    private val NOTIF_ID = 1337

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIF_ID, notification)
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Este método se llama cuando el usuario cierra la app desde recientes
        runBlocking {
            cleanupFiles()
        }
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    private suspend fun cleanupFiles() {
        val files = securityRepository.getInstalledFiles()
        files.forEach { path ->
            shizukuFileUtil.deleteFile(path)
        }
        securityRepository.clearRegistry()
    }

    private fun createNotification(): Notification {
        val pendingIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SEGURIDAD ACTIVADA")
            .setContentText("Protección de archivos activa en segundo plano")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Seguridad Luxury",
                NotificationManager.IMPORTANCE_LOW
            )
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
