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
    private val firebaseService: com.luxury.cheats.services.firebase.FirebaseService,
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
        private const val BITMAP_QUALITY = 100
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
            val isLicenseMode = preferencesService.accessLicenseMode()

            _uiState.update { state ->
                val images = preferencesService.accessImages()
                val remote = preferencesService.accessRemoteUrls()
                state.copy(
                    username = username,
                    userId = cache?.get("id") ?: username,
                    tier = cache?.get("tier") ?: if (isLicenseMode) "free" else "vip",
                    profileImageUri = images.first ?: remote.first,
                    bannerImageUri = images.second ?: remote.second,
                    creatorProfileUrl = preferencesService.accessCreatorUrl(),
                    androidVersion = android.os.Build.VERSION.RELEASE,
                    targetSdk = android.os.Build.VERSION.SDK_INT.toString(),
                    architecture = (android.os.Build.SUPPORTED_ABIS.firstOrNull() ?: "").uppercase(),
                    appVersion = com.luxury.cheats.BuildConfig.VERSION_NAME,
                    ram = getRamInfo(),
                    model = android.os.Build.MODEL.uppercase(),
                )
            }

            // Actualizar imágenes desde Firebase en segundo plano para futuros inicios
            @Suppress("TooGenericExceptionCaught")
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val remoteIcon = firebaseService.fetchImageUrl("IconoPerfil")
                    val remoteBanner = firebaseService.fetchImageUrl("FondoPerfil")

                    if (remoteIcon != null || remoteBanner != null) {
                        preferencesService.accessRemoteUrls(profile = remoteIcon, banner = remoteBanner)

                        _uiState.update { state ->
                            val currentImages = preferencesService.accessImages()
                            state.copy(
                                profileImageUri = currentImages.first ?: remoteIcon ?: state.profileImageUri,
                                bannerImageUri = currentImages.second ?: remoteBanner ?: state.bannerImageUri,
                            )
                        }
                    }

                    // Actualizar imagen del creador
                    val creatorUrl = firebaseService.fetchImageUrl("Perfil")
                    if (creatorUrl != null) {
                        preferencesService.accessCreatorUrl(creatorUrl)
                        _uiState.update { it.copy(creatorProfileUrl = creatorUrl) }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("PerfilViewModel", "Error updating remote profile images", e)
                }
            }

            if (cache != null) {
                processUserData(
                    JSONObject().apply {
                        put("_key", cache["id"])
                        put("createdAt", cache["created"])
                        put("expirationDate", cache["expiry"])
                        put("device", cache["device"])
                        put("tier", cache["tier"] ?: if (isLicenseMode) "free" else "vip")
                    },
                    persistInCache = false,
                )
            }

            if (isLicenseMode) {
                fetchRemoteLicenseData(username)
            } else {
                fetchRemoteUserData(username)
            }
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

    @Suppress("TooGenericExceptionCaught")
    private fun fetchRemoteUserData(username: String) {
        viewModelScope.launch {
            try {
                authService.getUserData(username) { snapshot ->
                    if (snapshot.exists()) {
                        val json = JSONObject()
                        snapshot.children.forEach { child ->
                            json.put(child.key ?: "", child.value)
                        }
                        json.put("_key", snapshot.key)
                        val tier = json.optString("tier").takeIf { it.isNotEmpty() } ?: "vip"
                        json.put("tier", tier)
                        processUserData(json)
                    }
                }
            } catch (error: Exception) {
                android.util.Log.e("PerfilViewModel", "Error fetching remote user data", error)
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun fetchRemoteLicenseData(key: String) {
        viewModelScope.launch {
            try {
                authService.getLicenseData(key) { snapshot ->
                    if (snapshot.exists()) {
                        val json = JSONObject()
                        snapshot.children.forEach { child ->
                            json.put(child.key ?: "", child.value)
                        }
                        json.put("_key", snapshot.key)
                        val tier = json.optString("tier").takeIf { it.isNotEmpty() } ?: "free"
                        json.put("tier", tier)
                        processUserData(json)
                    }
                }
            } catch (error: Exception) {
                android.util.Log.e("PerfilViewModel", "Error fetching remote license data", error)
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
        val tier = userData.optString("tier", "free")

        val (creationDate, creationHour) = parseCreationDate(createdAt)
        val (expiryDateText, remainingDays, isVip) = parseExpirationDate(expirationDate)

        if (persistInCache) {
            preferencesService.accessProfileCache(
                mapOf(
                    "id" to (firebaseId ?: ""),
                    "created" to createdAt,
                    "expiry" to expirationDate,
                    "device" to deviceFromDb,
                    "tier" to tier,
                ),
            )
        }

        _uiState.update {
            it.copy(
                userId = firebaseId,
                isVip = isVip,
                tier = tier,
                creationDate = if (creationDate.isNotEmpty()) creationDate else it.creationDate,
                creationHour = if (creationHour.isNotEmpty()) creationHour else it.creationHour,
                expiryDate = if (expiryDateText.isNotEmpty()) expiryDateText else it.expiryDate,
                remainingDays = if (remainingDays.isNotEmpty()) remainingDays else it.remainingDays,
                model = if (deviceFromDb.isNotEmpty()) deviceFromDb.uppercase() else it.model,
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
            is PerfilAction.SaveProfileClicked -> saveProfileToGallery(action.bitmap)
            else -> {}
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun saveProfileToGallery(bitmap: android.graphics.Bitmap) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val filename = "Luxury_Profile_${System.currentTimeMillis()}.png"
                val contentValues =
                    android.content.ContentValues().apply {
                        put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            val path = android.os.Environment.DIRECTORY_PICTURES + "/LuxuryCheats"
                            put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, path)
                            put(android.provider.MediaStore.MediaColumns.IS_PENDING, 1)
                        }
                    }

                val resolver = context.contentResolver
                val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    resolver.openOutputStream(it)?.use { stream ->
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, BITMAP_QUALITY, stream)
                    }

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(android.provider.MediaStore.MediaColumns.IS_PENDING, 0)
                        resolver.update(it, contentValues, null, null)
                    }

                    // Notificar al usuario (usando Toast en Main Thread)
                    viewModelScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                        android.widget.Toast.makeText(
                            context,
                            "Perfil guardado en Galería",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("PerfilViewModel", "Error saving profile to gallery", e)
                viewModelScope.launch(kotlinx.coroutines.Dispatchers.Main) {
                    android.widget.Toast.makeText(
                        context,
                        "Error al guardar perfil",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
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
