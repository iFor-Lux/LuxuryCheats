package com.luxury.cheats.features.download.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Modelo de datos para la información de descarga de un cheat.
 */
data class DownloadItem(
    val title: String = "",
    val path: String = "",
    val url: String = "",
)

/**
 * Servicio para gestionar la obtención de enlaces de descarga desde Firebase.
 */
class DownloadService
    @Inject
    constructor() {
        /** Constantes de configuración de red y conversión de unidades. */
        companion object {
            private const val TIMEOUT_MS = 5000
            private const val BYTES_PER_KB = 1024.0
            private const val MB_THRESHOLD = 1.0
            private const val BUFFER_SIZE = 4096
        }

        private val db = FirebaseDatabase.getInstance().getReference("urls")

        /**
         * Obtiene la información de descarga para un cheat específico.
         * @param cheatName Nombre del cheat (Aimbot, WallHack, etc.)
         * @return [DownloadItem] con los datos del servidor.
         */
        suspend fun getDownloadInfo(cheatName: String): DownloadItem =
            withContext(Dispatchers.IO) {
                suspendCancellableCoroutine { cont ->
                    db.child(cheatName).addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val item =
                                    try {
                                        DownloadItem(
                                            title = cheatName,
                                            path = snapshot.child("path").getValue(String::class.java) ?: "",
                                            url = snapshot.child("url").child("url").getValue(String::class.java) ?: "",
                                        )
                                    } catch (e: com.google.firebase.database.DatabaseException) {
                                        android.util.Log.e("DownloadService", "Error mapping DownloadItem", e)
                                        DownloadItem(title = cheatName)
                                    }
                                cont.resume(item)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                cont.resumeWithException(error.toException())
                            }
                        },
                    )
                }
            }

        /**
         * Obtiene el tamaño del archivo desde una URL.
         * @param urlString URL del archivo.
         * @return Tamaño formateado (ej: "24.5MB" o "97.22kb").
         */
        suspend fun getFileSize(urlString: String): String =
            withContext(Dispatchers.IO) {
                try {
                    val url = URL(urlString)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "HEAD"
                    connection.connectTimeout = TIMEOUT_MS
                    connection.readTimeout = TIMEOUT_MS
                    connection.instanceFollowRedirects = true
                    connection.connect()

                    val fileSize = connection.contentLengthLong
                    connection.disconnect()

                    if (fileSize <= 0) return@withContext "Desconocido"

                    formatFileSize(fileSize)
                } catch (e: java.net.MalformedURLException) {
                    android.util.Log.e("DownloadService", "Invalid URL: $urlString", e)
                    "URL inválida"
                } catch (e: java.io.IOException) {
                    android.util.Log.e("DownloadService", "Network error getting file size", e)
                    "Error de red"
                }
            }

        /**
         * Descarga un archivo desde una URL a un archivo destino reportando el progreso.
         * @param url La URL de descarga.
         * @param destFile El archivo local donde se guardará.
         * @return Flow que emite el progreso de 0.0 a 1.0.
         */
        fun downloadFile(
            url: String,
            destFile: java.io.File,
        ): Flow<Float> =
            flow {
                var connection: HttpURLConnection? = null
                try {
                    val urlObj = URL(url)
                    connection =
                        withContext(Dispatchers.IO) {
                            urlObj.openConnection() as HttpURLConnection
                        }
                    connection.connect()

                    if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                        throw java.io.IOException(
                            "Server returned HTTP ${connection.responseCode} ${connection.responseMessage}",
                        )
                    }

                    val fileLength = connection.contentLength
                    val input = connection.inputStream
                    val output = java.io.FileOutputStream(destFile)

                    val data = ByteArray(BUFFER_SIZE)
                    var total: Long = 0
                    var count: Int

                    // Si no sabemos el tamaño, emitiremos progreso indeterminado o simulado parcial,
                    // pero por ahora asumimos que funciona mejor con Content-Length.

                    while (withContext(Dispatchers.IO) { input.read(data) }.also { count = it } != -1) {
                        withContext(Dispatchers.IO) { output.write(data, 0, count) }
                        total += count
                        if (fileLength > 0) {
                            emit(total.toFloat() / fileLength)
                        }
                    }
                    emit(1f) // Asegurar 100% al finalizar

                    withContext(Dispatchers.IO) {
                        output.flush()
                        output.close()
                        input.close()
                    }
                } catch (e: java.io.IOException) {
                    android.util.Log.e("DownloadService", "Error downloading file", e)
                    throw e
                } finally {
                    connection?.disconnect()
                }
            }.flowOn(Dispatchers.IO)

        private fun formatFileSize(size: Long): String {
            val kb = size / BYTES_PER_KB
            val mb = kb / BYTES_PER_KB
            return when {
                mb >= MB_THRESHOLD -> String.format(java.util.Locale.US, "%.1fMB", mb)
                else -> String.format(java.util.Locale.US, "%.2fkb", kb)
            }
        }
    }
