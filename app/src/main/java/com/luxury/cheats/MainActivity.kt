package com.luxury.cheats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.luxury.cheats.core.theme.LuxuryCheatsTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Actividad principal de la aplicación.
 * Es el punto de entrada que configura el tema y el layout base (LuxuryCheatsApp).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LuxuryCheatsTheme {
                LuxuryCheatsApp()
            }
        }
    }
}
