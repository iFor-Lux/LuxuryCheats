package com.luxury.cheats.services

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Repositorio para gestionar la búsqueda y obtención de datos de jugadores.
 * Centraliza la lógica de búsqueda multirregión.
 */
import javax.inject.Inject

/**
 * Repositorio para gestionar la búsqueda y obtención de datos de jugadores.
 * Centraliza la lógica de búsqueda multirregión.
 */
class PlayerRepository @Inject constructor(
    private val ffApiService: FreeFireApiService
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
    ): PlayerResponse? = coroutineScope {
        val servers = listOf("us", "br", "ind", "sg", "id")
        val resultDeferred = CompletableDeferred<PlayerResponse?>()
        
        // Disparamos todas las búsquedas en paralelo
        val jobs = servers.map { server ->
            launch {
                try {
                    onConsoleLog("\nRASTREANDO REGIÓN: ${server.uppercase()}...")
                    val response = ffApiService.getPlayerInfo(server, uid)
                    
                    if (response.isSuccessful && response.body()?.basicInfo != null) {
                        // Si encontramos al jugador, completamos el resultado
                        resultDeferred.complete(response.body())
                    }
                } catch (e: Exception) {
                    // Ignoramos errores individuales para permitir que otras regiones sigan buscando
                }
            }
        }

        // Corrutina auxiliar para detectar cuando todas las regiones han fallado
        launch {
            jobs.forEach { it.join() }
            if (!resultDeferred.isCompleted) {
                resultDeferred.complete(null)
            }
        }

        val finalResult = resultDeferred.await()
        
        // Cancelamos cualquier búsqueda que siga en curso para ahorrar recursos
        coroutineContext[Job]?.cancelChildren()
        
        finalResult
    }
}
