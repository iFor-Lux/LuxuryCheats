package com.luxury.cheats.features.update.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.luxury.cheats.features.update.logic.AppUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Servicio para gestionar la lógica de actualizaciones desde Firebase.
 */
class UpdateService {

    private val db = FirebaseDatabase.getInstance().getReference("app_update")

    /**
     * Obtiene la información de actualización desde Firebase.
     * @return [AppUpdate] con los datos del servidor.
     */
    suspend fun getAppUpdate(): AppUpdate = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { cont ->
            db.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val update = try {
                        AppUpdate(
                            active = snapshot.child("active").getValue(Boolean::class.java) ?: false,
                            title = snapshot.child("title").getValue(String::class.java) ?: "",
                            // Intentamos obtener tanto 'description' como 'descripcion' por compatibilidad
                            description = snapshot.child("description").getValue(String::class.java) 
                                ?: snapshot.child("descripcion").getValue(String::class.java) ?: "",
                            version = snapshot.child("version").getValue(String::class.java) ?: "",
                            // Intentamos obtener tanto 'downloadLink' como 'downloadlink' por compatibilidad
                            downloadLink = snapshot.child("downloadLink").getValue(String::class.java)
                                ?: snapshot.child("downloadlink").getValue(String::class.java) ?: "",
                            timestamp = snapshot.child("timestamp").getValue(String::class.java) ?: ""
                        )
                    } catch (e: com.google.firebase.database.DatabaseException) {
                        AppUpdate()
                    }
                    cont.resume(update)
                }

                override fun onCancelled(error: DatabaseError) {
                    cont.resumeWithException(error.toException())
                }
            })
        }
    }
}
