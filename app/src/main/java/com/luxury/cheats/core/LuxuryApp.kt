package com.luxury.cheats.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

/**
 * Clase de aplicación personalizada para configuraciones globales.
 */
@HiltAndroidApp
class LuxuryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        try {
            // Inicializar Firebase
            FirebaseApp.initializeApp(this)
            
            // Obtener instancia global (usa la config de google-services.json)
            val db = FirebaseDatabase.getInstance()
            
            // CRÍTICO: La persistencia DEBE configurarse antes que cualquier otra operación
            db.setPersistenceEnabled(true)
            
            // Ahora sí podemos pre-calentar la conexión
            db.goOnline()
        } catch (e: com.google.firebase.FirebaseException) {
            // Si algo falla aquí, que la app al menos abra
            android.util.Log.e("LuxuryApp", "Error initializing Firebase", e)
        }
    }
}
