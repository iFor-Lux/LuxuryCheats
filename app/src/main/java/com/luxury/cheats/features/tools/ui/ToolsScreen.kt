package com.luxury.cheats.features.tools.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.luxury.cheats.core.ui.FadingEdges
import com.luxury.cheats.core.ui.SquarePatternBackground
import com.luxury.cheats.features.tools.logic.ToolsAction
import com.luxury.cheats.features.tools.logic.ToolsViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ToolsScreen(
    viewModel: ToolsViewModel,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop? = null,
) {
    val uiState by viewModel.uiState.collectAsState()

    MaterialExpressiveTheme(
        motionScheme = MotionScheme.expressive(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .then(if (backdrop != null) Modifier.layerBackdrop(backdrop) else Modifier),
            ) {
                // Fondo con patrón de cuadrícula
                SquarePatternBackground()

                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Spacer(modifier = Modifier.statusBarsPadding())

                    ToolsWidgetConfigSection(
                        uiState = uiState,
                        onAction = { viewModel.onAction(it) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ToolsSecuritySection(
                        uiState = uiState,
                        onAction = { viewModel.onAction(it) },
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ToolsAimbotSection(
                        uiState = uiState,
                        onAction = { viewModel.onAction(it) },
                    )

                    Spacer(modifier = Modifier.height(130.dp)) // Espacio para el BottomBar
                }

                if (uiState.showVipDialog && uiState.vipMessage != null) {
                    VipRestrictionDialog(
                        message = uiState.vipMessage!!,
                        onDismiss = { viewModel.onAction(ToolsAction.DismissVipDialog) },
                    )
                }

                // Efecto de bordes desvanecidos
                FadingEdges()
            }
        }
    }
}
