package com.luxury.cheats.services

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz de Retrofit para la API de Free Fire.
 */
interface FreeFireApiService {
    /**
     * Obtiene la información pública de un jugador.
     *
     * @param server Servidor del jugador (e.g., 'BR', 'US').
     * @param uid Identificador único del jugador.
     * @return [Response] con los datos del [PlayerResponse].
     */
    @GET("get_player_personal_show")
    suspend fun getPlayerInfo(
        @Query("server") server: String,
        @Query("uid") uid: String
    ): Response<PlayerResponse>

    /** Fábrica para crear instancias del servicio de API. */
    companion object {
        private const val BASE_URL = "https://freefire-api-six.vercel.app/"

        /**
         * Crea una instancia configurada del servicio con interceptores de seguridad.
         *
         * @return Instancia de [FreeFireApiService].
         */
        fun create(): FreeFireApiService {
            val client = okhttp3.OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val userAgent = "Mozilla/5.0 (Linux; Android 13; Pixel 7) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/116.0.0.0 Mobile Safari/537.36"
                    val request = chain.request().newBuilder()
                        .header("User-Agent", userAgent)
                        .build()
                    chain.proceed(request)
                }
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FreeFireApiService::class.java)
        }
    }
}
