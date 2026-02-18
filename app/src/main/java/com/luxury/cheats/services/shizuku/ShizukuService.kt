package com.luxury.cheats.services.shizuku

import android.content.pm.PackageManager
import android.util.Log
import rikka.shizuku.Shizuku
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio encargado de gestionar la comunicación con Shizuku.
 * Permite verificar disponibilidad, solicitar permisos y ejecutar comandos shell.
 */
@Suppress("TooGenericExceptionCaught")
@Singleton
class ShizukuService
    @Inject
    constructor() {
        private var isBinderReceived = false

        init {
            try {
                Shizuku.addBinderReceivedListener {
                    isBinderReceived = true
                }
                Shizuku.addBinderDeadListener {
                    isBinderReceived = false
                }
            } catch (e: IllegalStateException) {
                Log.w("ShizukuService", "Shizuku binder listener error: ${e.message}")
                isBinderReceived = false
            } catch (e: Exception) {
                Log.e("ShizukuService", "Unexpected error initializing Shizuku", e)
                isBinderReceived = false
            }
        }

        /**
         * Verifica si el binder de Shizuku está disponible y responde.
         * @return true si Shizuku está listo para usarse.
         */
        @Suppress("TooGenericExceptionCaught")
        fun isShizukuAvailable(): Boolean {
            if (isBinderReceived) return true
            return try {
                // Re-check ping
                if (Shizuku.pingBinder()) {
                    isBinderReceived = true
                    true
                } else {
                    false
                }
            } catch (e: IllegalStateException) {
                Log.w("ShizukuService", "Shizuku ping error: ${e.message}")
                false
            } catch (e: Exception) {
                Log.e("ShizukuService", "Error checking Shizuku availability", e)
                false
            }
        }

        /**
         * Verifica si la aplicación tiene permisos concedidos en Shizuku.
         * @return true si el permiso es GRANTED.
         */
        @Suppress("TooGenericExceptionCaught")
        fun hasPermission(): Boolean {
            if (!isShizukuAvailable()) return false
            return try {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            } catch (e: IllegalStateException) {
                Log.w("ShizukuService", "Shizuku permission check error: ${e.message}")
                false
            } catch (e: Exception) {
                Log.e("ShizukuService", "Error checking Shizuku permission", e)
                false
            }
        }

        /**
         * Solicita permisos de Shizuku al usuario.
         * @param onRequestResult Callback que recibe el resultado de la solicitud.
         */
        @Suppress("TooGenericExceptionCaught")
        fun requestPermission(onRequestResult: (Boolean) -> Unit) {
            if (!isShizukuAvailable()) {
                onRequestResult(false)
                return
            }

            if (hasPermission()) {
                onRequestResult(true)
                return
            }

            val listener =
                object : Shizuku.OnRequestPermissionResultListener {
                    override fun onRequestPermissionResult(
                        requestCode: Int,
                        grantResult: Int,
                    ) {
                        Shizuku.removeRequestPermissionResultListener(this)
                        onRequestResult(grantResult == PackageManager.PERMISSION_GRANTED)
                    }
                }

            Shizuku.addRequestPermissionResultListener(listener)
            try {
                Shizuku.requestPermission(0)
            } catch (e: IllegalStateException) {
                Log.e("ShizukuService", "Shizuku request permission error", e)
                Shizuku.removeRequestPermissionResultListener(listener)
                onRequestResult(false)
            } catch (e: Exception) {
                Log.e("ShizukuService", "Error requesting Shizuku permission", e)
                Shizuku.removeRequestPermissionResultListener(listener)
                onRequestResult(false)
            }
        }

        /**
         * Ejecuta un comando shell de forma asíncrona utilizando Shizuku.
         * @param command Comando a ejecutar.
         * @return [StringResult] con el output del comando o el mensaje de error.
         */
        @Suppress("TooGenericExceptionCaught")
        suspend fun executeCommand(command: String): StringResult =
            withContext(Dispatchers.IO) {
                if (!hasPermission()) return@withContext StringResult.Error("No Shizuku permission")

                try {
                    val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
                    val output = StringBuilder()
                    val error = StringBuilder()

                    // Leer streams en hilos separados para evitar deadlocks
                    val outThread = Thread {
                        try {
                            val reader = BufferedReader(InputStreamReader(process.inputStream))
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                output.append(line).append("\n")
                            }
                        } catch (e: Exception) {
                            Log.e("ShizukuService", "Error reading stdout", e)
                        }
                    }

                    val errThread = Thread {
                        try {
                            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
                            var line: String?
                            while (errorReader.readLine().also { line = it } != null) {
                                error.append(line).append("\n")
                            }
                        } catch (e: Exception) {
                            Log.e("ShizukuService", "Error reading stderr", e)
                        }
                    }

                    outThread.start()
                    errThread.start()

                    // Esperar a que el proceso termine con un timeout razonable (por ejemplo, 2 minutos para instalaciones grandes)
                    // Como Process.waitFor(long, TimeUnit) es API 26, usamos un bucle simple para compatibilidad si es necesario,
                    // pero aquí confiaremos en waitFor() normal por ahora, asegurando que los hilos de lectura terminen.
                    
                    val exitCode = process.waitFor()
                    outThread.join(5000) // Esperar un poco más a que los hilos terminen de leer
                    errThread.join(5000)

                    if (exitCode == 0) {
                        StringResult.Success(output.toString().trim())
                    } else {
                        val errorMsg = error.toString().trim().ifEmpty { 
                            output.toString().trim().ifEmpty { "Unknown error (Exit code $exitCode)" }
                        }
                        StringResult.Error(errorMsg)
                    }
                } catch (e: java.io.IOException) {
                    Log.e("ShizukuService", "IO error executing command: $command", e)
                    StringResult.Error("IO error: ${e.message}")
                } catch (e: Exception) {
                    Log.e("ShizukuService", "Unexpected error executing command: $command", e)
                    StringResult.Error(e.message ?: "Exception executing command")
                }
            }

        /** Resultado de la ejecución de un comando. */
        sealed class StringResult {
            /** Resultado exitoso con el output capturado. */
            data class Success(val output: String) : StringResult()

            /** Resultado fallido con el mensaje de error. */
            data class Error(val message: String) : StringResult()
        }
    }
