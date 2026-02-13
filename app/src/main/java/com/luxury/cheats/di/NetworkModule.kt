package com.luxury.cheats.di

import com.luxury.cheats.services.freefireapi.FreeFireApiService
import com.luxury.cheats.services.firebase.UrlService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFreeFireApiService(): FreeFireApiService {
        return FreeFireApiService.create()
    }
}
