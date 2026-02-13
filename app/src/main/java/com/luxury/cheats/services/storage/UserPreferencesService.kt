package com.luxury.cheats.services.storage

import android.content.Context
import android.content.SharedPreferences
import com.luxury.cheats.BuildConfig

import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * Servicio para persistir datos locales del usuario (Credenciales, Configuración).
 */
class UserPreferencesService @Inject constructor(@ApplicationContext context: Context) {

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

    /** Gestiona el estado de "Guardar Usuario" y credenciales. */
    fun setSaveUserEnabled(enabled: Boolean) {
        update {
            putBoolean(KEY_SAVE_USER, enabled)
            if (!enabled) {
                remove(KEY_USERNAME)
                remove(KEY_PASSWORD)
            }
        }
    }

    /** Retorna si la opción de guardar usuario está activa. */
    fun isSaveUserEnabled(): Boolean = prefs.getBoolean(KEY_SAVE_USER, false)

    /** Guarda el usuario y la contraseña (ofuscada). */
    fun saveCredentials(username: String, password: String) {
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_PASSWORD, PreferenceHelper.xor(password))
            .apply()
    }

    /** Recupera las credenciales guardadas. */
    fun getCredentials(): Pair<String, String>? {
        val user = prefs.getString(KEY_USERNAME, null) ?: return null
        val pass = prefs.getString(KEY_PASSWORD, null) ?: return null
        return user to PreferenceHelper.xor(pass)
    }

    /** Borra las credenciales locales. */
    fun clearCredentials() {
        prefs.edit().remove(KEY_USERNAME).remove(KEY_PASSWORD).apply()
    }

    /** Gestiona el ID del jugador. */
    fun accessPlayerId(value: String? = null): String? {
        if (value != null) prefs.edit().putString(KEY_PLAYER_ID, value).apply()
        return prefs.getString(KEY_PLAYER_ID, null)
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
            prefs.getBoolean(KEY_OPCIONES_VISIBLE, false)
        )
    }

    /** Gestiona el estado extendido de Home (consola y búsqueda). */
    fun saveExtendedHomeState(console: String, searchSuccessful: Boolean) {
        prefs.edit()
            .putString(KEY_CONSOLE_OUTPUT, console)
            .putBoolean(KEY_SEARCH_SUCCESSFUL, searchSuccessful)
            .apply()
    }

    fun getExtendedHomeState(): Pair<String, Boolean> {
        return (prefs.getString(KEY_CONSOLE_OUTPUT, "") ?: "") to 
               prefs.getBoolean(KEY_SEARCH_SUCCESSFUL, false)
    }

    /** Gestiona los cheats activos. */
    fun saveCheatOptions(options: Map<String, Boolean>) {
        val encoded = options.entries.joinToString(",") { "${it.key}:${it.value}" }
        prefs.edit().putString(KEY_CHEAT_OPTIONS, encoded).apply()
    }

    fun getCheatOptions(): Map<String, Boolean> {
        val encoded = prefs.getString(KEY_CHEAT_OPTIONS, "") ?: ""
        if (encoded.isEmpty()) return emptyMap()
        return encoded.split(",").associate {
            val parts = it.split(":")
            parts[0] to parts[1].toBoolean()
        }
    }

    /** Acceso a imágenes de perfil/banner. */
    fun accessImage(isBanner: Boolean, value: String? = null): String? {
        val key = if (isBanner) KEY_BANNER_IMAGE else KEY_PROFILE_IMAGE
        if (value != null) prefs.edit().putString(key, value).apply()
        return prefs.getString(key, null)
    }

    /**
     * Recupera la imagen del banner guardada.
     * @return El URI de la imagen o null si no existe.
     */
    fun getBannerImage(): String? = accessImage(isBanner = true)

    /**
     * Recupera la imagen de perfil guardada.
     * @return El URI de la imagen o null si no existe.
     */
    fun getProfileImage(): String? = accessImage(isBanner = false)

    /**
     * Guarda la imagen de perfil o banner.
     * @param isBanner True si es banner, False si es perfil.
     * @param uri URI de la imagen a guardar.
     */
    fun saveImage(isBanner: Boolean, uri: String) { accessImage(isBanner, uri) }

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
            "device" to (prefs.getString(KEY_PROFILE_DEVICE, "") ?: "")
        )
    }
    
    /** Gestiona la información de la última versión y fecha de lanzamiento. */
    fun accessUpdateInfo(version: String? = null, timestamp: String? = null): Pair<String, String>? {
        if (version != null && timestamp != null) {
            prefs.edit()
                .putString(KEY_LAST_UPDATE_VERSION, version)
                .putString(KEY_LAST_UPDATE_TIMESTAMP, timestamp)
                .apply()
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
