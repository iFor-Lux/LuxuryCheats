package com.luxury.cheats.navigations

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
 * Definición de rutas de navegación mediante objetos type-safe.
 * Utiliza Kotlin Serialization para evitar errores tipográficos y errores en tiempo de ejecución.
 */
@Serializable
@Keep
object AuthGraph

@Serializable
@Keep
object MainGraph

@Serializable
@Keep
object Splash

@Serializable
@Keep
object WelcomePage1

@Serializable
@Keep
object WelcomePage2

@Serializable
@Keep
object WelcomePage3

@Serializable
@Keep
object Login

@Serializable
@Keep
object Home

@Serializable
@Keep
object Perfil

@Serializable
@Keep
object Update
