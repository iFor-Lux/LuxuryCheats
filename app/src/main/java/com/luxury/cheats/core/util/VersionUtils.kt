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

        return calculateIsNewer(cleanRemote, cleanLocal)
    }

    private fun calculateIsNewer(remote: String, local: String): Boolean {
        return try {
            val remoteParts = remote.split(".").mapNotNull { it.toIntOrNull() }
            val localParts = local.split(".").mapNotNull { it.toIntOrNull() }
            val maxLength = maxOf(remoteParts.size, localParts.size)

            var i = 0
            var comparisonResult = 0
            while (i < maxLength && comparisonResult == 0) {
                val remoteVal = remoteParts.getOrElse(i) { 0 }
                val localVal = localParts.getOrElse(i) { 0 }
                
                if (remoteVal > localVal) {
                    comparisonResult = 1
                } else if (remoteVal < localVal) {
                    comparisonResult = -1
                }
                i++
            }
            comparisonResult > 0
        } catch (ignored: Exception) {
            remote != local
        }
    }
}
