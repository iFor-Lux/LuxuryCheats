package com.luxury.cheats.services.security

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para gestionar las rutas de archivos que deben ser limpiados.
 */
@Singleton
class SecurityRepository
    @Inject
    constructor(
        @ApplicationContext private val context: android.content.Context,
    ) {
        /** Constantes internas del repositorio de seguridad. */
        companion object {
            private const val KEY_INSTALLED_FILES = "installed_files"
        }

        private val prefs = context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)

        /**
         * Registra una nueva ruta de archivo instalado.
         */
        fun registerFile(path: String) {
            val files = getInstalledFiles().toMutableSet()
            files.add(path)
            prefs.edit().putStringSet(KEY_INSTALLED_FILES, files).apply()
        }

        /**
         * Obtiene la lista de todos los archivos instalados registrados.
         */
        fun getInstalledFiles(): Set<String> {
            return prefs.getStringSet(KEY_INSTALLED_FILES, emptySet()) ?: emptySet()
        }

        /**
         * Limpia la lista de archivos registrados.
         */
        fun clearRegistry() {
            prefs.edit().remove(KEY_INSTALLED_FILES).apply()
        }
    }
