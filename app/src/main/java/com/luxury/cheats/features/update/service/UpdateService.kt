package com.luxury.cheats.features.update.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luxury.cheats.features.update.logic.AppUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Servicio para gestionar la lógica de actualizaciones desde Firebase.
 */
class UpdateService
    @Inject
    constructor() {
        private val db = FirebaseDatabase.getInstance().getReference("app_update")

        companion object {
            private var lastFetchedUpdate: AppUpdate? = null
            private var lastFetchTime: Long = 0
            private const val CACHE_DURATION_MS = 30 * 1000L // 30 segundos para facilitar pruebas
        }

        /**
         * Obtiene la información de actualización desde Firebase.
         * Usa un caché de sesión para evitar consultas redundantes.
         * @return [AppUpdate] con los datos del servidor.
         */
        suspend fun getAppUpdate(): AppUpdate =
            withContext(Dispatchers.IO) {
                // Si ya tenemos un dato reciente (menos de 5 min), lo devolvemos sin consultar Firebase
                val now = System.currentTimeMillis()
                lastFetchedUpdate?.let {
                    if (now - lastFetchTime < CACHE_DURATION_MS) {
                        return@withContext it
                    }
                }

                suspendCancellableCoroutine { cont ->
                    db.addListenerForSingleValueEvent(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val update =
                                    try {
                                        AppUpdate(
                                            active = snapshot.child("active").getValue(Boolean::class.java) ?: false,
                                            title = snapshot.child("title").getValue(String::class.java) ?: "",
                                            description =
                                                snapshot.child("description").getValue(String::class.java)
                                                    ?: snapshot.child("descripcion").getValue(String::class.java)
                                                    ?: "",
                                            version = snapshot.child("version").getValue(String::class.java) ?: "",
                                            downloadLink =
                                                snapshot.child("downloadLink").getValue(String::class.java)
                                                    ?: snapshot.child("downloadlink").getValue(String::class.java)
                                                    ?: "",
                                            timestamp = snapshot.child("timestamp").getValue(String::class.java) ?: "",
                                        )
                                    } catch (e: com.google.firebase.database.DatabaseException) {
                                        android.util.Log.e("UpdateService", "Error parsing AppUpdate snapshot", e)
                                        AppUpdate()
                                    }
                                
                                // Actualizamos el caché de sesión
                                lastFetchedUpdate = update
                                lastFetchTime = System.currentTimeMillis()
                                
                                cont.resume(update)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                cont.resumeWithException(error.toException())
                            }
                        },
                    )
                }
            }
    }
