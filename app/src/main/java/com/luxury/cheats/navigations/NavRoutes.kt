package com.luxury.cheats.navigations

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
 * Definición de rutas de navegación mediante objetos type-safe.
 * Utiliza Kotlin Serialization para evitar errores tipográficos y errores en tiempo de ejecución.
 *
 * Grafo de navegación para el flujo de autenticación (Login, Welcome).
 */
@Serializable
@Keep
object AuthGraph

/** Grafo de navegación principal para la aplicación autenticada (Home, Perfil). */
@Serializable
@Keep
object MainGraph

/** Ruta para la pantalla de Splash inicial. */
@Serializable
@Keep
object Splash

/** Ruta para la primera página del flujo de bienvenida. */
@Serializable
@Keep
object WelcomePage1

/** Ruta para la segunda página del flujo de bienvenida (permisos). */
@Serializable
@Keep
object WelcomePage2

/** Ruta para la tercera página del flujo de bienvenida (Shizuku). */
@Serializable
@Keep
object WelcomePage3

/** Ruta para la pantalla de inicio de sesión. */
@Serializable
@Keep
object Login

/** Ruta para la pantalla principal de la aplicación. */
@Serializable
@Keep
object Home

/** Ruta para la pantalla de perfil del usuario. */
@Serializable
@Keep
object Perfil

/** Ruta para la pantalla de actualización y descarga de archivos. */
@Serializable
@Keep
object Update
