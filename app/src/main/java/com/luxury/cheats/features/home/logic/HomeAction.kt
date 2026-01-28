package com.luxury.cheats.features.home.logic

/**
 * Acciones/Eventos del usuario en la pantalla Home
 */
sealed class HomeAction {
    /** Acción para mostrar la sección de ID y Consola. */
    object ToggleIdAndConsole : HomeAction()
    /** Acción para mostrar la sección de Opciones (Trampas). */
    object ToggleOpciones : HomeAction()
    /** Acción para cambiar el valor del ID. */
    data class OnIdValueChange(val value: String) : HomeAction()
    /** Acción para ejecutar la búsqueda del jugador. */
    object ExecuteSearch : HomeAction()
    /** Acción para expandir/colapsar la consola. */
    object ToggleConsoleExpansion : HomeAction()
    /** Acción para guardar el ID en preferencias. */
    object SaveId : HomeAction()
    /** Acción para remover una notificación. */
    data class RemoveNotification(val notificationId: Long) : HomeAction()
    /** Acción para desbloquear las secciones (al presionar Seguridad). */
    object ToggleSeguridad : HomeAction()
    /** Acción para cerrar el anuncio de actualización. */
    object DismissUpdateAnuncio : HomeAction()
    /** Acción para cerrar la notificación in-app. */
    object DismissInAppNotification : HomeAction()
}
