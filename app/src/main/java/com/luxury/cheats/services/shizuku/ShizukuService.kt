package com.luxury.cheats.services.shizuku

import android.content.pm.PackageManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShizukuService @Inject constructor() {

    private var isBinderReceived = false

    init {
        Shizuku.addBinderReceivedListener {
            isBinderReceived = true
        }
        Shizuku.addBinderDeadListener {
            isBinderReceived = false
        }
    }

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
        } catch (e: Exception) {
            false
        }
    }

    fun hasPermission(): Boolean {
        if (!isShizukuAvailable()) return false
        return try {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }

    fun requestPermission(onRequestResult: (Boolean) -> Unit) {
        if (!isShizukuAvailable()) {
            onRequestResult(false)
            return
        }

        if (hasPermission()) {
            onRequestResult(true)
            return
        }

        val listener = object : Shizuku.OnRequestPermissionResultListener {
            override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                Shizuku.removeRequestPermissionResultListener(this)
                onRequestResult(grantResult == PackageManager.PERMISSION_GRANTED)
            }
        }

        Shizuku.addRequestPermissionResultListener(listener)
        try {
            Shizuku.requestPermission(0)
        } catch (e: Exception) {
            Shizuku.removeRequestPermissionResultListener(listener)
            onRequestResult(false)
        }
    }

    suspend fun executeCommand(command: String): StringResult = withContext(Dispatchers.IO) {
        if (!hasPermission()) return@withContext StringResult.Error("No Shizuku permission")

        try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            val output = StringBuilder()
            val error = StringBuilder()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val errorReader = BufferedReader(InputStreamReader(process.errorStream))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            while (errorReader.readLine().also { line = it } != null) {
                error.append(line).append("\n")
            }

            process.waitFor()
            
            if (process.exitValue() == 0) {
                StringResult.Success(output.toString().trim())
            } else {
                StringResult.Error(error.toString().trim().ifEmpty { "Unknown error (Exit code ${process.exitValue()})" })
            }

        } catch (e: Exception) {
            StringResult.Error(e.message ?: "Exception executing command")
        }
    }

    sealed class StringResult {
        data class Success(val output: String) : StringResult()
        data class Error(val message: String) : StringResult()
    }
}
