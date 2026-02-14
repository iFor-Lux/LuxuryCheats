package com.luxury.cheats.features.perfil.logic

/**
 * Acciones de usuario disponibles en la pantalla de Perfil.
 */
sealed class PerfilAction {
    /** Click en el botón de cerrar sesión. */
    data object LogoutClicked : PerfilAction()

    /** Click en el botón de soporte. */
    data object SupportClicked : PerfilAction()

    /** Click en el botón de comunidad. */
    data object CommunityClicked : PerfilAction()

    /** Click en la imagen de perfil para cambiarla. */
    data object ProfileImageClicked : PerfilAction()

    /** Click en el banner para cambiarlo. */
    data object BannerImageClicked : PerfilAction()

    /** Nueva imagen de perfil seleccionada. */
    data class ProfileImageSelected(val uri: String) : PerfilAction()

    /** Nuevo banner seleccionado. */
    data class BannerImageSelected(val uri: String) : PerfilAction()
}
