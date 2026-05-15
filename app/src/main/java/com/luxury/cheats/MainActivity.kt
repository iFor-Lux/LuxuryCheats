package com.luxury.cheats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.luxury.cheats.core.theme.LuxuryCheatsTheme
import com.luxury.cheats.services.storage.UserPreferencesService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Actividad principal de la aplicación.
 * Es el punto de entrada que configura el tema y el layout base (LuxuryCheatsApp).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferencesService: UserPreferencesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Aplicar protección Anti-Recording dinámicamente con repeatOnLifecycle
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                preferencesService.antiRecordingFlow.collect { isEnabled ->
                    if (isEnabled) {
                        window.setFlags(
                            android.view.WindowManager.LayoutParams.FLAG_SECURE,
                            android.view.WindowManager.LayoutParams.FLAG_SECURE,
                        )
                    } else {
                        window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
                    }
                }
            }
        }

        setContent {
            LuxuryCheatsTheme {
                LuxuryCheatsApp()
            }
        }
    }
}
