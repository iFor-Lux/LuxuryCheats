package com.luxury.cheats.services

import com.luxury.cheats.features.home.logic.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Response

/**
 * Repositorio para gestionar la búsqueda y obtención de datos de jugadores.
 * Centraliza la lógica de búsqueda multirregión.
 */
class PlayerRepository(
    private val ffApiService: FreeFireApiService = FreeFireApiService.create()
) {
    /**
     * Busca un jugador en múltiples regiones.
     * 
     * @param uid El ID del jugador.
     * @param onConsoleLog Callback para registrar mensajes en la consola.
     * @return El [PlayerResponse] encontrado o null si no se localizó en ninguna región.
     */
    suspend fun searchAcrossRegions(
        uid: String,
        onConsoleLog: (String) -> Unit
    ): PlayerResponse? {
        val servers = listOf("us", "br", "ind", "sg", "id")
        
        for (server in servers) {
            onConsoleLog("\nRASTREANDO REGIÓN: ${server.uppercase()}...")
            
            try {
                val response = ffApiService.getPlayerInfo(server, uid)
                if (response.isSuccessful && response.body()?.basicInfo != null) {
                    return response.body()
                } else if (!response.isSuccessful) {
                    onConsoleLog("\n❌ RESPUESTA FALLIDA: ${response.code()}")
                }
            } catch (e: java.io.IOException) {
                onConsoleLog("\n⚠️ ERROR DE RED: ${e.message}")
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                val errorMsg = e.localizedMessage ?: e.message ?: "Error desconocido"
                onConsoleLog("\n⚠️ ERROR: $errorMsg")
            }
        }
        return null
    }
}
