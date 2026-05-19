package com.luxury.cheats.features.home.logic

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luxury.cheats.BuildConfig
import com.luxury.cheats.core.ui.AppNotification
import com.luxury.cheats.core.ui.NotificationType
import com.luxury.cheats.core.util.VersionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

/**
 * ViewModel para la pantalla Home
 * Gestiona la visibilidad condicional de las secciones y el estado del usuario.
 */
@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val preferencesService: com.luxury.cheats.services.storage.UserPreferencesService,
        private val playerRepository: com.luxury.cheats.services.freefireapi.PlayerRepository,
        private val updateService: com.luxury.cheats.features.update.service.UpdateService,
        private val notificationService: com.luxury.cheats.features.home.service.InAppNotificationService,
        private val floatingWidgetManager: com.luxury.cheats.services.floating.logic.FloatingWidgetManager,
        private val firebaseService: com.luxury.cheats.services.firebase.FirebaseService,
        @ApplicationContext private val context: android.content.Context,
    ) : ViewModel() {
        private val adminComponent =
            android.content.ComponentName(
                context,
                com.luxury.cheats.core.receiver.LuxuryDeviceAdminReceiver::class.java,
            )

        private val _uiState = MutableStateFlow(HomeState())
        val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

        init {
            initializeData()
            val (_, searchResults) = preferencesService.accessSearchData()
            val cache = preferencesService.accessProfileCache()
            val isLicenseMode = preferencesService.accessLicenseMode()
            // Determinación de nivel (free, vip, plus) con fallbacks por tipo de inicio
            val computedTier = cache?.get("tier") ?: if (isLicenseMode) "free" else "vip"

            _uiState.update {
                it.copy(
                    isSeguridadUnlocked = false,
                    isIdAndConsoleVisible = false,
                    isOpcionesVisible = false,
                    consoleOutput = searchResults.first,
                    isSearchSuccessful = searchResults.second,
                    cheatOptions = emptyMap(),
                    idValue = preferencesService.accessSearchData().first.ifEmpty { it.idValue },
                    tier = computedTier,
                )
            }
        }

        private fun initializeData() {
            updateGreeting()
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                fetchHomeImage()
            }
            observeRealtimeUpdates()
            observeRealtimeNotifications()
            checkGifts()
        }

        private suspend fun fetchHomeImage() {
            val homeUrl =
                firebaseService.fetchImageUrl("Home")
                    ?: firebaseService.fetchImageUrl("home")

            val diamantesUrl =
                firebaseService.fetchImageUrl("Diamantes")
                    ?: firebaseService.fetchImageUrl("diamantes")

            _uiState.update { it.copy(homeImageUrl = homeUrl, diamantesImageUrl = diamantesUrl) }
        }

        @Suppress("TooGenericExceptionCaught")
        private fun observeRealtimeUpdates() {
            updateService.observeAppUpdate()
                .onEach { update ->
                    val localVersion = BuildConfig.VERSION_NAME
                    android.util.Log.d(
                        "HomeViewModel",
                        "Realtime Update -> Remote: ${update.version}, Local: $localVersion, Active: ${update.active}",
                    )

                    if (update.active && VersionUtils.isVersionNewer(update.version, localVersion)) {
                        _uiState.update { it.copy(appUpdate = update) }
                    } else {
                        _uiState.update { it.copy(appUpdate = null) }
                    }
                }
                .launchIn(viewModelScope)
        }

        private fun observeRealtimeNotifications() {
            notificationService.observeInAppNotifications()
                .onEach { all ->
                    val seen = preferencesService.accessSeenNotifications()
                    val toShow = all.firstOrNull { it.frequency != "always" && !seen.contains(it.id) }
                        ?: all.firstOrNull { it.frequency == "always" }

                    _uiState.update { it.copy(currentInAppNotification = toShow) }
                }
                .launchIn(viewModelScope)
        }

        /**
         * Carga los pesos de los archivos solo cuando sea necesario (on-demand).
         */
        fun loadFileWeights() {
            if (_uiState.value.fileWeightsCache.isNotEmpty()) return

            viewModelScope.launch {
                val weightsMap =
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                        val downloadService = com.luxury.cheats.features.download.service.DownloadService()
                        val cheatNames = listOf("Aimbot", "Holograma", "WallHack", "AimFov")
                        val map = mutableMapOf<String, String>()

                        for (name in cheatNames) {
                            try {
                                val info = downloadService.getDownloadInfo(name)
                                if (info.active && info.url.isNotEmpty()) {
                                    map[name] = downloadService.getFileSize(info.url)
                                }
                            } catch (e: java.io.IOException) {
                                android.util.Log.w("HomeViewModel", "Error fetching file weight for $name", e)
                            }
                        }
                        map
                    }

                _uiState.update { it.copy(fileWeightsCache = weightsMap) }
            }
        }

        private fun updateGreeting() {
            val username = preferencesService.accessCredentials()?.first ?: ""
            val now = java.time.LocalDateTime.now()
            val (greeting, subtitle) =
                HomeGreetingProvider.getGreetingForTimeAndDate(
                    now.hour,
                    now.monthValue,
                    now.dayOfMonth,
                )
            _uiState.update { it.copy(userName = username, greeting = greeting, greetingSubtitle = subtitle) }
        }

        private fun checkGifts() {
            val cache = preferencesService.accessProfileCache()
            val userId = cache?.get("id") ?: ""
            
            // Primero verificamos el local para respuesta inmediata
            val localGift = preferencesService.accessGiftDays()
            if (localGift > 0) {
                _uiState.update {
                    it.copy(
                        giftDaysPending = localGift,
                        showGiftDialog = true
                    )
                }
            }

            // Luego consultamos Firebase en tiempo real para estar al día
            if (userId.isNotEmpty()) {
                viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        val regaloRef = com.google.firebase.database.FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(userId)
                            .child("regalo")
                        
                        val snapshot = regaloRef.get().await()
                        val giftDays = snapshot.getValue(Int::class.java) ?: 0
                        if (giftDays > 0) {
                            preferencesService.accessGiftDays(giftDays)
                            _uiState.update {
                                it.copy(
                                    giftDaysPending = giftDays,
                                    showGiftDialog = true
                                )
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("HomeViewModel", "Error fetching real-time gift days", e)
                    }
                }
            }
        }

        private fun claimGift() {
            val cache = preferencesService.accessProfileCache()
            val userId = cache?.get("id") ?: ""
            val giftDays = _uiState.value.giftDaysPending

            // Cerrar el diálogo inmediatamente y activar confeti para una espectacular UX
            _uiState.update { it.copy(
                showGiftDialog = false,
                showConfetti = true
            ) }

            // Programar apagado del efecto de confeti tras 4 segundos (tiempo de la animación)
            viewModelScope.launch {
                kotlinx.coroutines.delay(4000L)
                _uiState.update { it.copy(showConfetti = false) }
            }

            if (giftDays > 0) {
                // Notificación Premium de Reclamado exitoso en el hilo principal
                addNotification("¡FELICIDADES! Reclamaste $giftDays días de regalo", NotificationType.SUCCESS)

                // Limpiar local
                preferencesService.accessGiftDays(0)

                // Limpiar en Firebase de forma asíncrona
                if (userId.isNotEmpty()) {
                    viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        try {
                            val userRef = com.google.firebase.database.FirebaseDatabase.getInstance()
                                .getReference("users")
                                .child(userId)
                            
                            // 1. Limpiar el regalo para que no se muestre de nuevo
                            userRef.child("regalo").setValue(0).await()

                            // 2. Sumar los días a la fecha de expiración actual del usuario
                            val snapshot = userRef.child("expirationDate").get().await()
                            val currentExpiryStr = snapshot.getValue(String::class.java) ?: ""
                            
                            val newExpiry = if (currentExpiryStr.isNotEmpty()) {
                                try {
                                    val currentExpiry = java.time.ZonedDateTime.parse(currentExpiryStr, java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME)
                                    val baseDate = if (currentExpiry.isAfter(java.time.ZonedDateTime.now())) currentExpiry else java.time.ZonedDateTime.now()
                                    baseDate.plusDays(giftDays.toLong())
                                } catch (e: Exception) {
                                    java.time.ZonedDateTime.now().plusDays(giftDays.toLong())
                                }
                            } else {
                                java.time.ZonedDateTime.now().plusDays(giftDays.toLong())
                            }

                            val newExpiryStr = newExpiry.format(java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME)
                            userRef.child("expirationDate").setValue(newExpiryStr).await()

                            // Actualizar la caché del perfil local para reflejar la nueva fecha de expiración
                            if (cache != null) {
                                val updatedCache = cache.toMutableMap().apply {
                                    put("expiry", newExpiryStr)
                                }
                                preferencesService.accessProfileCache(updatedCache)
                            }
                            
                            android.util.Log.d("HomeViewModel", "Gift claimed: $giftDays days added. New expiry: $newExpiryStr")
                        } catch (e: Exception) {
                            android.util.Log.e("HomeViewModel", "Error claiming gift from Firebase", e)
                        }
                    }
                }
            }
        }

        /**
         * Procesa una acción del usuario.
         */
        fun onAction(action: HomeAction) {
            when (action) {
                HomeAction.ToggleSeguridad -> {
                    if (_uiState.value.tier.equals("free", ignoreCase = true) && !_uiState.value.isSeguridadUnlocked) {
                        _uiState.update { it.copy(showFreeSecurityDialog = true) }
                    } else {
                        uiStateManager.handleToggle("seguridad")
                    }
                }
                HomeAction.DismissFreeSecurityDialog -> {
                    _uiState.update { it.copy(showFreeSecurityDialog = false) }
                }
                HomeAction.ConfirmFreeSecurityDialog -> {
                    _uiState.update { it.copy(showFreeSecurityDialog = false) }
                    uiStateManager.handleToggle("seguridad")
                }
                HomeAction.BuyVip -> {
                    _uiState.update { it.copy(showFreeSecurityDialog = false, showLotteryInfoDialog = false) }
                    openVipPurchasePortal()
                }
                HomeAction.ShowLotteryInfoDialog -> {
                    _uiState.update { it.copy(showLotteryInfoDialog = true) }
                }
                HomeAction.DismissLotteryInfoDialog -> {
                    _uiState.update { it.copy(showLotteryInfoDialog = false) }
                }
                HomeAction.ToggleIdAndConsole -> uiStateManager.handleToggle("id_console")
                HomeAction.ToggleOpciones -> uiStateManager.handleToggle("opciones")
                HomeAction.ToggleConsoleExpansion -> uiStateManager.handleToggle("console_expansion")
                is HomeAction.OnIdValueChange ->
                    _uiState.update {
                        it.copy(idValue = action.value, isSearchSuccessful = false)
                    }
                HomeAction.ExecuteSearch -> lookupProcessor.execute(uiState.value.idValue)
                HomeAction.SaveId -> handleSaveId()
                is HomeAction.RemoveNotification -> handleRemoveNotification(action.notificationId)
                HomeAction.DismissUpdateAnuncio -> _uiState.update { it.copy(appUpdate = null) }
                HomeAction.DismissInAppNotification -> uiStateManager.dismissInAppNotification()
                is HomeAction.ShowDownloadArchivo -> uiStateManager.showDownloadArchivo(action.cheatName)
                is HomeAction.ToggleCheat -> uiStateManager.toggleCheat(action.cheatName, action.enable)
                HomeAction.DismissDownloadArchivo -> _uiState.update { it.copy(isDownloadArchivoVisible = false) }
                HomeAction.TogglePanelControlFloating -> uiStateManager.handleToggle("panel_floating")
                HomeAction.DismissPanelControlFloating ->
                    _uiState.update {
                        it.copy(
                            isPanelControlFloatingVisible = false,
                        )
                    }
                HomeAction.ToggleFloatingWidget -> toggleFloatingWidget()
                HomeAction.ShowPremiumSurprise -> {
                    // Manejado localmente en HomeOpcionesSection
                }
                HomeAction.DismissGiftDialog -> {
                    _uiState.update { it.copy(showGiftDialog = false) }
                }
                HomeAction.ClaimGift -> {
                    claimGift()
                }
            }
        }

        private fun toggleFloatingWidget() {
            val isActive = !_uiState.value.isFloatingWidgetActive

            if (isActive && !android.provider.Settings.canDrawOverlays(context)) {
                val intent =
                    Intent(
                        android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        android.net.Uri.parse("package:${context.packageName}"),
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                context.startActivity(intent)
                return
            }

            _uiState.update { it.copy(isFloatingWidgetActive = isActive) }
            val intent = Intent(context, com.luxury.cheats.services.floating.logic.FloatingControlService::class.java)
            if (isActive) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } else {
                context.stopService(intent)
            }
        }

        private fun handleSaveId() {
            val currentId = _uiState.value.idValue
            if (currentId.isNotEmpty()) {
                preferencesService.accessSearchData(id = currentId)
                addNotification("ID GUARDADO: $currentId", NotificationType.SUCCESS)
            }
        }

        private fun handleRemoveNotification(notificationId: Long) {
            _uiState.update { it.copy(notifications = it.notifications.filter { notif -> notif.id != notificationId }) }
        }

        private val uiStateManager = UIStateManager()

        private inner class UIStateManager {
            fun handleToggle(type: String) {
                val currentState = _uiState.value
                when (type) {
                    "seguridad" -> {
                        if (!currentState.isSeguridadUnlocked) {
                            addNotification("SEGURIDAD ACTIVADA", NotificationType.SUCCESS)
                            sendPushNotification("SEGURIDAD ACTIVADA", "La protección de archivos está lista.")
                            startSecurityService()
                        }
                    }
                    "id_console" -> {
                        if (!currentState.isSeguridadUnlocked) {
                            addNotification("DEBE DESBLOQUEAR SEGURIDAD PRIMERO", NotificationType.WARNING)
                            return
                        }
                    }
                    "opciones" -> {
                        if (!currentState.isIdAndConsoleVisible) {
                            addNotification("DEBE ABRIR EL PANEL DE ID PRIMERO", NotificationType.WARNING)
                            return
                        }
                        if (currentState.idValue.isEmpty()) {
                            addNotification(
                                message = "DEBE INGRESAR UN ID PRIMERO PARA ACTIVAR LAS OPCIONES",
                                type = NotificationType.WARNING,
                            )
                            return
                        }
                    }
                }
                updateStateForToggle(type)
            }

            private fun updateStateForToggle(type: String) {
                _uiState.update { state ->
                    when (type) {
                        "seguridad" -> if (!state.isSeguridadUnlocked) state.copy(isSeguridadUnlocked = true) else state
                        "id_console" -> state.copy(isIdAndConsoleVisible = !state.isIdAndConsoleVisible)
                        "opciones" -> {
                            if (!state.isOpcionesVisible) loadFileWeights()
                            state.copy(isOpcionesVisible = !state.isOpcionesVisible)
                        }
                        "console_expansion" -> state.copy(isConsoleExpanded = !state.isConsoleExpanded)
                        "panel_floating" ->
                            state.copy(
                                isPanelControlFloatingVisible = !state.isPanelControlFloatingVisible,
                            )
                        else -> state
                    }
                }
            }

            fun showDownloadArchivo(cheatName: String) {
                val weight = _uiState.value.fileWeightsCache[cheatName] ?: ""
                _uiState.update {
                    it.copy(
                        isDownloadArchivoVisible = true,
                        downloadingFileName = cheatName,
                        downloadingFileWeight = weight,
                    )
                }
            }

            fun toggleCheat(
                name: String,
                enable: Boolean,
            ) {
                _uiState.update { it.copy(cheatOptions = it.cheatOptions + (name to enable)) }
                if (enable) showDownloadArchivo(name)
            }

            fun dismissInAppNotification() {
                val current = _uiState.value.currentInAppNotification
                if (current?.frequency == "once") preferencesService.accessSeenNotifications(current.id)
                _uiState.update { it.copy(currentInAppNotification = null) }
            }
        }

        private val lookupProcessor = PlayerLookupProcessor()

        private inner class PlayerLookupProcessor {
            fun execute(uid: String) {
                if (uid.isEmpty() || _uiState.value.isLoadingPlayer) return

                _uiState.update {
                    it.copy(
                        isLoadingPlayer = true,
                        consoleOutput = "INICIANDO PROTOCOLO DE BÚSQUEDA...",
                        isSearchSuccessful = false,
                    )
                }

                viewModelScope.launch {
                    try {
                        val foundData = performSearch(uid)
                        val (success, finalOutput) = processResult(foundData)

                        _uiState.update {
                            it.copy(consoleOutput = finalOutput, isSearchSuccessful = success, isLoadingPlayer = false)
                        }

                        finalize(success, finalOutput)
                    } catch (e: java.io.IOException) {
                        handleError(e, isNetwork = true)
                    } catch (e: IllegalStateException) {
                        handleError(e, isNetwork = false)
                    }
                }
            }

            private suspend fun performSearch(uid: String): com.luxury.cheats.services.freefireapi.PlayerResponse? {
                return try {
                    playerRepository.searchAcrossRegions(uid) { msg ->
                        _uiState.update { it.copy(consoleOutput = it.consoleOutput + msg) }
                    }
                } catch (e: java.io.IOException) {
                    android.util.Log.e("HomeViewModel", "Network error during search", e)
                    null
                }
            }

            private fun processResult(
                foundData: com.luxury.cheats.services.freefireapi.PlayerResponse?,
            ): Pair<Boolean, String> {
                val success = foundData != null
                val output =
                    if (success) {
                        try {
                            val formatted = HomeGreetingProvider.formatPlayerData(foundData!!)
                            _uiState.value.consoleOutput + "\n\n" + formatted
                        } catch (e: IllegalStateException) {
                            android.util.Log.e("HomeViewModel", "Formatting error", e)
                            _uiState.value.consoleOutput + "\n\n[ERROR FORMATEO]: ${e.message}"
                        }
                    } else {
                        _uiState.value.consoleOutput + "\n\nPROTOCOLO FINALIZADO: SIN RESULTADOS."
                    }
                return success to output
            }

            private suspend fun finalize(
                success: Boolean,
                finalOutput: String,
            ) {
                val (msg, type) =
                    if (success) {
                        "OBJETIVO LOCALIZADO EXITOSAMENTE" to NotificationType.SUCCESS
                    } else {
                        "SUJETO NO ENCONTRADO" to NotificationType.ERROR
                    }
                addNotification(msg, type)

                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    preferencesService.accessSearchData(results = finalOutput to success)
                }
            }

            private fun handleError(
                e: Exception,
                isNetwork: Boolean,
            ) {
                android.util.Log.e("HomeViewModel", "Fatal lookup error", e)
                val type = if (isNetwork) "RED" else "SISTEMA"
                _uiState.update {
                    it.copy(
                        consoleOutput = it.consoleOutput + "\n\n[ERROR $type]: ${e.message}",
                        isLoadingPlayer = false,
                    )
                }
                addNotification("FALLO EN EL PROTOCOLO", NotificationType.ERROR)
            }
        }

        /**
         * Agrega una notificación y la elimina automáticamente después de 3 segundos.
         * Manejo arquitectural correcto: la UI solo renderiza, el ViewModel maneja la lógica temporal.
         */
        private fun addNotification(
            message: String,
            type: NotificationType,
        ) {
            val notification = AppNotification(message = message, type = type)

            _uiState.update {
                it.copy(notifications = it.notifications + notification)
            }

            viewModelScope.launch {
                kotlinx.coroutines.delay(NOTIFICATION_AUTO_DISMISS_DELAY)
                _uiState.update { state ->
                    state.copy(
                        notifications =
                            state.notifications.filterNot { n ->
                                n.id == notification.id
                            },
                    )
                }
            }
        }

        /**
         * Envía una notificación push real del sistema (Android Notification).
         */
        private fun sendPushNotification(
            title: String,
            message: String,
        ) {
            val notificationManager =
                context.getSystemService(
                    android.content.Context.NOTIFICATION_SERVICE,
                ) as android.app.NotificationManager
            val channelId = "security_push_channel"

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel =
                    android.app.NotificationChannel(
                        channelId,
                        "Alertas de Seguridad",
                        android.app.NotificationManager.IMPORTANCE_HIGH,
                    )
                notificationManager.createNotificationChannel(channel)
            }

            val builder =
                androidx.core.app.NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)

            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        }

        private fun startSecurityService() {
            val dpm =
                context.getSystemService(
                    android.content.Context.DEVICE_POLICY_SERVICE,
                ) as android.app.admin.DevicePolicyManager
            if (!dpm.isAdminActive(adminComponent)) {
                addNotification("ADMIN REQUERIDO PARA PROTECCIÓN TOTAL", NotificationType.WARNING)
                // Aún así iniciamos el servicio, pero avisamos
            }

            val intent = Intent(context, com.luxury.cheats.services.security.SecurityService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        private fun openVipPurchasePortal() {
            try {
                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://luxurycheats.pages.dev/")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                addNotification("ERROR AL ABRIR EL PORTAL", NotificationType.ERROR)
            }
        }

        /**
         * Constantes de configuración para el HomeViewModel.
         */
        companion object {
            private const val NOTIFICATION_AUTO_DISMISS_DELAY = 3000L
        }
    }
