package com.luxury.cheats.features.home.logic

import androidx.lifecycle.ViewModel
import com.luxury.cheats.core.ui.AppNotification
import com.luxury.cheats.core.ui.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import androidx.lifecycle.viewModelScope
import android.content.Intent

/**
 * ViewModel para la pantalla Home
 * Gestiona la visibilidad condicional de las secciones y el estado del usuario.
 */
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * ViewModel para la pantalla Home
 * Gestiona la visibilidad condicional de las secciones y el estado del usuario.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesService: com.luxury.cheats.services.storage.UserPreferencesService,
    private val playerRepository: com.luxury.cheats.services.freefireapi.PlayerRepository,
    private val updateService: com.luxury.cheats.features.update.service.UpdateService,
    private val notificationService: com.luxury.cheats.features.home.service.InAppNotificationService,
    @ApplicationContext private val context: android.content.Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        initializeData()
        val extendedState = preferencesService.getExtendedHomeState()
        
        _uiState.update { 
            it.copy(
                isSeguridadUnlocked = false, // Protocolo: Siempre cerrado al iniciar app
                isIdAndConsoleVisible = false,
                isOpcionesVisible = false,
                consoleOutput = extendedState.first,
                isSearchSuccessful = extendedState.second,
                cheatOptions = emptyMap(),
                idValue = preferencesService.accessPlayerId() ?: it.idValue
            ) 
        }
    }

    private fun initializeData() {
        updateGreeting()
        viewModelScope.launch {
            // Check updates & Notifications (IO operations)
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val update = updateService.getAppUpdate()
                    if (update.active) {
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            _uiState.update { it.copy(appUpdate = update) }
                        }
                    }
                } catch (ignored: Exception) {}

                try {
                    val all = notificationService.getActiveNotifications()
                    val seen = preferencesService.accessSeenNotifications()
                    val toShow = all.firstOrNull { it.frequency != "once" || !seen.contains(it.id) }
                    if (toShow != null) {
                        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                            _uiState.update { it.copy(currentInAppNotification = toShow) }
                        }
                    }
                } catch (ignored: Exception) {}
            }
        }
    }

    /**
     * Carga los pesos de los archivos solo cuando sea necesario (on-demand).
     */
    fun loadFileWeights() {
        if (_uiState.value.fileWeightsCache.isNotEmpty()) return
        
        viewModelScope.launch {
            val weightsMap = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                val downloadService = com.luxury.cheats.features.download.service.DownloadService()
                val cheatNames = listOf("Aimbot", "Holograma", "WallHack", "AimFov")
                val map = mutableMapOf<String, String>()
                
                for (name in cheatNames) {
                    try {
                        val info = downloadService.getDownloadInfo(name)
                        if (info.url.isNotEmpty()) {
                            map[name] = downloadService.getFileSize(info.url)
                        }
                    } catch (ignored: Exception) {}
                }
                map
            }
            
            _uiState.update { it.copy(fileWeightsCache = weightsMap) }
        }
    }


    private fun updateGreeting() {
        val username = preferencesService.getCredentials()?.first ?: ""
        val now = java.time.LocalDateTime.now()
        val (greeting, subtitle) = HomeGreetingProvider.getGreetingForTimeAndDate(
            now.hour, now.monthValue, now.dayOfMonth
        )
        _uiState.update { it.copy(userName = username, greeting = greeting, greetingSubtitle = subtitle) }
    }

    /**
     * Procesa una acción del usuario.
     */
    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.ToggleSeguridad -> handleToggle("seguridad")
            HomeAction.ToggleIdAndConsole -> handleToggle("id_console")
            HomeAction.ToggleOpciones -> handleToggle("opciones")

            HomeAction.ToggleConsoleExpansion -> handleToggle("console_expansion")
            is HomeAction.OnIdValueChange -> _uiState.update { 
                it.copy(idValue = action.value, isSearchSuccessful = false) 
            }
            HomeAction.ExecuteSearch -> executePlayerLookup()
            HomeAction.SaveId -> handleSaveId()
            is HomeAction.RemoveNotification -> handleRemoveNotification(action.notificationId)
            HomeAction.DismissUpdateAnuncio -> _uiState.update { it.copy(appUpdate = null) }
            HomeAction.DismissInAppNotification -> handleDismissInAppNotification()
            is HomeAction.ShowDownloadArchivo -> handleShowDownloadArchivo(action.cheatName)
            is HomeAction.ToggleCheat -> handleToggleCheat(action.cheatName, action.enable)
            HomeAction.DismissDownloadArchivo -> _uiState.update { it.copy(isDownloadArchivoVisible = false) }

        }
    }

    private fun handleToggleCheat(cheatName: String, enable: Boolean) {
        _uiState.update { 
            it.copy(cheatOptions = it.cheatOptions + (cheatName to enable)) 
        }
        if (enable) {
            handleShowDownloadArchivo(cheatName)
        }
    }

    private fun handleToggle(type: String) {
        val currentState = _uiState.value
        
        when (type) {
            "seguridad" -> {
                // Solo activar si no está ya activado
                if (!currentState.isSeguridadUnlocked) {
                    addNotification("SEGURIDAD ACTIVADA", NotificationType.SUCCESS)
                    sendPushNotification("SEGURIDAD ACTIVADA", "La protección de archivos está lista.")
                }
                // Si ya está activado, no hacer nada (one-way activation)
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
            }

        }

        // Actualización de estado pura
        updateStateForToggle(type)
    }

    private fun updateStateForToggle(type: String) {
        _uiState.update { state ->
            when (type) {
                "seguridad" -> {
                    // One-way activation: solo se puede activar, nunca desactivar
                    if (!state.isSeguridadUnlocked) {
                        state.copy(isSeguridadUnlocked = true)
                    } else {
                        // Ya está activado, no hacer nada
                        state
                    }
                }
                "id_console" -> state.copy(isIdAndConsoleVisible = !state.isIdAndConsoleVisible)
                "opciones" -> {
                    // Cargar pesos de archivos al activar opciones
                    if (!state.isOpcionesVisible) loadFileWeights()
                    state.copy(isOpcionesVisible = !state.isOpcionesVisible)
                }

                "console_expansion" -> state.copy(isConsoleExpanded = !state.isConsoleExpanded)
                else -> state
            }
        }
    }

    private fun handleShowDownloadArchivo(cheatName: String) {
        val weight = _uiState.value.fileWeightsCache[cheatName] ?: ""
        _uiState.update { 
            it.copy(
                isDownloadArchivoVisible = true, 
                downloadingFileName = cheatName, 
                downloadingFileWeight = weight
            ) 
        }
    }

    private fun handleDismissInAppNotification() {
        val current = _uiState.value.currentInAppNotification
        if (current?.frequency == "once") preferencesService.accessSeenNotifications(current.id)
        _uiState.update { it.copy(currentInAppNotification = null) }
    }

    private fun handleSaveId() {
        val currentId = _uiState.value.idValue
        if (currentId.isNotEmpty()) {
            preferencesService.accessPlayerId(currentId)
            addNotification("ID GUARDADO: $currentId", NotificationType.SUCCESS)
        }
    }

    private fun handleRemoveNotification(notificationId: Long) {
        _uiState.update { it.copy(notifications = it.notifications.filter { notif -> notif.id != notificationId }) }
    }

    private fun executePlayerLookup() {
        val uid = _uiState.value.idValue
        if (uid.isEmpty() || _uiState.value.isLoadingPlayer) return
        
        viewModelScope.launch {
            try {
                // 1. UI Feedback inmediato
                _uiState.update { 
                    it.copy(
                        isLoadingPlayer = true, 
                        consoleOutput = "INICIANDO PROTOCOLO DE BÚSQUEDA...", 
                        isSearchSuccessful = false
                    ) 
                }
                
                // 2. Operación de búsqueda (corre en IO internamente en el repo)
                val foundData = try {
                    playerRepository.searchAcrossRegions(uid) { msg ->
                        // Actualizar consola incrementalmente
                        _uiState.update { currentState ->
                            currentState.copy(consoleOutput = currentState.consoleOutput + msg)
                        }
                    }
                } catch (e: Exception) {
                    null
                }
                
                // 3. Publicación de resultados finales
                val success = foundData != null
                val finalOutput = if (success) {
                    try {
                        val formatted = HomeGreetingProvider.formatPlayerData(foundData!!)
                        _uiState.value.consoleOutput + "\n\n" + formatted
                    } catch (e: Exception) {
                        _uiState.value.consoleOutput + "\n\n[ERROR FORMATEO]: ${e.message}"
                    }
                } else {
                    _uiState.value.consoleOutput + "\n\nPROTOCOLO FINALIZADO: SIN RESULTADOS."
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        consoleOutput = finalOutput,
                        isSearchSuccessful = success,
                        isLoadingPlayer = false
                    )
                }

                // Notificar al usuario
                val notificationType = if (success) NotificationType.SUCCESS else NotificationType.ERROR
                val message = if (success) "OBJETIVO LOCALIZADO EXITOSAMENTE" else "SUJETO NO ENCONTRADO"
                addNotification(message, notificationType)

                // 4. Guardado de estado en background para evitar ANRs
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    preferencesService.saveExtendedHomeState(finalOutput, success)
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(
                    consoleOutput = it.consoleOutput + "\n\n[ERROR FATAL]: ${e.message}",
                    isLoadingPlayer = false
                )}
                addNotification("FALLO EN EL PROTOCOLO", NotificationType.ERROR)
            }
        }
    }


    /**
     * Agrega una notificación y la elimina automáticamente después de 3 segundos.
     * Manejo arquitectural correcto: la UI solo renderiza, el ViewModel maneja la lógica temporal.
     */
    private fun addNotification(message: String, type: NotificationType) {
        val notification = AppNotification(message = message, type = type)
        
        _uiState.update { 
            it.copy(notifications = it.notifications + notification) 
        }
        
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            _uiState.update { state ->
                state.copy(
                    notifications = state.notifications.filterNot { n -> 
                        n.id == notification.id 
                    }
                )
            }
        }
    }
    /**
     * Envía una notificación push real del sistema (Android Notification).
     */
    private fun sendPushNotification(title: String, message: String) {
        val notificationManager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val channelId = "security_push_channel"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                channelId,
                "Alertas de Seguridad",
                android.app.NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
