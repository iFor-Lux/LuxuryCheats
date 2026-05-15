package com.luxury.cheats.services.storage

import android.content.Context
import android.content.SharedPreferences
import com.luxury.cheats.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio para persistir datos locales del usuario (Credenciales, Configuración).
 */
@Singleton
class UserPreferencesService
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val prefs: SharedPreferences = context.getSharedPreferences("luxury_prefs", Context.MODE_PRIVATE)

        private val _antiRecordingFlow = MutableStateFlow(prefs.getBoolean(KEY_ANTI_RECORDING, false))
        val antiRecordingFlow: StateFlow<Boolean> = _antiRecordingFlow.asStateFlow()

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
            private const val KEY_PROFILE_TIER = "profile_tier"
            private const val KEY_LAST_UPDATE_VERSION = "last_update_version"
            private const val KEY_LAST_UPDATE_TIMESTAMP = "last_update_timestamp"
            private const val KEY_SEEN_NOTIFICATIONS = "seen_notifications"
            private const val KEY_IS_LICENSE_MODE = "is_license_mode"
            private const val KEY_REMOTE_PROFILE_URL = "remote_profile_url"
            private const val KEY_REMOTE_BANNER_URL = "remote_banner_url"
            private const val KEY_CREATOR_PROFILE_URL = "creator_profile_url"
            
            // Claves para Floating Preview Widget (Tools)

            private const val KEY_FLOATING_WIDTH = "floating_width"
            private const val KEY_FLOATING_HEIGHT = "floating_height"
            private const val KEY_FLOATING_CENTER_X = "floating_center_x"
            private const val KEY_FLOATING_CENTER_Y = "floating_center_y"
            private const val KEY_FLOATING_STROKE_WIDTH = "floating_stroke_width"
            private const val KEY_FLOATING_STROKE_ENABLED = "floating_stroke_enabled"
            private const val KEY_FLOATING_STROKE_COLOR = "floating_stroke_color"
            private const val KEY_ANTI_RECORDING = "anti_recording_enabled"
            private const val KEY_AIMBOT_STRENGTH = "aimbot_strength"

            private const val DEFAULT_FLOATING_WIDTH = 180
            private const val DEFAULT_FLOATING_HEIGHT = 35
            private const val DEFAULT_FLOATING_X = 200
            private const val DEFAULT_FLOATING_Y = 400
            private const val DEFAULT_STROKE_COLOR = 0xFFFFFFFF
            private const val DEFAULT_AIMBOT_STRENGTH = 50

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

        /** Gestiona las imágenes de perfil y banner locales. */
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

        /** Gestiona las URLs remotas de perfil y banner (Firebase). */
        fun accessRemoteUrls(
            profile: String? = null,
            banner: String? = null,
        ): Pair<String?, String?> {
            update {
                profile?.let { putString(KEY_REMOTE_PROFILE_URL, it) }
                banner?.let { putString(KEY_REMOTE_BANNER_URL, it) }
            }
            return prefs.getString(KEY_REMOTE_PROFILE_URL, null) to prefs.getString(KEY_REMOTE_BANNER_URL, null)
        }

        /** Gestiona la URL del perfil del creador (Firebase). */
        fun accessCreatorUrl(url: String? = null): String? {
            url?.let {
                update { putString(KEY_CREATOR_PROFILE_URL, it) }
            }
            return prefs.getString(KEY_CREATOR_PROFILE_URL, null)
        }

        /** Gestiona si se ha iniciado sesión con una licencia. */
        fun accessLicenseMode(isLicense: Boolean? = null): Boolean {
            isLicense?.let {
                update { putBoolean(KEY_IS_LICENSE_MODE, it) }
            }
            return prefs.getBoolean(KEY_IS_LICENSE_MODE, false)
        }

        /** Gestiona el caché del perfil. */
        fun accessProfileCache(data: Map<String, String>? = null): Map<String, String>? {
            if (data != null) {
                prefs.edit()
                    .putString(KEY_PROFILE_ID, data["id"])
                    .putString(KEY_PROFILE_CREATED, data["created"])
                    .putString(KEY_PROFILE_EXPIRY, data["expiry"])
                    .putString(KEY_PROFILE_DEVICE, data["device"])
                    .putString(KEY_PROFILE_TIER, data["tier"])
                    .apply()
            }
            val id = prefs.getString(KEY_PROFILE_ID, null) ?: return null
            return mapOf(
                "id" to id,
                "created" to (prefs.getString(KEY_PROFILE_CREATED, "") ?: ""),
                "expiry" to (prefs.getString(KEY_PROFILE_EXPIRY, "") ?: ""),
                "device" to (prefs.getString(KEY_PROFILE_DEVICE, "") ?: ""),
                "tier" to (prefs.getString(KEY_PROFILE_TIER, "free") ?: "free"),
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

        /** Gestiona la persistencia de las coordenadas y estilos del Widget Flotante de Previsualización. */
        fun accessFloatingConfig(
            width: Int? = null,
            height: Int? = null,
            centerX: Int? = null,
            centerY: Int? = null,
            strokeWidth: Float? = null,
            isStrokeEnabled: Boolean? = null,
            strokeColor: Long? = null
        ): Map<String, Any> {
            update {
                width?.let { putInt(KEY_FLOATING_WIDTH, it) }
                height?.let { putInt(KEY_FLOATING_HEIGHT, it) }
                centerX?.let { putInt(KEY_FLOATING_CENTER_X, it) }
                centerY?.let { putInt(KEY_FLOATING_CENTER_Y, it) }
                strokeWidth?.let { putFloat(KEY_FLOATING_STROKE_WIDTH, it) }
                isStrokeEnabled?.let { putBoolean(KEY_FLOATING_STROKE_ENABLED, it) }
                strokeColor?.let { putLong(KEY_FLOATING_STROKE_COLOR, it) }
            }
            
            return mapOf(
                "width" to prefs.getInt(KEY_FLOATING_WIDTH, DEFAULT_FLOATING_WIDTH),
                "height" to prefs.getInt(KEY_FLOATING_HEIGHT, DEFAULT_FLOATING_HEIGHT),
                "centerX" to prefs.getInt(KEY_FLOATING_CENTER_X, DEFAULT_FLOATING_X),
                "centerY" to prefs.getInt(KEY_FLOATING_CENTER_Y, DEFAULT_FLOATING_Y),
                "strokeWidth" to prefs.getFloat(KEY_FLOATING_STROKE_WIDTH, 0f),
                "isStrokeEnabled" to prefs.getBoolean(KEY_FLOATING_STROKE_ENABLED, false),
                "strokeColor" to prefs.getLong(KEY_FLOATING_STROKE_COLOR, DEFAULT_STROKE_COLOR)
            )
        }

        fun accessAntiRecording(enabled: Boolean? = null): Boolean {
            enabled?.let {
                update { putBoolean(KEY_ANTI_RECORDING, it) }
                _antiRecordingFlow.value = it
            }
            return prefs.getBoolean(KEY_ANTI_RECORDING, false)
        }

        fun accessAimbotConfig(strength: Int? = null): Int {
            strength?.let {
                update { putInt(KEY_AIMBOT_STRENGTH, it) }
            }
            return prefs.getInt(KEY_AIMBOT_STRENGTH, DEFAULT_AIMBOT_STRENGTH) // 50% por defecto
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
