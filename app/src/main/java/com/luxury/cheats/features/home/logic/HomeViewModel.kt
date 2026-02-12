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

/**
 * ViewModel para la pantalla Home
 * Gestiona la visibilidad condicional de las secciones y el estado del usuario.
 */
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel para la pantalla Home
 * Gestiona la visibilidad condicional de las secciones y el estado del usuario.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesService: com.luxury.cheats.services.UserPreferencesService,
    private val playerRepository: com.luxury.cheats.services.PlayerRepository,
    private val updateService: com.luxury.cheats.features.update.service.UpdateService,
    private val notificationService: com.luxury.cheats.features.home.service.InAppNotificationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        initializeData()
        val extendedState = preferencesService.getExtendedHomeState()
        val savedCheats = preferencesService.getCheatOptions()
        
        _uiState.update { 
            it.copy(
                isSeguridadUnlocked = false, // Protocolo: Siempre cerrado al iniciar app
                isIdAndConsoleVisible = false,
                isOpcionesVisible = false,
                consoleOutput = extendedState.first,
                isSearchSuccessful = extendedState.second,
                cheatOptions = savedCheats,
                idValue = preferencesService.accessPlayerId() ?: it.idValue
            ) 
        }
    }

    private fun initializeData() {
        updateGreeting()
        viewModelScope.launch {
            // Check updates
            try {
                val update = updateService.getAppUpdate()
                if (update.active) _uiState.update { it.copy(appUpdate = update) }
            } catch (ignored: Exception) {}

            // Fetch In-App Notifications
            try {
                val all = notificationService.getActiveNotifications()
                val seen = preferencesService.accessSeenNotifications()
                val toShow = all.firstOrNull { it.frequency != "once" || !seen.contains(it.id) }
                if (toShow != null) _uiState.update { it.copy(currentInAppNotification = toShow) }
            } catch (ignored: Exception) {}

            // Preload weights in parallel
            val downloadService = com.luxury.cheats.features.download.service.DownloadService()
            val cheatNames = listOf("Aimbot", "Holograma", "WallHack", "AimFov")
            
            val deferredWeights = cheatNames.map { name ->
                async {
                    try {
                        val info = downloadService.getDownloadInfo(name)
                        if (info.url.isNotEmpty()) {
                            name to downloadService.getFileSize(info.url)
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            
            val weightsMap = deferredWeights.awaitAll()
                .filterNotNull()
                .toMap()
                
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
            HomeAction.ToggleControlPanel -> handleToggle("control_panel")
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
            HomeAction.RequestFloatingPanel -> handleToggle("control_panel")
        }
    }

    private fun handleToggleCheat(cheatName: String, enable: Boolean) {
        _uiState.update { 
            it.copy(cheatOptions = it.cheatOptions + (cheatName to enable)) 
        }.also { 
            preferencesService.saveCheatOptions(_uiState.value.cheatOptions)
        }
        if (enable) {
            handleShowDownloadArchivo(cheatName)
        }
    }

    private fun handleToggle(type: String) {
        _uiState.update { state ->
            when (type) {
                "seguridad" -> {
                    val newUnlocked = !state.isSeguridadUnlocked
                    state.copy(
                        isSeguridadUnlocked = newUnlocked,
                        isIdAndConsoleVisible = if (!newUnlocked) false else state.isIdAndConsoleVisible,
                        isOpcionesVisible = if (!newUnlocked) false else state.isOpcionesVisible
                    )
                }
                "id_console" -> {
                    if (!state.isSeguridadUnlocked) {
                        addNotification("DEBE DESBLOQUEAR SEGURIDAD PRIMERO", NotificationType.WARNING)
                        state
                    } else state.copy(isIdAndConsoleVisible = !state.isIdAndConsoleVisible)
                }
                "opciones" -> {
                    if (state.isIdAndConsoleVisible && state.isSearchSuccessful) {
                        state.copy(isOpcionesVisible = !state.isOpcionesVisible)
                    } else {
                        val msg = if (!state.isIdAndConsoleVisible) "DEBE ABRIR EL PANEL DE ID PRIMERO" 
                                 else "DEBE ENCONTRAR UN OBJETIVO ANTES DE ACTIVAR"
                        addNotification(msg, NotificationType.WARNING)
                        state
                    }
                }
                "control_panel" -> {
                    if (state.isOpcionesVisible) {
                        // El lanzamiento del servicio se manejará en la UI (HomeScreen)
                        // vía una acción que la UI observe o simplemente permitiendo que el estado cambie
                        // para que HomeScreen reaccione.
                        state.copy(isFloatingServiceRunning = !state.isFloatingServiceRunning)
                    } else {
                        addNotification("DEBE ACTIVAR EL PANEL DE OPCIONES PRIMERO", NotificationType.WARNING)
                        state
                    }
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
        if (uid.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { 
                it.copy(
                    isLoadingPlayer = true, 
                    consoleOutput = "INICIANDO PROTOCOLO DE BÚSQUEDA...", 
                    isSearchSuccessful = false
                ) 
            }
            val foundData = playerRepository.searchAcrossRegions(uid) { msg ->
                _uiState.update { 
                    val newOutput = it.consoleOutput + msg
                    it.copy(consoleOutput = newOutput).also {
                        preferencesService.saveExtendedHomeState(newOutput, it.isSearchSuccessful)
                    }
                }
            }
            
            _uiState.update { currentState ->
                val (finalOutput, isSuccessful) = if (foundData != null) {
                    HomeGreetingProvider.formatPlayerData(foundData) to true
                } else {
                    (currentState.consoleOutput + "\n\nPROTOCOLO FINALIZADO: SIN RESULTADOS.") to false
                }
                
                val notification = if (isSuccessful) {
                    AppNotification("OBJETIVO LOCALIZADO EXITOSAMENTE", NotificationType.SUCCESS)
                } else {
                    AppNotification("SUJETO NO ENCONTRADO EN NINGUNA REGIÓN", NotificationType.ERROR)
                }

                currentState.copy(
                    consoleOutput = finalOutput,
                    isSearchSuccessful = isSuccessful,
                    notifications = currentState.notifications + notification,
                    isLoadingPlayer = false
                ).also { 
                    preferencesService.saveExtendedHomeState(it.consoleOutput, it.isSearchSuccessful)
                }
            }
        }
    }

    private fun addNotification(message: String, type: NotificationType) {
        _uiState.update { it.copy(notifications = it.notifications + AppNotification(message = message, type = type)) }
    }
}
