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

    /** Acción para mostrar el bottom sheet de descarga. */
    data class ShowDownloadArchivo(val cheatName: String) : HomeAction()

    /** Acción para alternar el estado de una trampa. */
    data class ToggleCheat(val cheatName: String, val enable: Boolean) : HomeAction()

    /** Acción para cerrar el bottom sheet de descarga. */
    object DismissDownloadArchivo : HomeAction()

    /** Acción para alternar el panel flotante de control. */
    object TogglePanelControlFloating : HomeAction()

    /** Acción para cerrar el panel flotante de control. */
    object DismissPanelControlFloating : HomeAction()

    /** Acción para alternar el widget flotante. */
    object ToggleFloatingWidget : HomeAction()

    /** Acción para cerrar el diálogo persuasivo de seguridad. */
    object DismissFreeSecurityDialog : HomeAction()

    /** Acción para aceptar la seguridad limitada y continuar. */
    object ConfirmFreeSecurityDialog : HomeAction()

    /** Acción para comprar la versión VIP. */
    object BuyVip : HomeAction()

    /** Acción para mostrar la información del sorteo falso. */
    object ShowLotteryInfoDialog : HomeAction()

    /** Acción para ocultar la información del sorteo falso. */
    object DismissLotteryInfoDialog : HomeAction()
}
