package com.luxury.cheats.services.firebase

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio central para la inicialización y acceso a componentes de Firebase.
 */
@Singleton
class FirebaseService
    @Inject
    constructor() {
        private val db = FirebaseDatabase.getInstance()
        private val urlCache = mutableMapOf<String, String>()

        /**
         * Busca la URL de una imagen en Firebase filtrando por título.
         * Intenta en 'app_imagen' y 'app_images' para mayor compatibilidad.
         *
         * @param title El título de la imagen a buscar (ej: "Shizuku").
         * @return La URL de la imagen si se encuentra, null en caso contrario.
         */
        suspend fun fetchImageUrl(title: String): String? {
            // 0. Retornar del caché si ya existe
            urlCache[title]?.let {
                android.util.Log.d("FirebaseService", "Cache HIT for '$title': $it")
                return it
            }

            val nodes = listOf("app_imagen", "app_images")

            for (node in nodes) {
                try {
                    android.util.Log.d(
                        "FirebaseService",
                        "Strategy 1: Searching for '$title' in node '$node' by property 'title'...",
                    )
                    val snapshot =
                        db.getReference(node)
                            .orderByChild("title")
                            .equalTo(title)
                            .limitToFirst(1)
                            .get()
                            .await()

                    if (snapshot.exists() && snapshot.childrenCount > 0) {
                        val child = snapshot.children.first()
                        val url = extractUrl(child)
                        if (url != null) {
                            android.util.Log.d("FirebaseService", "Strategy 1 Success in '$node': $url")
                            urlCache[title] = url // Guardar en caché
                            return url
                        }
                    }

                    // Estrategia 2: Acceso directo por clave (en caso de que el nodo sea un Mapa donde la clave es el título)
                    android.util.Log.d("FirebaseService", "Strategy 2: Accessing direct child '$node/$title'...")
                    val directSnapshot = db.getReference(node).child(title).get().await()
                    if (directSnapshot.exists()) {
                        val url =
                            extractUrl(directSnapshot)
                                ?: directSnapshot.getValue(String::class.java) // En caso de que el valor sea directamente la URL

                        if (url != null) {
                            android.util.Log.d("FirebaseService", "Strategy 2 Success in '$node/$title': $url")
                            urlCache[title] = url // Guardar en caché
                            return url
                        }
                    }
                } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                    android.util.Log.e("FirebaseService", "Error in '$node' for '$title'", e)
                }
            }

            android.util.Log.w("FirebaseService", "No image URL found for '$title' in any known node or strategy.")
            return null
        }

        private fun extractUrl(snapshot: com.google.firebase.database.DataSnapshot): String? {
            return snapshot.child("url").getValue(String::class.java)
                ?: snapshot.child("imageUrl").getValue(String::class.java)
                ?: snapshot.child("image_url").getValue(String::class.java)
        }

        /**
         * Registra el token de FCM del dispositivo en el nodo 'device_tokens'.
         *
         * @param token El token de FCM del dispositivo.
         */
        suspend fun registerDeviceToken(token: String) {
            if (token.isBlank()) return

            try {
                val tokenRef = db.getReference("device_tokens").child(token)
                val timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
                
                val data = mapOf(
                    "active" to true,
                    "plataform" to "android",
                    "timestamp" to timestamp
                )
                
                tokenRef.setValue(data).await()
                android.util.Log.d("FirebaseService", "Device token registered successfully: $token")
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                android.util.Log.e("FirebaseService", "Error registering device token", e)
            }
        }
    }
