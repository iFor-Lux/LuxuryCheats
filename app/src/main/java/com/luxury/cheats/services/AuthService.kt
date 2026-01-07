package com.luxury.cheats.services

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import org.json.JSONObject

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.util.Log

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
    suspend fun loginWithFirebase(u: String, p: String): LoginResult = withContext(Dispatchers.IO) {
        loginWithSdk(u, p)
    }

    private suspend fun loginWithSdk(u: String, p: String): LoginResult {
        return try {
            val t0 = System.currentTimeMillis()

            val snapshot = suspendCancellableCoroutine { cont ->
                db.orderByChild("username").equalTo(u).limitToFirst(1)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            cont.resume(snapshot)
                        }
                        override fun onCancelled(error: DatabaseError) {
                            cont.resumeWithException(error.toException())
                        }
                    })
            }

            val t1 = System.currentTimeMillis()
            Log.d("PERF", "Query = ${t1 - t0} ms")

            if (!snapshot.exists()) return LoginResult.Error("Usuario no encontrado")

            val user = snapshot.children.first()
            val userKey = user.key ?: ""

            val userData = JSONObject()
            user.children.forEach { child ->
                userData.put(child.key ?: "", child.value)
            }

            validateUserData(p, userData, userKey)
        } catch (e: com.google.firebase.database.DatabaseException) {
            LoginResult.Error("Error de base de datos: ${e.message}")
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            LoginResult.Error("Error: ${e.localizedMessage}")
        }
    }

    private fun validateUserData(p: String, userData: JSONObject, userKey: String): LoginResult {
        if (userData.optString("password") != p) return LoginResult.Error("Contraseña incorrecta")

        val manufacturer = android.os.Build.MANUFACTURER
        val model = android.os.Build.MODEL
        val deviceName = if (model.startsWith(manufacturer, ignoreCase = true)) {
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
            } catch (ignored: Exception) {}
        }

        if (savedDevice.isEmpty()) {
            db.child(userKey).child("device").setValue(deviceName)
        }

        return LoginResult.Success
    }

    /**
     * Método legacy con callback (usar fetchUserDataRest para mayor velocidad)
     */
    fun getUserData(username: String, onComplete: (DataSnapshot) -> Unit) {
        db.orderByChild("username").equalTo(username).limitToFirst(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) onComplete(snapshot.children.first()) else onComplete(snapshot)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w("AuthService", "getUserData:onCancelled", error.toException())
                }
            })
    }
}
