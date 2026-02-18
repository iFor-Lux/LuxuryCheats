package com.luxury.cheats.features.perfil.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * ViewModel para la pantalla de Perfil.
 * Versión Estricta: Solo muestra datos reales de Firebase y el dispositivo. Sin fallbacks.
 */
class PerfilViewModel(
    private val preferencesService: com.luxury.cheats.services.storage.UserPreferencesService,
    private val authService: com.luxury.cheats.services.firebase.AuthService,
    private val context: android.content.Context,
    private val fileService: com.luxury.cheats.services.storage.FileService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilState())
    val uiState: StateFlow<PerfilState> = _uiState.asStateFlow()

    /**
     * Constantes para el procesamiento de datos de perfil y formateo.
     */
    companion object {
        private const val BYTES_TO_GB = 1024.0
        private const val YEAR_CHARS = 4
        private const val TIME_CHARS = 5
        private const val SHORT_DATE_CHARS = 10
    }

    init {
        viewModelScope.launch {
            loadAllData()
        }
    }

    private suspend fun loadAllData() {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val credentials = preferencesService.accessCredentials()
            val username = credentials?.first ?: return@withContext

            val cache = preferencesService.accessProfileCache()

            _uiState.update { state ->
                val images = preferencesService.accessImages()
                state.copy(
                    username = username,
                    userId = cache?.get("id") ?: username,
                    profileImageUri = images.first,
                    bannerImageUri = images.second,
                    androidVersion = android.os.Build.VERSION.RELEASE,
                    targetSdk = android.os.Build.VERSION.SDK_INT.toString(),
                    architecture = (android.os.Build.SUPPORTED_ABIS.firstOrNull() ?: "").uppercase(),
                    appVersion = com.luxury.cheats.BuildConfig.VERSION_NAME,
                    ram = getRamInfo(),
                )
            }

            if (cache != null) {
                processUserData(
                    JSONObject().apply {
                        put("_key", cache["id"])
                        put("createdAt", cache["created"])
                        put("expirationDate", cache["expiry"])
                        put("device", cache["device"])
                    },
                    persistInCache = false,
                )
            }

            fetchRemoteData(username)
        }
    }


    private fun getRamInfo(): String {
        return try {
            val service = context.getSystemService(android.content.Context.ACTIVITY_SERVICE)
            val activityManager = service as android.app.ActivityManager
            val memoryInfo = android.app.ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            val gb = memoryInfo.totalMem / (BYTES_TO_GB * BYTES_TO_GB * BYTES_TO_GB)
            "${Math.round(gb).toInt()} GB"
        } catch (ignored: RuntimeException) {
            "0 GB"
        }
    }

    private fun fetchRemoteData(username: String) {
        viewModelScope.launch {
            try {
                authService.getUserData(username) { snapshot ->
                    if (snapshot.exists()) {
                        val json = JSONObject()
                        snapshot.children.forEach { child ->
                            json.put(child.key ?: "", child.value)
                        }
                        json.put("_key", snapshot.key)
                        processUserData(json)
                    }
                }
            } catch (error: com.google.firebase.database.DatabaseException) {
                // Error silencioso en carga remota, se mantiene el estado previo/caché
                android.util.Log.e("PerfilViewModel", "Error fetching remote data", error)
            }
        }
    }

    private fun processUserData(
        userData: JSONObject,
        persistInCache: Boolean = true,
    ) {
        val createdAt = userData.optString("createdAt")
        val expirationDate = userData.optString("expirationDate")
        val firebaseId = userData.optString("_key")
        val deviceFromDb = userData.optString("device")

        val (creationDate, creationHour) = parseCreationDate(createdAt)
        val (expiryDateText, remainingDays, isVip) = parseExpirationDate(expirationDate)

        if (persistInCache) {
            preferencesService.accessProfileCache(
                mapOf(
                    "id" to (firebaseId ?: ""),
                    "created" to createdAt,
                    "expiry" to expirationDate,
                    "device" to deviceFromDb,
                ),
            )
        }

        _uiState.update {
            it.copy(
                userId = firebaseId,
                isVip = isVip,
                creationDate = creationDate,
                creationHour = creationHour,
                expiryDate = expiryDateText,
                remainingDays = remainingDays,
                model = deviceFromDb.uppercase(),
            )
        }
    }

    private fun parseCreationDate(createdAt: String): Pair<String, String> {
        if (createdAt.isEmpty()) return "" to ""

        return try {
            val zoneId = java.time.ZoneId.systemDefault()
            val createdZoned =
                if (createdAt.all { it.isDigit() }) {
                    ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(createdAt.toLong()), zoneId)
                } else {
                    ZonedDateTime.parse(createdAt, DateTimeFormatter.ISO_ZONED_DATE_TIME).withZoneSameInstant(zoneId)
                }
            createdZoned.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) to
                createdZoned.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH))
        } catch (ignored: Exception) {
            parseLegacyDate(createdAt)
        }
    }

    private fun parseLegacyDate(createdAt: String): Pair<String, String> {
        if (!createdAt.contains(" ")) return createdAt to ""

        val parts = createdAt.split(" ")
        val datePart = parts[0].replace("-", "/")
        val formattedDate =
            if (datePart.contains("/") && datePart.split("/")[0].length == YEAR_CHARS) {
                val d = datePart.split("/")
                "${d[2]}/${d[1]}/${d[0]}"
            } else {
                datePart
            }

        return formattedDate to parts[1].take(TIME_CHARS)
    }

    private fun parseExpirationDate(expirationDate: String): Triple<String, String, Boolean> {
        if (expirationDate.isEmpty() || expirationDate == "null") return Triple("", "", false)

        return try {
            val expiryZoned = ZonedDateTime.parse(expirationDate, DateTimeFormatter.ISO_ZONED_DATE_TIME)
            val days = ChronoUnit.DAYS.between(ZonedDateTime.now(), expiryZoned)
            val isVip = days >= 0
            Triple(expiryZoned.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), "$days DÍAS", isVip)
        } catch (ignored: Exception) {
            Triple(expirationDate.take(SHORT_DATE_CHARS).replace("-", "/"), "", true)
        }
    }

    /**
     * Gestiona las acciones de usuario para el perfil.
     *
     * @param action Acción a ser procesada.
     */
    fun handleAction(action: PerfilAction) {
        when (action) {
            PerfilAction.LogoutClicked -> preferencesService.accessCredentials(clear = true)
            is PerfilAction.ProfileImageSelected -> updateProfileImage(action.uri)
            is PerfilAction.BannerImageSelected -> updateBannerImage(action.uri)
            else -> {}
        }
    }

    private fun updateProfileImage(uri: String) {
        val prefix = "profile.jpg"
        val localUri = fileService.saveImageToInternal(uri, prefix)
        preferencesService.accessImages(profile = localUri)
        _uiState.update { it.copy(profileImageUri = localUri) }
    }

    private fun updateBannerImage(uri: String) {
        val prefix = "banner.jpg"
        val localUri = fileService.saveImageToInternal(uri, prefix)
        preferencesService.accessImages(banner = localUri)
        _uiState.update { it.copy(bannerImageUri = localUri) }
    }
}
