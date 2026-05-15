package com.luxury.cheats.services.floating.ui

import com.luxury.cheats.services.floating.ui.components.CategoryItem
import com.luxury.cheats.services.floating.ui.sections.AimbotSection
import com.luxury.cheats.services.floating.ui.sections.ConfigSection
import com.luxury.cheats.services.floating.ui.sections.GlooSection
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

private val COLOR_GRAY_NORMAL = Color(0xFF8E8E8E)
private val COLOR_GREEN_SELECTED = Color(0xFFBBFF00)

private const val MENU_MARGIN_DP = 32
private const val CONFIG_BASE_HEIGHT_DP = 175
private const val CONFIG_FOV_EXTRA_HEIGHT_DP = 120
private const val CONFIG_DRAG_EXTRA_HEIGHT_DP = 80
private const val CONFIG_POINT_EXTRA_HEIGHT_DP = 280

/**
 * Contenido visual del widget flotante con animación de Morphing.
 */
@Composable
fun FloatingWidgetContent(
    manager: com.luxury.cheats.services.floating.logic.FloatingWidgetManager,
    config: com.luxury.cheats.services.floating.logic.FloatingWidgetConfig,
    onToggleMenu: () -> Unit = {},
    onSelectCategory: (String) -> Unit = {},
    onSetAimbotTarget: (String) -> Unit = {}
) {
    val widthDp = config.width
    val heightDp = config.height
    val strokeWidthDp = config.strokeWidth
    val isStrokeEnabled = config.isStrokeEnabled
    val strokeColorLong = config.strokeColor
    val isMenuVisible = config.isMenuVisible
    val selectedCategory = config.selectedCategory

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    val strokeWidth = if (isStrokeEnabled) strokeWidthDp else 0f
    val strokeColor = if (strokeColorLong == 0L) {
        MaterialTheme.colorScheme.primary
    } else {
        Color(strokeColorLong)
    }

    var isConfigExpanded by remember { mutableStateOf(false) }

    val targetWidth = if (isMenuVisible) (screenWidth - MENU_MARGIN_DP).dp else widthDp.dp
    val targetHeight = when {
        !isMenuVisible -> heightDp.dp
        selectedCategory.isEmpty() -> 64.dp
        selectedCategory == "Aimbot" -> 270.dp
        selectedCategory == "Config" -> {
            if (isConfigExpanded) {
                var h = CONFIG_BASE_HEIGHT_DP
                h += when (config.selectedMicroSection) {
                    "FOV" -> if (config.isFovEnabled) CONFIG_FOV_EXTRA_HEIGHT_DP else 0
                    "Drag" -> if (config.isDragClickEnabled) CONFIG_DRAG_EXTRA_HEIGHT_DP else 0
                    "Point" -> if (config.isPointScanEnabled) CONFIG_POINT_EXTRA_HEIGHT_DP else 0
                    else -> 0
                }
                h.dp
            } else CONFIG_BASE_HEIGHT_DP.dp
        }
        selectedCategory == "Gloo" -> 260.dp
        else -> 240.dp
    }
    val targetCornerRadius = if (isMenuVisible) 32.dp else (heightDp / 2).dp

    val morphSpec = tween<androidx.compose.ui.unit.Dp>(
        durationMillis = 250,
        easing = FastOutSlowInEasing
    )

    val animatedWidth by animateDpAsState(targetValue = targetWidth, animationSpec = morphSpec, label = "W")
    val animatedHeight by animateDpAsState(targetValue = targetHeight, animationSpec = morphSpec, label = "H")
    val animatedCornerRadius by animateDpAsState(
        targetValue = targetCornerRadius,
        animationSpec = morphSpec,
        label = "R"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. CAPA DE FONDO PARA CIERRE (Solo visible si el menú está abierto)
        if (isMenuVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onToggleMenu() // Cierra al tocar en cualquier lugar fuera del menú
                    }
            )
        }

        // 2. CONTENEDOR MORPHING ÚNICO
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
                .size(width = animatedWidth, height = animatedHeight)
                .clip(RoundedCornerShape(animatedCornerRadius))
                .background(Color.Black)
                .then(
                    if (isStrokeEnabled) {
                        Modifier.border(
                            width = strokeWidth.dp,
                            color = strokeColor,
                            shape = RoundedCornerShape(animatedCornerRadius)
                        )
                    } else Modifier
                )
                .systemGestureExclusion()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // CONSUME EL TOQUE: Si el menú está abierto, no hacemos nada para evitar que el
                    // click "atraviese" hacia el fondo y cierre el widget.
                }
                .then(
                    // Solo escuchamos el toque de apertura si el menú está cerrado
                    if (!isMenuVisible) {
                        Modifier.pointerInput(Unit) {
                            awaitEachGesture {
                                awaitFirstDown().also { it.consume() }
                                onToggleMenu()
                            }
                        }
                    } else Modifier
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            if (isMenuVisible) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val categories = listOf("Aimbot", "Config", "Gloo", "Ai", "Optimizar", "Mas")
                        categories.forEach { category ->
                            CategoryItem(
                                text = category,
                                isSelected = selectedCategory == category,
                                normalColor = COLOR_GRAY_NORMAL,
                                selectedColor = COLOR_GREEN_SELECTED,
                                onClick = {
                                    if (selectedCategory == category) onSelectCategory("")
                                    else onSelectCategory(category)
                                }
                            )
                        }
                    }

                    if (selectedCategory == "Aimbot") {
                        AimbotSection(
                            target = config.aimbotTarget,
                            onTargetSelected = onSetAimbotTarget
                        )
                    }
                    if (selectedCategory == "Config") {
                        ConfigSection(
                            manager = manager,
                            isExpanded = isConfigExpanded,
                            onExpandChange = { isConfigExpanded = it }
                        )
                    }

                    if (selectedCategory == "Gloo") {
                        GlooSection(
                            isRotated = config.isGlooRotated,
                            onRotate = { manager.toggleGlooRotation() }
                        )
                    }
                }
            }
        }
    }
}
