package com.luxury.cheats.services.firebase

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Servicio de Autenticación - Versión simplificada usando SDK de Firebase.
 */
class AuthService {
    private val db by lazy { FirebaseDatabase.getInstance().getReference("users") }

    /** Resultado de la operación de login. */
    sealed class LoginResult {
        /** Login exitoso. */
        object Success : LoginResult()

        /**
         * Error en el proceso de login.
         * @property message Descripción del error.
         */
        data class Error(val message: String) : LoginResult()
    }

    /**
     * Inicia el proceso de autenticación con Firebase.
     *
     * @param u Nombre de usuario.
     * @param p Contraseña.
     * @return [LoginResult] indicando el éxito o fallo de la operación.
     */
    suspend fun loginWithFirebase(
        u: String,
        p: String,
    ): LoginResult =
        withContext(Dispatchers.IO) {
            loginWithSdk(u, p)
        }

    private suspend fun loginWithSdk(
        u: String,
        p: String,
    ): LoginResult {
        return try {
            // 1. Verificar si hay conexión con el servidor
            if (!isOnline()) {
                return LoginResult.Error("Sin conexión a internet. El login requiere estar online.")
            }

            val t0 = System.currentTimeMillis()

            // 2. FORZAR consulta al servidor mediante REST (ignora la caché del SDK)
            // Esto resuelve el problema de usuarios eliminados que aún se "ven" en la caché local
            val userDataJson = fetchUserDataRest(u)

            val t1 = System.currentTimeMillis()
            Log.d("PERF", "Query REST = ${t1 - t0} ms")

            if (userDataJson == null) {
                return LoginResult.Error("Usuario no encontrado")
            }

            val userKey = userDataJson.optString("_key")
            validateUserData(p, userDataJson, userKey)
        } catch (e: Exception) {
            LoginResult.Error("Error: ${e.localizedMessage}")
        }
    }

    /**
     * Consulta directamente a la base de datos vía REST para saltar la persistencia local del SDK.
     */
    private suspend fun fetchUserDataRest(u: String): JSONObject? =
        withContext(Dispatchers.IO) {
            try {
                // El nodo /users está indexado por "username", por lo que la consulta REST es eficiente
                val baseUrl = "https://luxury-counter-default-rtdb.firebaseio.com/users.json"
                val queryUrl = "$baseUrl?orderBy=\"username\"&equalTo=\"$u\"&limitToFirst=1"

                val url = URL(queryUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 8000
                conn.readTimeout = 8000

                if (conn.responseCode != HttpURLConnection.HTTP_OK) return@withContext null

                val response =
                    conn.inputStream.bufferedReader().use { it.readText() }
                if (response == "null" || response == "{}" || response.isBlank()) return@withContext null

                val json = JSONObject(response)
                val keys = json.keys()
                if (!keys.hasNext()) return@withContext null

                val key = keys.next()
                val userData = json.getJSONObject(key)
                userData.put("_key", key) // Preservar la clave del usuario
                userData
            } catch (e: Exception) {
                Log.e("AuthService", "REST Query Failed: ${e.message}")
                null
            }
        }

    private suspend fun isOnline(): Boolean =
        suspendCancellableCoroutine { cont ->
            FirebaseDatabase.getInstance().getReference(".info/connected")
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val connected = snapshot.getValue(Boolean::class.java) ?: false
                            cont.resume(connected)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            cont.resume(false)
                        }
                    },
                )
        }

    private fun validateUserData(
        p: String,
        userData: JSONObject,
        userKey: String,
    ): LoginResult {
        if (userData.optString("password") != p) return LoginResult.Error("Contraseña incorrecta")

        val manufacturer = android.os.Build.MANUFACTURER
        val model = android.os.Build.MODEL
        val deviceName =
            if (model.startsWith(manufacturer, ignoreCase = true)) {
                model.replaceFirstChar { it.uppercase() }
            } else {
                "${manufacturer.replaceFirstChar { it.uppercase() }} $model"
            }

        val savedDevice = userData.optString("device")
        if (savedDevice.isNotEmpty() && savedDevice != deviceName) {
            return LoginResult.Error("Vincular en: $savedDevice")
        }

        val exp = userData.optString("expirationDate")
        if (exp.isNotEmpty()) {
            try {
                val expDate = ZonedDateTime.parse(exp, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                if (ZonedDateTime.now().isAfter(expDate)) return LoginResult.Error("Membresía expirada")
            } catch (ignored: Exception) {
            }
        }

        if (savedDevice.isEmpty()) {
            db.child(userKey).child("device").setValue(deviceName)
        }

        return LoginResult.Success
    }

    /**
     * Método legacy con callback (usar fetchUserDataRest para mayor velocidad)
     */
    fun getUserData(
        username: String,
        onComplete: (DataSnapshot) -> Unit,
    ) {
        db.orderByChild("username").equalTo(username).limitToFirst(1)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) onComplete(snapshot.children.first()) else onComplete(snapshot)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("AuthService", "getUserData:onCancelled", error.toException())
                    }
                },
            )
    }
}
