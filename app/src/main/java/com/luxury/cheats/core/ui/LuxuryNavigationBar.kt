package com.luxury.cheats.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

/**
 * Navigation Bar personalizado con efecto Glassmorphism usando Backdrop
 * Dimensiones: 190dp x 99dp
 */
@Composable
fun LuxuryNavigationBar(
    modifier: Modifier = Modifier,
    activeTab: String = "Inicio",
    backdrop: LayerBackdrop? = null,
    onTabSelected: (String) -> Unit = {},
    onFabClick: () -> Unit = {},
) {
    val shape = RoundedCornerShape(30.dp)
    val fabShape = RoundedCornerShape(32.dp)

    // Lifted interaction sources to coordinate physics globally
    val interactionSource0 = remember { MutableInteractionSource() }
    val interactionSource1 = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }
    val interactionSourceFab = remember { MutableInteractionSource() }

    val isPressed0 by interactionSource0.collectIsPressedAsState()
    val isPressed1 by interactionSource1.collectIsPressedAsState()
    val isPressed2 by interactionSource2.collectIsPressedAsState()
    val isPressedFab by interactionSourceFab.collectIsPressedAsState()

    val anyNavPressed = isPressed0 || isPressed1 || isPressed2
    val pushAmountGlobal = 12f

    val targetTransNav = when {
        isPressedFab -> -pushAmountGlobal
        else -> 0f
    }

    val targetTransFab = when {
        anyNavPressed -> pushAmountGlobal
        else -> 0f
    }

    val springSpec = androidx.compose.animation.core.spring<Float>(
        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
        stiffness = androidx.compose.animation.core.Spring.StiffnessLow
    )

    val transNav by androidx.compose.animation.core.animateFloatAsState(targetTransNav, springSpec, label = "transNav")
    val transFab by androidx.compose.animation.core.animateFloatAsState(targetTransFab, springSpec, label = "transFab")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer { translationX = transNav.dp.toPx() }
                .size(width = 280.dp, height = 99.dp),
            contentAlignment = Alignment.Center,
        ) {
            LuxuryNavBarBackground(backdrop, shape)
            LuxuryNavBarItems(
                activeTab = activeTab,
                interactionSources = listOf(interactionSource0, interactionSource1, interactionSource2),
                onTabSelected = onTabSelected
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        ExpressiveFab(
            backdrop = backdrop,
            shape = fabShape,
            interactionSource = interactionSourceFab,
            modifier = Modifier.graphicsLayer { translationX = transFab.dp.toPx() },
            onClick = onFabClick
        )
    }
}

@Composable
private fun ExpressiveFab(
    backdrop: LayerBackdrop?,
    shape: RoundedCornerShape,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isPressed by interactionSource.collectIsPressedAsState()

    // Resorte táctico (MD3 Expressive)
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "FabScale"
    )

    Box(
        modifier = modifier
            .size(width = 96.dp, height = 99.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center,
    ) {
        LuxuryNavBarBackground(backdrop, shape)

        // Borde translúcido para consistencia visual
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    shape = shape,
                )
        )

        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(42.dp)
        )
    }
}

@Composable
private fun LuxuryNavBarBackground(
    backdrop: LayerBackdrop?,
    shape: RoundedCornerShape,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clip(shape)
                .then(
                    if (backdrop != null) {
                        Modifier.drawBackdrop(
                            backdrop = backdrop,
                            shape = { shape },
                            effects = {
                                lens(refractionHeight = 4f, refractionAmount = 0.5f)
                                blur(radius = 15f)
                                vibrancy()
                            },
                        )
                    } else {
                        Modifier.background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f))
                    },
                ),
    )
}

@Composable
private fun LuxuryNavBarItems(
    activeTab: String,
    interactionSources: List<MutableInteractionSource>,
    onTabSelected: (String) -> Unit,
) {
    val interactionSource0 = interactionSources[0]
    val interactionSource1 = interactionSources[1]
    val interactionSource2 = interactionSources[2]

    val isPressed0 by interactionSource0.collectIsPressedAsState()
    val isPressed1 by interactionSource1.collectIsPressedAsState()
    val isPressed2 by interactionSource2.collectIsPressedAsState()

    val pushAmount = 14f

    val targetTrans0 = when {
        isPressed1 -> -pushAmount
        isPressed2 -> -pushAmount * 1.5f
        else -> 0f
    }

    val targetTrans1 = when {
        isPressed0 -> pushAmount
        isPressed2 -> -pushAmount
        else -> 0f
    }

    val targetTrans2 = when {
        isPressed0 -> pushAmount * 1.5f
        isPressed1 -> pushAmount
        else -> 0f
    }

    val springSpec = androidx.compose.animation.core.spring<Float>(
        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
        stiffness = androidx.compose.animation.core.Spring.StiffnessLow
    )

    val trans0 by androidx.compose.animation.core.animateFloatAsState(targetTrans0, springSpec, label = "trans0")
    val trans1 by androidx.compose.animation.core.animateFloatAsState(targetTrans1, springSpec, label = "trans1")
    val trans2 by androidx.compose.animation.core.animateFloatAsState(targetTrans2, springSpec, label = "trans2")

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(30.dp),
                )
                .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NavItem(
                label = "Inicio",
                icon = Icons.Outlined.Home,
                isActive = activeTab == "Inicio",
                activeBgColor = Color(0xFF88BF90),
                activeIconColor = Color(0xFFDBFFD4),
                interactionSource = interactionSource0,
                translationX = trans0,
                onSelect = { onTabSelected("Inicio") },
            )

            NavItem(
                label = "Tools",
                icon = Icons.Outlined.Settings,
                isActive = activeTab == "Tools",
                activeBgColor = Color(0xFFA3C7D4),
                activeIconColor = Color(0xFFE0FFFF),
                interactionSource = interactionSource1,
                translationX = trans1,
                onSelect = { onTabSelected("Tools") },
            )

            NavItem(
                label = "Perfil",
                icon = Icons.Outlined.AccountCircle,
                isActive = activeTab == "Perfil",
                activeBgColor = Color(0xFFD4A3CF),
                activeIconColor = Color(0xFFFFE0F9),
                interactionSource = interactionSource2,
                translationX = trans2,
                onSelect = { onTabSelected("Perfil") },
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    activeBgColor: Color,
    activeIconColor: Color,
    interactionSource: MutableInteractionSource,
    translationX: Float,
    onSelect: () -> Unit,
) {
    val isPressed by interactionSource.collectIsPressedAsState()

    // Resorte táctico (MD3 Expressive)
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "NavItemScale"
    )

    Column(
        modifier =
            Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    this.translationX = translationX.dp.toPx()
                }
                .clickable(
                    interactionSource = interactionSource,
                    // Sin ripple para mantener estética premium minimalista
                    indication = null,
                ) { onSelect() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(width = 72.dp, height = 46.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isActive) activeBgColor else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) activeIconColor else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
