package com.luxury.cheats.services.freefireapi

import androidx.annotation.Keep
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz de Retrofit para la API de Free Fire.
 */
@Keep
interface FreeFireApiService {
    /**
     * Obtiene la información pública de un jugador.
     *
     * @param server Servidor del jugador (e.g., 'BR', 'US').
     * @param uid Identificador único del jugador.
     * @return [Response] con los datos del [PlayerResponse].
     */
    @GET("get_player_personal_show")
    fun getPlayerInfo(
        @Query("server") server: String,
        @Query("uid") uid: String,
    ): Call<PlayerResponse>

    /** Fábrica para crear instancias del servicio de API. */
    companion object {
        private const val BASE_URL = "https://freefire-api-six.vercel.app/"
        private const val CONNECT_TIMEOUT_SECONDS = 30L
        private const val READ_TIMEOUT_SECONDS = 30L

        /**
         * Crea una instancia configurada del servicio con interceptores de seguridad.
         *
         * @return Instancia de [FreeFireApiService].
         */
        fun create(): FreeFireApiService {
            val client =
                okhttp3.OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT_SECONDS, java.util.concurrent.TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        val userAgent =
                            "Mozilla/5.0 (Linux; Android 13; Pixel 7) " +
                                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                                "Chrome/116.0.0.0 Mobile Safari/537.36"
                        val request =
                            chain.request().newBuilder()
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
