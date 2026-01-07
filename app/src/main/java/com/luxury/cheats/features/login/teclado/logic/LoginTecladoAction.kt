package com.luxury.cheats.features.login.teclado.logic

/**
 * Acciones que el teclado puede emitir hacia el ViewModel.
 */
sealed class LoginTecladoAction {
    /** Acción cuando se presiona una tecla de carácter. */
    data class OnKeyPress(val key: String) : LoginTecladoAction()

    /** Acción para borrar un carácter. */
    object OnDelete : LoginTecladoAction()

    /** Acción cuando se confirma la entrada (Done). */
    object OnDone : LoginTecladoAction()
}
