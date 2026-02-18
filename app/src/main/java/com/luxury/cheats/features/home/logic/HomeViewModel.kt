package com.luxury.cheats.features.home.logic

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luxury.cheats.core.ui.AppNotification
import com.luxury.cheats.core.ui.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

            _uiState.update {
                it.copy(
                    isSeguridadUnlocked = false,
                    // Protocolo: Siempre cerrado al iniciar app
                    isIdAndConsoleVisible = false,
                    isOpcionesVisible = false,
                    consoleOutput = searchResults.first,
                    isSearchSuccessful = searchResults.second,
                    cheatOptions = emptyMap(),
                    idValue = preferencesService.accessSearchData().first.ifEmpty { it.idValue },
                )
            }
        }

        private fun initializeData() {
            updateGreeting()
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                checkUpdates()
                loadInAppNotifications()
            }
        }

        private suspend fun checkUpdates() {
            try {
                val update = updateService.getAppUpdate()
                if (update.active) {
                    _uiState.update { it.copy(appUpdate = update) }
                }
            } catch (e: java.io.IOException) {
                android.util.Log.w("HomeViewModel", "Failed to check updates", e)
            }
        }

        private suspend fun loadInAppNotifications() {
            try {
                val all = notificationService.getActiveNotifications()
                val seen = preferencesService.accessSeenNotifications()
                val toShow = all.firstOrNull { it.frequency != "once" || !seen.contains(it.id) }
                if (toShow != null) {
                    _uiState.update { it.copy(currentInAppNotification = toShow) }
                }
            } catch (e: java.io.IOException) {
                android.util.Log.w("HomeViewModel", "Failed to load notifications", e)
            }
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
                                if (info.url.isNotEmpty()) {
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

        /**
         * Procesa una acción del usuario.
         */
        fun onAction(action: HomeAction) {
            when (action) {
                HomeAction.ToggleSeguridad -> uiStateManager.handleToggle("seguridad")
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
                            addNotification("DEBE INGRESAR UN ID PRIMERO PARA ACTIVAR LAS OPCIONES", NotificationType.WARNING)
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

        /**
         * Constantes de configuración para HomeViewModel.
         */
        companion object {
            private const val NOTIFICATION_AUTO_DISMISS_DELAY = 3000L
        }
    }
