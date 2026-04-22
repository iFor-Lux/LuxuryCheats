package com.luxury.cheats.services.firebase

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Servicio de Autenticación - Versión simplificada usando SDK de Firebase.
 */
class AuthService {
    private val db by lazy { FirebaseDatabase.getInstance().getReference("users") }

    /** Constantes para la conexión de red. */
    companion object {
        private const val CONNECT_TIMEOUT_MS = 8000
        private const val READ_TIMEOUT_MS = 8000
    }

    /** Resultado de la operación de login. */
    sealed class LoginResult {
        /** Login exitoso. */
        data class Success(val userData: JSONObject? = null) : LoginResult()


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
            val t0 = System.currentTimeMillis()

            // 1. FORZAR consulta al servidor mediante REST (ignora la caché del SDK)
            // La propia petición REST fallará con una excepción descriptiva si no hay internet.
            val fetchResult = fetchUserDataRest(u)

            val t1 = System.currentTimeMillis()
            Log.d("PERF", "Query REST = ${t1 - t0} ms")

            when (fetchResult) {
                is FetchResult.Success -> {
                    val userDataJson = fetchResult.data
                    val userKey = userDataJson.optString("_key")
                    validateUserData(p, userDataJson, userKey)
                }
                is FetchResult.NoInternet -> {
                    LoginResult.Error("Sin conexión a internet. El login requiere estar online.")
                }
                is FetchResult.NotFound -> {
                    LoginResult.Error("Usuario no encontrado")
                }
                is FetchResult.Error -> {
                    LoginResult.Error("Error: ${fetchResult.message}")
                }
            }
        } catch (e: java.io.IOException) {
            Log.e("AuthService", "Login failed due to IO exception", e)
            LoginResult.Error("Fallo de red: ${e.localizedMessage}")
        } catch (e: org.json.JSONException) {
            Log.e("AuthService", "Login failed due to JSON parsing", e)
            LoginResult.Error("Error de datos: ${e.localizedMessage}")
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            Log.e("AuthService", "Unexpected login error", e)
            LoginResult.Error("Error inesperado: ${e.localizedMessage}")
        }
    }

    /** Resultado interno de la búsqueda de usuario. */
    private sealed class FetchResult {
        data class Success(val data: JSONObject) : FetchResult()
        object NoInternet : FetchResult()
        object NotFound : FetchResult()
        data class Error(val message: String) : FetchResult()
    }

    /**
     * Consulta directamente a la base de datos vía REST para saltar la persistencia local del SDK.
     */
    private suspend fun fetchUserDataRest(u: String): FetchResult =
        withContext(Dispatchers.IO) {
            try {
                // El nodo /users está indexado por "username", por lo que la consulta REST es eficiente
                val baseUrl = "https://luxury-counter-default-rtdb.firebaseio.com/users.json"
                val queryUrl = "$baseUrl?orderBy=\"username\"&equalTo=\"$u\"&limitToFirst=1"

                val url = URL(queryUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = CONNECT_TIMEOUT_MS
                conn.readTimeout = READ_TIMEOUT_MS

                val responseCode = conn.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext FetchResult.Error("HTTP $responseCode")
                }

                val response = conn.inputStream.bufferedReader().use { it.readText() }
                if (response == "null" || response == "{}" || response.isBlank()) {
                    return@withContext FetchResult.NotFound
                }

                val json = JSONObject(response)
                val keys = json.keys()
                if (!keys.hasNext()) return@withContext FetchResult.NotFound

                val key = keys.next()
                val userData = json.getJSONObject(key)
                userData.put("_key", key) // Preservar la clave del usuario
                FetchResult.Success(userData)
            } catch (e: java.net.UnknownHostException) {
                Log.e("AuthService", "No Internet: ${e.message}")
                FetchResult.NoInternet
            } catch (e: java.net.SocketTimeoutException) {
                Log.e("AuthService", "Timeout: ${e.message}")
                FetchResult.NoInternet
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                Log.e("AuthService", "REST Query Failed: ${e.message}")
                FetchResult.Error(e.message ?: "Unknown error")
            }
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

        return LoginResult.Success(userData)
    }

    /**

     * Valida una clave de licencia en Firebase.
     *
     * @param key Clave de la licencia (ej. LUXURY-XXXX).
     * @return [LoginResult] indicando el éxito o fallo de la validación.
     */
    suspend fun validateLicense(key: String): LoginResult =
        withContext(Dispatchers.IO) {
            try {
                // Consultamos directamente el nodo de la licencia vía REST
                val baseUrl = "https://luxury-counter-default-rtdb.firebaseio.com/licenses/$key.json"
                val url = URL(baseUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = CONNECT_TIMEOUT_MS
                conn.readTimeout = READ_TIMEOUT_MS

                val responseCode = conn.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext LoginResult.Error("Error de servidor: $responseCode")
                }

                val response = conn.inputStream.bufferedReader().use { it.readText() }
                if (response == "null" || response == "{}") {
                    return@withContext LoginResult.Error("Licencia no válida")
                }

                val licenseData = JSONObject(response)
                
                // 1. Verificar estado
                if (licenseData.optString("status") != "active") {
                    return@withContext LoginResult.Error("Licencia inactiva o cancelada")
                }

                // 2. Verificar dispositivo (Vincular si está vacío)
                val manufacturer = android.os.Build.MANUFACTURER
                val model = android.os.Build.MODEL
                val deviceName =
                    if (model.startsWith(manufacturer, ignoreCase = true)) {
                        model.replaceFirstChar { it.uppercase() }
                    } else {
                        "${manufacturer.replaceFirstChar { it.uppercase() }} $model"
                    }

                val savedDevice = licenseData.optString("device")
                if (savedDevice.isNotEmpty() && savedDevice != deviceName) {
                    return@withContext LoginResult.Error("Llave vinculada en: $savedDevice")
                }

                // 3. Verificar expiración
                val exp = licenseData.optString("expirationDate")
                if (exp.isNotEmpty()) {
                    try {
                        val expDate = ZonedDateTime.parse(exp, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                        if (ZonedDateTime.now().isAfter(expDate)) {
                            return@withContext LoginResult.Error("Licencia expirada")
                        }
                    } catch (ignored: Exception) {}
                }

                // 4. Actualizar estado en Firebase (vincular dispositivo y marcar como usada)
                val updateDb = FirebaseDatabase.getInstance().getReference("licenses").child(key)
                if (savedDevice.isEmpty()) {
                    updateDb.child("device").setValue(deviceName)
                }
                updateDb.child("used").setValue(true)
                
                // Agregamos la clave original para que PerfilViewModel sepa qué licencia es
                licenseData.put("_key", key)

                LoginResult.Success(licenseData)
            } catch (e: java.net.UnknownHostException) {

                LoginResult.Error("Sin conexión a internet")
            } catch (e: Exception) {
                Log.e("AuthService", "License validation failed", e)
                LoginResult.Error("Error: ${e.localizedMessage}")
            }
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

    /**
     * Obtiene los datos de una licencia específica.
     */
    fun getLicenseData(
        key: String,
        onComplete: (DataSnapshot) -> Unit,
    ) {
        FirebaseDatabase.getInstance().getReference("licenses").child(key)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        onComplete(snapshot)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w("AuthService", "getLicenseData:onCancelled", error.toException())
                    }
                }
            )
    }
}

