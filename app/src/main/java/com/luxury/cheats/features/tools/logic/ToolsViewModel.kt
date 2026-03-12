package com.luxury.cheats.features.tools.logic

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luxury.cheats.services.floating.FloatingWidgetManager
import com.luxury.cheats.services.floating.FloatingControlService
import com.luxury.cheats.services.floating.IslandAccessibilityService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de Herramientas
 * Maneja la lógica de configuración del widget flotante.
 */
@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val floatingWidgetManager: FloatingWidgetManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ToolsState())
    val uiState: StateFlow<ToolsState> = _uiState.asStateFlow()

    init {
        // Sincronizar configuración del widget flotante
        viewModelScope.launch {
            floatingWidgetManager.config.collectLatest { config ->
                _uiState.update {
                    it.copy(
                        floatingWidth = config.width,
                        floatingHeight = config.height,
                        floatingCenterX = config.centerX,
                        floatingCenterY = config.centerY,
                        floatingStrokeWidth = config.strokeWidth,
                        isFloatingStrokeEnabled = config.isStrokeEnabled,
                        floatingStrokeColor = config.strokeColor
                    )
                }
            }
        }
    }

    fun onAction(action: ToolsAction) {
        when (action) {
            is ToolsAction.UpdateFloatingConfig -> updateFloatingConfig(action)
            ToolsAction.ToggleFloatingWidget -> toggleFloatingWidget()
        }
    }

    private fun updateFloatingConfig(config: ToolsAction.UpdateFloatingConfig) {
        floatingWidgetManager.updateConfig(
            width = config.width,
            height = config.height,
            centerX = config.centerX,
            centerY = config.centerY,
            strokeWidth = config.strokeWidth,
            isStrokeEnabled = config.isStrokeEnabled,
            strokeColor = config.strokeColor
        )
    }

    private fun toggleFloatingWidget() {
        val isActive = !_uiState.value.isFloatingWidgetActive

        if (isActive && !isAccessibilityServiceEnabled()) {
             val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        }

        if (isActive && !Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                android.net.Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            return
        }

        _uiState.update { it.copy(isFloatingWidgetActive = isActive) }
        val intent = Intent(context, FloatingControlService::class.java)
        if (isActive) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } else {
            context.stopService(intent)
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedComponentName = android.content.ComponentName(context, IslandAccessibilityService::class.java)
        val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        return enabledServices?.contains(expectedComponentName.flattenToString()) == true
    }
}
