package com.luxury.cheats.core.util

/**
 * Utilidades para el manejo de versiones de la aplicación.
 */
object VersionUtils {

    /**
     * Compara dos versiones en formato semántico (ej. "1.2.3").
     * @param remote Versión obtenida de Firebase o fuente remota.
     * @param local Versión actual instalada en el dispositivo.
     * @return true si la versión remota es estrictamente mayor que la local.
     */
    fun isVersionNewer(remote: String, local: String): Boolean {
        // Limpiamos espacios y posibles prefijos 'v'
        val cleanRemote = remote.trim().lowercase().removePrefix("v")
        val cleanLocal = local.trim().lowercase().removePrefix("v")

        android.util.Log.d("VersionUtils", "Comparing -> Remote: '$cleanRemote', Local: '$cleanLocal'")
        if (cleanRemote.isEmpty() || cleanLocal.isEmpty()) return false
        if (cleanRemote == cleanLocal) return false

        return try {
            val remoteParts = cleanRemote.split(".").mapNotNull { it.toIntOrNull() }
            val localParts = cleanLocal.split(".").mapNotNull { it.toIntOrNull() }

            val maxLength = maxOf(remoteParts.size, localParts.size)

            for (i in 0 until maxLength) {
                val remoteVal = remoteParts.getOrElse(i) { 0 }
                val localVal = localParts.getOrElse(i) { 0 }

                if (remoteVal > localVal) return true
                if (remoteVal < localVal) return false
            }
            false
        } catch (ignored: Exception) {
            // Fallback simple si el formato no es numérico
            cleanRemote != cleanLocal
        }
    }
}
