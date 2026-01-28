package com.luxury.cheats.features.download.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Modelo de datos para la información de descarga de un cheat.
 */
data class DownloadItem(
    val title: String = "",
    val path: String = "",
    val url: String = ""
)

/**
 * Servicio para gestionar la obtención de enlaces de descarga desde Firebase.
 */
class DownloadService {

    private val db = FirebaseDatabase.getInstance().getReference("urls")

    /**
     * Obtiene la información de descarga para un cheat específico.
     * @param cheatName Nombre del cheat (Aimbot, WallHack, etc.)
     * @return [DownloadItem] con los datos del servidor.
     */
    suspend fun getDownloadInfo(cheatName: String): DownloadItem = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { cont ->
            db.child(cheatName).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val item = try {
                        DownloadItem(
                            title = cheatName,
                            path = snapshot.child("path").getValue(String::class.java) ?: "",
                            url = snapshot.child("url").child("url").getValue(String::class.java) ?: ""
                        )
                    } catch (e: com.google.firebase.database.DatabaseException) {
                        DownloadItem(title = cheatName)
                        // Consider resuming with exception if you want to show an error to the user
                    }
                    cont.resume(item)
                }

                override fun onCancelled(error: DatabaseError) {
                    cont.resumeWithException(error.toException())
                }
            })
        }
    }

    /**
     * Obtiene el tamaño del archivo desde una URL.
     * @param urlString URL del archivo.
     * @return Tamaño formateado (ej: "24.5MB" o "97.22kb").
     */
    suspend fun getFileSize(urlString: String): String = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.instanceFollowRedirects = true
            connection.connect()
            
            val fileSize = connection.contentLengthLong
            connection.disconnect()

            if (fileSize <= 0) return@withContext "Desconocido"

            formatFileSize(fileSize)
        } catch (e: java.net.MalformedURLException) {
            "URL inválida"
        } catch (e: java.io.IOException) {
            "Error de red"
        }
    }

    private fun formatFileSize(size: Long): String {
        val kb = size / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1.0 -> String.format(java.util.Locale.US, "%.1fMB", mb)
            else -> String.format(java.util.Locale.US, "%.2fkb", kb)
        }
    }
}
