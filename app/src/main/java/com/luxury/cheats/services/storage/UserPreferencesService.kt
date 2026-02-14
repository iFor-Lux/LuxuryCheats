package com.luxury.cheats.services.storage

import android.content.Context
import android.content.SharedPreferences
import com.luxury.cheats.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Servicio para persistir datos locales del usuario (Credenciales, Configuración).
 */
class UserPreferencesService
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val prefs: SharedPreferences = context.getSharedPreferences("luxury_prefs", Context.MODE_PRIVATE)

        /** Claves de almacenamiento y semillas de ofuscación. */
        companion object {
            private const val KEY_SAVE_USER = "save_user"
            private const val KEY_USERNAME = "remembered_user"
            private const val KEY_PASSWORD = "remembered_pass"
            private const val KEY_PLAYER_ID = "player_id"
            private const val KEY_SEGURIDAD_UNLOCKED = "seguridad_unlocked"
            private const val KEY_ID_CONSOLE_VISIBLE = "id_console_visible"
            private const val KEY_OPCIONES_VISIBLE = "opciones_visible"
            private const val KEY_CONSOLE_OUTPUT = "console_output"
            private const val KEY_SEARCH_SUCCESSFUL = "search_successful"
            private const val KEY_CHEAT_OPTIONS = "cheat_options"
            private const val KEY_PROFILE_IMAGE = "profile_image_uri"
            private const val KEY_BANNER_IMAGE = "banner_image_uri"
            private const val KEY_PROFILE_ID = "profile_id"
            private const val KEY_PROFILE_CREATED = "profile_created"
            private const val KEY_PROFILE_EXPIRY = "profile_expiry"
            private const val KEY_PROFILE_DEVICE = "profile_device"
            private const val KEY_LAST_UPDATE_VERSION = "last_update_version"
            private const val KEY_LAST_UPDATE_TIMESTAMP = "last_update_timestamp"
            private const val KEY_SEEN_NOTIFICATIONS = "seen_notifications"
            private const val XOR_SEED = 0x55
        }

        /** Gestiona el estado de "Guardar Usuario" y credenciales ofuscadas. */
        fun accessCredentials(
            data: Pair<String, String>? = null,
            clear: Boolean = false,
        ): Pair<String, String>? {
            if (clear) {
                update {
                    remove(KEY_USERNAME)
                    remove(KEY_PASSWORD)
                }
                return null
            }
            if (data != null) {
                update {
                    putString(KEY_USERNAME, data.first)
                    putString(KEY_PASSWORD, PreferenceHelper.xor(data.second))
                }
            }
            val user = prefs.getString(KEY_USERNAME, null) ?: return null
            val pass = prefs.getString(KEY_PASSWORD, null) ?: return null
            return user to PreferenceHelper.xor(pass)
        }

        /** Gestiona si la opción de recordar usuario está activa. */
        fun saveUserFeature(enabled: Boolean? = null): Boolean {
            enabled?.let {
                update {
                    putBoolean(KEY_SAVE_USER, it)
                    if (!it) accessCredentials(clear = true)
                }
            }
            return prefs.getBoolean(KEY_SAVE_USER, false)
        }

        /** Gestiona el ID del jugador y el estado extendido de la búsqueda. */
        fun accessSearchData(
            id: String? = null,
            results: Pair<String, Boolean>? = null,
        ): Pair<String, Pair<String, Boolean>> {
            update {
                id?.let { putString(KEY_PLAYER_ID, it) }
                results?.let {
                    putString(KEY_CONSOLE_OUTPUT, it.first)
                    putBoolean(KEY_SEARCH_SUCCESSFUL, it.second)
                }
            }
            val currentId = prefs.getString(KEY_PLAYER_ID, "") ?: ""
            val console = prefs.getString(KEY_CONSOLE_OUTPUT, "") ?: ""
            val success = prefs.getBoolean(KEY_SEARCH_SUCCESSFUL, false)
            return currentId to (console to success)
        }

        /** Gestiona el estado de la pantalla Home. */
        fun accessHomeState(state: Triple<Boolean, Boolean, Boolean>? = null): Triple<Boolean, Boolean, Boolean> {
            if (state != null) {
                prefs.edit()
                    .putBoolean(KEY_SEGURIDAD_UNLOCKED, state.first)
                    .putBoolean(KEY_ID_CONSOLE_VISIBLE, state.second)
                    .putBoolean(KEY_OPCIONES_VISIBLE, state.third)
                    .apply()
            }
            return Triple(
                prefs.getBoolean(KEY_SEGURIDAD_UNLOCKED, false),
                prefs.getBoolean(KEY_ID_CONSOLE_VISIBLE, false),
                prefs.getBoolean(KEY_OPCIONES_VISIBLE, false),
            )
        }

        /** Gestiona las imágenes de perfil y banner. */
        fun accessImages(
            profile: String? = null,
            banner: String? = null,
        ): Pair<String?, String?> {
            update {
                profile?.let { putString(KEY_PROFILE_IMAGE, it) }
                banner?.let { putString(KEY_BANNER_IMAGE, it) }
            }
            return prefs.getString(KEY_PROFILE_IMAGE, null) to prefs.getString(KEY_BANNER_IMAGE, null)
        }

        /** Gestiona el caché del perfil. */
        fun accessProfileCache(data: Map<String, String>? = null): Map<String, String>? {
            if (data != null) {
                prefs.edit()
                    .putString(KEY_PROFILE_ID, data["id"])
                    .putString(KEY_PROFILE_CREATED, data["created"])
                    .putString(KEY_PROFILE_EXPIRY, data["expiry"])
                    .putString(KEY_PROFILE_DEVICE, data["device"])
                    .apply()
            }
            val id = prefs.getString(KEY_PROFILE_ID, null) ?: return null
            return mapOf(
                "id" to id,
                "created" to (prefs.getString(KEY_PROFILE_CREATED, "") ?: ""),
                "expiry" to (prefs.getString(KEY_PROFILE_EXPIRY, "") ?: ""),
                "device" to (prefs.getString(KEY_PROFILE_DEVICE, "") ?: ""),
            )
        }

        /** Gestiona los cheats activos y su persistencia. */
        fun accessCheatOptions(options: Map<String, Boolean>? = null): Map<String, Boolean> {
            options?.let {
                val encoded = it.entries.joinToString(",") { entry -> "${entry.key}:${entry.value}" }
                update { putString(KEY_CHEAT_OPTIONS, encoded) }
            }
            val encoded = prefs.getString(KEY_CHEAT_OPTIONS, "") ?: ""
            if (encoded.isEmpty()) return emptyMap()
            return encoded.split(",").associate {
                val parts = it.split(":")
                parts[0] to parts[1].toBoolean()
            }
        }

        /** Gestiona la información de la última versión y fecha de lanzamiento. */
        fun accessUpdateInfo(
            version: String? = null,
            timestamp: String? = null,
        ): Pair<String, String>? {
            update {
                version?.let { putString(KEY_LAST_UPDATE_VERSION, it) }
                timestamp?.let { putString(KEY_LAST_UPDATE_TIMESTAMP, it) }
            }
            val v = prefs.getString(KEY_LAST_UPDATE_VERSION, null) ?: return null
            val t = prefs.getString(KEY_LAST_UPDATE_TIMESTAMP, "") ?: ""
            return v to t
        }

        /** Gestiona el conjunto de IDs de notificaciones ya vistas. */
        fun accessSeenNotifications(idToMark: String? = null): Set<String> {
            val currentSeen = prefs.getStringSet(KEY_SEEN_NOTIFICATIONS, emptySet()) ?: emptySet()

            if (idToMark != null && !currentSeen.contains(idToMark)) {
                val updated = currentSeen.toMutableSet().apply { add(idToMark) }
                update { putStringSet(KEY_SEEN_NOTIFICATIONS, updated) }
                return updated
            }
            return currentSeen
        }

        private object PreferenceHelper {
            fun xor(data: String): String {
                val key = XOR_SEED xor BuildConfig.VERSION_CODE
                return data.map { (it.code xor key).toChar() }.joinToString("")
            }
        }

        /** Utility for batch editing SharedPreferences */
        private fun update(block: SharedPreferences.Editor.() -> Unit) {
            prefs.edit().apply(block).apply()
        }
    }
