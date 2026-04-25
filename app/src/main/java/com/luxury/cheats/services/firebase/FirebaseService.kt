package com.luxury.cheats.services.firebase

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Servicio central para la inicialización y acceso a componentes de Firebase.
 */
class FirebaseService @Inject constructor() {
    private val db = FirebaseDatabase.getInstance()

    /**
     * Busca la URL de una imagen en el nodo 'app_images' filtrando por título.
     *
     * @param title El título de la imagen a buscar (ej: "imagenLogin").
     * @return La URL de la imagen si se encuentra, null en caso contrario.
     */
    suspend fun fetchImageUrl(title: String): String? = try {
        val snapshot = db.getReference("app_images")
            .orderByChild("title")
            .equalTo(title)
            .limitToFirst(1)
            .get()
            .await()

        val url = snapshot.children.firstOrNull()?.child("url")?.getValue(String::class.java)
        android.util.Log.d("FirebaseService", "fetchImageUrl result for '$title': $url")
        url
    } catch (e: Exception) {
        android.util.Log.e("FirebaseService", "Error fetching image url for '$title'", e)
        null
    }
}
