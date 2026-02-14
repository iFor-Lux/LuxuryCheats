package com.luxury.cheats.di

import com.luxury.cheats.services.freefireapi.FreeFireApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de Dagger Hilt para proveer dependencias relacionadas con la red.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /**
     * Provee una instancia única de [FreeFireApiService].
     * @return Implementación de la API de Free Fire.
     */
    @Provides
    @Singleton
    fun provideFreeFireApiService(): FreeFireApiService {
        return FreeFireApiService.create()
    }
}
