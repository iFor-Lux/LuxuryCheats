package com.luxury.cheats.features.home.logic

import androidx.lifecycle.ViewModel
import com.luxury.cheats.core.ui.AppNotification
import com.luxury.cheats.core.ui.NotificationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

/**
 * ViewModel para la pantalla Home
 * Gestiona la visibilidad condicional de las secciones y el estado del usuario.
 */
class HomeViewModel(
    private val preferencesService: com.luxury.cheats.services.UserPreferencesService,
    private val ffApiService: com.luxury.cheats.services.FreeFireApiService = 
        com.luxury.cheats.services.FreeFireApiService.create(),
    private val updateService: com.luxury.cheats.features.update.service.UpdateService = 
        com.luxury.cheats.features.update.service.UpdateService(),
    private val notificationService: com.luxury.cheats.features.home.service.InAppNotificationService =
        com.luxury.cheats.features.home.service.InAppNotificationService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private object HomeConstants {
        const val NEW_YEAR_MONTH = 1
        const val NEW_YEAR_DAY = 1
        const val YEAR_END_MONTH = 12
        const val YEAR_END_DAY = 31
        const val VALENTINE_MONTH = 2
        const val VALENTINE_DAY = 14
        const val HALLOWEEN_MONTH = 10
        const val HALLOWEEN_START = 25
        const val HALLOWEEN_END = 31
        const val XMAS_MONTH = 12
        const val XMAS_START = 20
        const val XMAS_END = 30
        
        const val MORNING_START = 6
        const val MORNING_END = 11
        const val AFTERNOON_START = 12
        const val AFTERNOON_END = 18
    }

    init {
        updateGreeting()
        checkForUpdates()
        fetchInAppNotifications()
        val savedId = preferencesService.accessPlayerId()
        _uiState.update { 
            it.copy(
                isSeguridadUnlocked = false,
                isIdAndConsoleVisible = false,
                isOpcionesVisible = false,
                idValue = savedId ?: it.idValue
            ) 
        }
    }

    private fun checkForUpdates() {
        viewModelScope.launch {
            try {
                val update = updateService.getAppUpdate()
                if (update.active) {
                    _uiState.update { it.copy(appUpdate = update) }
                }
            } catch (ignored: Exception) {
                // Silently ignore update check errors for Home
            }
        }
    }

    private fun fetchInAppNotifications() {
        viewModelScope.launch {
            try {
                val allNotifications = notificationService.getActiveNotifications()
                val seenNotifications = preferencesService.getSeenNotifications() // Necesito crear este método
                
                // Buscamos una notificación que deba mostrarse
                val toShow = allNotifications.firstOrNull { notif ->
                    if (notif.frequency == "once") {
                        !seenNotifications.contains(notif.id)
                    } else {
                        true // "always" o similar
                    }
                }
                
                if (toShow != null) {
                    _uiState.update { it.copy(currentInAppNotification = toShow) }
                }
            } catch (ignored: Exception) {
                // Silently ignore notification errors
            }
        }
    }

    private fun updateGreeting() {
        val savedCredentials = preferencesService.getCredentials()
        val username = savedCredentials?.first ?: ""

        val now = java.time.LocalDateTime.now()
        val hour = now.hour
        val month = now.monthValue
        val day = now.dayOfMonth

        val (greeting, subtitle) = HomeHelper.getGreetingForTimeAndDate(hour, month, day)

        _uiState.update {
            it.copy(
                userName = username,
                greeting = greeting,
                greetingSubtitle = subtitle
            )
        }
    }

    /**
     * Helper para la lógica de saludos, eventos especiales y formateo de datos.
     */
    private object HomeHelper {
        fun getGreetingForTimeAndDate(hour: Int, month: Int, day: Int): Pair<String, String> {
            return when {
                month == HomeConstants.NEW_YEAR_MONTH && day == HomeConstants.NEW_YEAR_DAY || 
                month == HomeConstants.YEAR_END_MONTH && day == HomeConstants.YEAR_END_DAY ->
                    "FELIZ AÑO NUEVO" to "¡Qué este año 2027 esté lleno de victorias incomparables!"
                month == HomeConstants.VALENTINE_MONTH && day == HomeConstants.VALENTINE_DAY ->
                    "FELIZ SAN VALENTÍN" to "¡Hoy es un gran día para compartir victorias con alguien especial!"
                month == HomeConstants.HALLOWEEN_MONTH && 
                        day in HomeConstants.HALLOWEEN_START..HomeConstants.HALLOWEEN_END ->
                    "¡FELIZ HALLOWEEN!" to "¡Una noche de trucos, tratos y muchas victorias de miedo!"
                month == HomeConstants.XMAS_MONTH && day in HomeConstants.XMAS_START..HomeConstants.XMAS_END ->
                    "FELIZ NAVIDAD" to "¡Te deseamos una muy feliz navidad y feliz juego con Luxury!"
                else -> getGreetingByHour(hour)
            }
        }

        private fun getGreetingByHour(hour: Int): Pair<String, String> {
            return when (hour) {
                in HomeConstants.MORNING_START..HomeConstants.MORNING_END -> 
                    "BUENOS DIAS" to "Gran día para empezar a jugar y divertirse todo el día"
                in HomeConstants.AFTERNOON_START..HomeConstants.AFTERNOON_END -> 
                    "BUENAS TARDES" to "Tarde perfecta para unas partidas legendarias"
                else -> "BUENAS NOCHES" to "La noche es joven para seguir ganando en cada partida"
            }
        }

        fun formatPlayerData(data: com.luxury.cheats.services.PlayerResponse): String {
            val b = data.basicInfo ?: return "ERROR EN PROTOCOLO DE DATOS."
            val s = data.socialInfo
            val c = data.clanInfo

            val sb = StringBuilder()
            sb.append("🔥 SEGURIDAD LUXURY ACTIVADO 🔥\n")
            sb.append("---------------------------------\n")
            sb.append("👤 PERFIL\n")
            sb.append("• UID      : ${b.accountId}\n")
            sb.append("• NICKNAME : ${b.nickname}\n")
            sb.append("• REGIÓN   : ${b.region}\n")
            sb.append("• NIVEL    : ${b.level} (EXP: ${b.exp?.let { 
                String.format(java.util.Locale.getDefault(), "%,d", it) 
            }})\n")
            sb.append("• LIKES    : ${b.liked ?: 0}\n\n")

            sb.append("---------------------------------\n\n")

            sb.append("🏆 RANGO\n")
            sb.append("• BR RANK  : ${b.rank} (${b.rankingPoints} pts)\n")
            sb.append("• CS RANK  : ${b.csRank} (${b.csRankingPoints} pts)\n")
            sb.append("• MAX RANK : BR: ${b.brMaxRank} | CS: ${b.csMaxRank}\n\n")

            sb.append("---------------------------------\n\n")

            if (c != null && c.clanName != null) {
                sb.append("🛡️ CLAN\n")
                sb.append("• NOMBRE   : ${c.clanName}\n")
                sb.append("• NIVEL    : ${c.clanLevel}\n")
                sb.append("• MIEMBROS : ${c.memberNum}/${c.capacity}\n\n")

                sb.append("---------------------------------\n\n")
            }

            if (s != null) {
                sb.append("🌐 SOCIAL\n")
                sb.append("• FIRMA    : ${s.signature ?: "Sin firma"}\n")
            }

            return sb.toString()
        }
    }

    /**
     * Añade una notificación toast al estado
     */
    private fun addNotification(message: String, type: NotificationType) {
        val notification = AppNotification(
            message = message,
            type = type
        )
        _uiState.update { 
            it.copy(notifications = it.notifications + notification) 
        }
    }

    /**
     * Procesa una acción del usuario y actualiza el estado de la UI.
     *
     * @param action La acción a ejecutar.
     */
    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.ToggleSeguridad -> handleToggleSeguridad()
            HomeAction.ToggleIdAndConsole -> handleToggleIdAndConsole()
            HomeAction.ToggleOpciones -> handleToggleOpciones()
            is HomeAction.OnIdValueChange -> handleIdValueChange(action.value)
            HomeAction.ExecuteSearch -> executePlayerLookup()
            HomeAction.ToggleConsoleExpansion -> handleToggleConsoleExpansion()
            HomeAction.SaveId -> handleSaveId()
            is HomeAction.RemoveNotification -> handleRemoveNotification(action.notificationId)
            HomeAction.DismissUpdateAnuncio -> handleDismissUpdateAnuncio()
            HomeAction.DismissInAppNotification -> handleDismissInAppNotification()
        }
    }

    private fun handleDismissUpdateAnuncio() {
        _uiState.update { it.copy(appUpdate = null) }
    }

    private fun handleDismissInAppNotification() {
        val current = _uiState.value.currentInAppNotification
        if (current != null && current.frequency == "once") {
            preferencesService.markNotificationAsSeen(current.id)
        }
        _uiState.update { it.copy(currentInAppNotification = null) }
    }

    private fun handleToggleSeguridad() {
        _uiState.update { 
            val newUnlocked = !it.isSeguridadUnlocked
            it.copy(
                isSeguridadUnlocked = newUnlocked,
                isIdAndConsoleVisible = if (!newUnlocked) false else it.isIdAndConsoleVisible,
                isOpcionesVisible = if (!newUnlocked) false else it.isOpcionesVisible
            ) 
        }
        saveCurrentHomeState()
    }

    private fun handleToggleIdAndConsole() {
        val state = _uiState.value
        if (!state.isSeguridadUnlocked) {
            addNotification("DEBE DESBLOQUEAR SEGURIDAD PRIMERO", NotificationType.WARNING)
        } else {
            _uiState.update { it.copy(isIdAndConsoleVisible = !it.isIdAndConsoleVisible) }
            saveCurrentHomeState()
        }
    }

    private fun handleToggleOpciones() {
        val state = _uiState.value
        when {
            state.isIdAndConsoleVisible && state.isSearchSuccessful -> {
                _uiState.update { it.copy(isOpcionesVisible = !it.isOpcionesVisible) }
                saveCurrentHomeState()
            }
            !state.isIdAndConsoleVisible -> {
                addNotification("DEBE ABRIR EL PANEL DE ID PRIMERO", NotificationType.WARNING)
            }
            !state.isSearchSuccessful -> {
                addNotification("DEBE ENCONTRAR UN OBJETIVO ANTES DE ACTIVAR", NotificationType.WARNING)
            }
        }
    }

    private fun handleIdValueChange(value: String) {
        _uiState.update { it.copy(idValue = value, isSearchSuccessful = false) }
    }

    private fun handleToggleConsoleExpansion() {
        _uiState.update { it.copy(isConsoleExpanded = !it.isConsoleExpanded) }
    }

    private fun handleSaveId() {
        val currentId = _uiState.value.idValue
        if (currentId.isNotEmpty()) {
            preferencesService.accessPlayerId(currentId)
            addNotification("ID GUARDADO: $currentId", NotificationType.SUCCESS)
        }
    }

    private fun handleRemoveNotification(notificationId: Long) {
        _uiState.update { 
            it.copy(notifications = it.notifications.filter { notif -> notif.id != notificationId }) 
        }
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
            
            val found = searchPlayerAcrossRegions(uid)
            handleLookupResult(found)
            
            _uiState.update { it.copy(isLoadingPlayer = false) }
        }
    }

    private suspend fun searchPlayerAcrossRegions(uid: String): Boolean {
        val servers = listOf("us", "br", "ind", "sg", "id")
        for (server in servers) {
            val regionMsg = "\nRASTREANDO REGIÓN: ${server.uppercase()}..."
            _uiState.update { it.copy(consoleOutput = it.consoleOutput + regionMsg) }
            
            try {
                val response = ffApiService.getPlayerInfo(server, uid)
                if (response.isSuccessful && response.body()?.basicInfo != null) {
                    val data = response.body()!!
                    _uiState.update { 
                        it.copy(
                            consoleOutput = HomeHelper.formatPlayerData(data),
                            isSearchSuccessful = true 
                        ) 
                    }
                    return true
                } else if (!response.isSuccessful) {
                     val failMsg = "\n❌ RESPUESTA FALLIDA: ${response.code()}"
                     _uiState.update { it.copy(consoleOutput = it.consoleOutput + failMsg) }
                }
            } catch (e: java.io.IOException) {
                _uiState.update { it.copy(consoleOutput = it.consoleOutput + "\n⚠️ ERROR DE RED: ${e.message}") }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                val errorMsg = e.localizedMessage ?: e.message ?: "Error desconocido"
                _uiState.update { it.copy(consoleOutput = it.consoleOutput + "\n⚠️ ERROR: $errorMsg") }
            }
        }
        return false
    }

    private fun handleLookupResult(found: Boolean) {
        if (!found) {
            val finalMsg = "\n\nPROTOCOLO FINALIZADO: SIN RESULTADOS."
            _uiState.update { it.copy(consoleOutput = it.consoleOutput + finalMsg, isSearchSuccessful = false) }
            addNotification("SUJETO NO ENCONTRADO EN NINGUNA REGIÓN", NotificationType.ERROR)
        } else {
            addNotification("OBJETIVO LOCALIZADO EXITOSAMENTE", NotificationType.SUCCESS)
        }
    }

    private fun saveCurrentHomeState() {
        val state = _uiState.value
        preferencesService.accessHomeState(
            Triple(state.isSeguridadUnlocked, state.isIdAndConsoleVisible, state.isOpcionesVisible)
        )
    }
}
