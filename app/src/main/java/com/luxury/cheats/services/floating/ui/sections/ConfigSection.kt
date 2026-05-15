package com.luxury.cheats.services.floating.ui.sections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.services.floating.logic.FloatingWidgetManager

private val SLIDER_PURPLE = Color(0xFF6A5ACD)
private val INACTIVE_TRACK_COLOR = Color(0xFFE0E0FF)

private const val COLOR_GOLD = 0xFFFFD700L
private const val COLOR_RED = 0xFFFF0000L
private const val COLOR_GREEN = 0xFF00FF00L
private const val COLOR_BLUE = 0xFF0088FFL
private const val COLOR_MAGENTA = 0xFFFF00FFL

private val COOKIE_BG_ACTIVE = Color(0xFF003300)
private val COOKIE_BG_INACTIVE = Color(0xFF330000)
private val COOKIE_ICON_ACTIVE = Color(0xFFBBFF00)
private val COOKIE_ICON_INACTIVE = Color(0xFFFF4444)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConfigSection(
    manager: FloatingWidgetManager,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit
) {
    val config by manager.config.collectAsState()
    val cookieShape = MaterialShapes.Cookie9Sided.toShape()

    val fadeSpec = tween<Float>(durationMillis = 200)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val cookieSize = 84.dp

            // Shape 1: FOV (Microsección FOV)
            ConfigCookie(
                isActive = config.isFovEnabled,
                icon = Icons.Default.OpenInFull,
                shape = cookieShape,
                size = cookieSize,
                onClick = {
                    val newState = !config.isFovEnabled
                    manager.toggleFov(newState)
                    manager.updateMicroSection("FOV")
                    onExpandChange(newState)
                }
            )

            // Shape 2: Drag Click (Microsección Drag)
            ConfigCookie(
                isActive = config.isDragClickEnabled,
                icon = Icons.Default.AdsClick,
                shape = cookieShape,
                size = cookieSize,
                onClick = {
                    val newState = !config.isDragClickEnabled
                    manager.toggleDragClick(newState)
                    manager.updateMicroSection("Drag")
                    onExpandChange(newState)
                }
            )

            // Shape 3: Point Scan (Microsección Point)
            ConfigCookie(
                isActive = config.isPointScanEnabled,
                icon = Icons.Default.TrackChanges,
                shape = cookieShape,
                size = cookieSize,
                onClick = {
                    val newState = !config.isPointScanEnabled
                    manager.togglePointScan(newState)
                    manager.updateMicroSection("Point")
                    onExpandChange(newState)
                }
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(animationSpec = fadeSpec) + scaleIn(initialScale = 0.95f, animationSpec = fadeSpec),
            exit = fadeOut(animationSpec = fadeSpec) + scaleOut(targetScale = 0.95f, animationSpec = fadeSpec)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                when (config.selectedMicroSection) {
                    "FOV" -> {
                        if (config.isFovEnabled) {
                            Text(
                                text = "Controle el FOV",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Slider(
                                value = config.fovRadius,
                                onValueChange = { manager.updateFov(it) },
                                valueRange = 25f..600f,
                                colors = SliderDefaults.colors(
                                    thumbColor = SLIDER_PURPLE,
                                    activeTrackColor = SLIDER_PURPLE,
                                    inactiveTrackColor = INACTIVE_TRACK_COLOR.copy(alpha = 0.8f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    "Drag" -> {
                        if (config.isDragClickEnabled) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Fijar en la pantalla",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Switch(
                                    checked = config.isDragClickLocked,
                                    onCheckedChange = { manager.toggleDragClickLock(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = SLIDER_PURPLE,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f),
                                        uncheckedBorderColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }

                    "Point" -> {
                        if (config.isPointScanEnabled) {
                            // 1. Tamaño
                            Text(
                                text = "Tamaño del Crosshair",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Slider(
                                value = config.crosshairSize,
                                onValueChange = { manager.updateCrosshairSize(it) },
                                valueRange = 10f..150f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(config.crosshairColor),
                                    activeTrackColor = Color(config.crosshairColor),
                                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 2. Colores (Cookies)
                            Text("Color", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val colors = listOf(
                                    COLOR_GOLD to "Dorado",
                                    COLOR_RED to "Rojo",
                                    COLOR_GREEN to "Verde",
                                    COLOR_BLUE to "Azul",
                                    COLOR_MAGENTA to "Magenta"
                                )
                                colors.forEach { (colorValue, _) ->
                                    ColorCookie(
                                        color = Color(colorValue),
                                        isSelected = config.crosshairColor == colorValue,
                                        onClick = { manager.updateCrosshairColor(colorValue) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // 3. Diseños
                            Text("Diseño", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val designs = listOf("Classic", "Box", "Dot", "Corners")
                                designs.forEach { design ->
                                    val isSelected = config.crosshairDesign == design
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) {
                                                    Color(config.crosshairColor).copy(alpha = 0.2f)
                                                } else {
                                                    Color.White.copy(alpha = 0.05f)
                                                }
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) {
                                                    Color(config.crosshairColor)
                                                } else {
                                                    Color.Transparent
                                                },
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { manager.updateCrosshairDesign(design) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = design,
                                            color = if (isSelected) {
                                                Color(config.crosshairColor)
                                            } else {
                                                Color.White.copy(alpha = 0.6f)
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ColorCookie(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = MaterialShapes.Cookie9Sided.toShape()
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(shape)
            .background(color)
            .border(2.dp, if (isSelected) Color.White else Color.Transparent, shape)
            .clickable { onClick() }
    )
}

@Composable
fun ConfigCookie(
    isActive: Boolean,
    icon: ImageVector,
    shape: Shape,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val backgroundColor = if (isActive) COOKIE_BG_ACTIVE else COOKIE_BG_INACTIVE
    val iconColor = if (isActive) COOKIE_ICON_ACTIVE else COOKIE_ICON_INACTIVE

    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(32.dp)
        )
    }
}
