package com.luxury.cheats.core.theme

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * ThemeManager - Gestión centralizada de temas
 * - Soporte para modo claro/oscuro adaptativo al sistema
 * - Material You (Dynamic Colors) con fallbacks premium
 * - Colores premium, minimalistas e intencionales
 * - Sin overrides sucios de lógica en tiempo de ejecución
 */
object ThemeManager {

    /**
     * Obtiene el ColorScheme según el tema del sistema y Material You
     */
    @Composable
    fun getColorScheme(
        context: Context = LocalContext.current,
        darkTheme: Boolean = isSystemInDarkTheme(),
        useDynamicColor: Boolean = true
    ): ColorScheme {
        // Material You disponible en Android 12+
        val isDynamicAvailable = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
        
        // Si Material You está disponible, usarlo directamente
        if (useDynamicColor && isDynamicAvailable) {
            return if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        
        // Fallback para dispositivos sin Material You
        return if (darkTheme) DarkColorScheme else LightColorScheme
    }

    /**
     * Verifica si Material You está disponible
     */
    fun isDynamicColorAvailable(): Boolean = 
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
    
    /**
     * Verifica si el sistema está en modo oscuro
     */
    @Composable
    fun isDarkTheme(): Boolean = isSystemInDarkTheme()
}

/**
 * ColorScheme para modo oscuro - Estética "Onyx Luxury"
 */
private val DarkColorScheme = darkColorScheme(
    primary = CleanWhite,
    onPrimary = OnyxBlack,
    primaryContainer = Gray40, 
    onPrimaryContainer = CleanWhite,
    
    secondary = AccentSilver,
    onSecondary = OnyxBlack,
    secondaryContainer = Color(0xFF2D2D2D), 
    onSecondaryContainer = CleanWhite,

    tertiary = LuxuryOrange,
    onTertiary = OnyxBlack,
    tertiaryContainer = Color(0xFF3B443B), 
    onTertiaryContainer = CleanWhite,
    
    background = OnyxBlack,
    onBackground = CleanWhite,
    
    surface = OnyxBlack,
    onSurface = CleanWhite,
    
    surfaceVariant = PremiumGray, // 202020 - Requested for Login Screen background
    onSurfaceVariant = CleanWhite, // White - Requested for Login text
    
    surfaceContainer = Gray40,    // 404040 - Requested for Login input backgrounds
    
    outline = MediumGray77,       // 777777 - Requested for Login Screen border
    outlineVariant = Color(0xFF707070), 

    error = Color(0xFFCF6679),
    onError = OnyxBlack,
    errorContainer = Color(0xFFB00020),
    onErrorContainer = CleanWhite,

    scrim = Color.Black,
    inverseSurface = CleanWhite,
    inverseOnSurface = OnyxBlack,
    inversePrimary = OnyxBlack
)



/**
 * ColorScheme para modo claro - Estética "Clean Pure"
 */
private val LightColorScheme = lightColorScheme(
    primary = OnyxBlack,
    onPrimary = CleanWhite,
    primaryContainer = Color(0xFFE0E0E0),
    onPrimaryContainer = OnyxBlack,
    
    secondary = MediumGray,
    onSecondary = CleanWhite,
    secondaryContainer = Color(0xFFECECEC),
    onSecondaryContainer = OnyxBlack,

    tertiary = LuxuryOrange,
    onTertiary = CleanWhite,
    tertiaryContainer = LuxuryOrange,
    onTertiaryContainer = CleanWhite,
    
    background = Color(0xFFF8F8F8), // Un fondo casi blanco pero que permite ver tarjetas blancas
    onBackground = OnyxBlack,
    
    surface = CleanWhite,          // Tarjetas pueden ser blancas sobre el fondo grisáceo
    onSurface = OnyxBlack,
    
    surfaceVariant = Color(0xFFEDEDED), // Gris para tarjetas secundarias
    onSurfaceVariant = OnyxBlack,
    
    surfaceContainer = Color(0xFFD8D8D8), // Gris más oscuro para inputs
    
    outline = AccentSilver,
    outlineVariant = LightGray,

    error = Color(0xFFB00020),
    onError = CleanWhite,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410002)
)



