package com.luxury.cheats.navigations

import kotlinx.serialization.Serializable

/**
 * Definición de rutas de navegación mediante objetos type-safe.
 * Utiliza Kotlin Serialization para evitar errores tipográficos y errores en tiempo de ejecución.
 */
@Serializable
object AuthGraph

@Serializable
object MainGraph

@Serializable
object Splash

@Serializable
object WelcomePage1

@Serializable
object WelcomePage2

@Serializable
object WelcomePage3

@Serializable
object Login

@Serializable
object Home

@Serializable
object Perfil

@Serializable
object Update
