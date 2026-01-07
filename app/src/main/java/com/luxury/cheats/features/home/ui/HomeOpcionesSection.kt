package com.luxury.cheats.features.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val SECTION_WIDTH = 340
private const val SECTION_HEIGHT = 450
private const val WIDE_CARD_HEIGHT = 80

private object OptionsConstants {
    const val COLLAPSE_THRESHOLD = 150f
    const val ANIM_DUR_COLLAPSED = 350
    const val ANIM_DUR_FADE = 250
    const val SCALE_TRANSITION = 0.95f
    const val ALPHA_COLLAPSED = 0.6f
    const val ROTATION_VERTICAL = -90f
    const val TOPOGRAPHIC_ROT = -21f
    const val TOPOGRAPHIC_SCALE = 1.1f
    const val TOPOGRAPHIC_OFF_MINUS = -10f
    const val TOPOGRAPHIC_P1 = 0.65f
    const val TOPOGRAPHIC_P2 = 0.33f
    const val TOPOGRAPHIC_P3 = 0.77f
}

/**
 * Representa una opción de trampa o herramienta en la UI.
 */
data class CheatOption(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val initialValue: Boolean = false
)

/**
 * Sección de Opciones (Rediseño con Carrusel Heroico)
 * - W: 340, H: 450
 * - Carrusel Centrado para Trampas + Botón Panel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeOpcionesSection(
    modifier: Modifier = Modifier
) {
    val options = listOf(
        CheatOption("Aimbot", "Precisión letal a la cabeza", Icons.Default.GpsFixed),
        CheatOption("Holograma", "Visualización de enemigos", Icons.Default.Visibility),
        CheatOption("WallHack", "Ver a través de estructuras", Icons.Default.Layers),
        CheatOption("AimFov", "Campo de visión mejorado", Icons.Default.CenterFocusStrong)
    )

    val carouselState = rememberCarouselState { options.size }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    // Calculamos el centro de la pantalla en píxeles
    val screenCenterX = with(density) { (configuration.screenWidthDp.dp / 2).toPx() }

    Column(
        modifier = modifier
            .width(SECTION_WIDTH.dp)
            .height(SECTION_HEIGHT.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(30.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeOptionsCarousel(options, carouselState, scope, screenCenterX)

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            WideOptionCard("PANEL DE CONTROL", "Ajustes avanzados y configuración")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeOptionsCarousel(
    options: List<CheatOption>,
    carouselState: androidx.compose.material3.carousel.CarouselState,
    scope: kotlinx.coroutines.CoroutineScope,
    screenCenterX: Float
) {
    Text(
        text = "OPCIONES",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    HorizontalCenteredHeroCarousel(
        state = carouselState,
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        itemSpacing = 8.dp
    ) { index ->
        val option = options[index]

        OptionCard(
            option = option,
            screenCenterX = screenCenterX,
            modifier = Modifier
                .fillMaxSize()
                .maskClip(MaterialTheme.shapes.extraLarge)
                .clickable {
                    scope.launch { carouselState.animateScrollToItem(index) }
                }
        )
    }
}

/**
 * Tarjeta individual adaptada para el carrusel.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OptionCard(
    option: CheatOption,
    screenCenterX: Float,
    modifier: Modifier = Modifier
) {
    var checked by remember { mutableStateOf(option.initialValue) }
    var itemCenterX by remember { mutableFloatStateOf(0f) }

    val distanceFromCenter = abs(itemCenterX - screenCenterX)
    val isCollapsed = distanceFromCenter > OptionsConstants.COLLAPSE_THRESHOLD
    val alpha by animateFloatAsState(
        targetValue = if (isCollapsed) OptionsConstants.ALPHA_COLLAPSED else 1f,
        animationSpec = tween(
            durationMillis = OptionsConstants.ANIM_DUR_COLLAPSED, 
            easing = FastOutSlowInEasing
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .onGloballyPositioned { coordinates ->
                val position = coordinates.positionInRoot()
                itemCenterX = position.x + coordinates.size.width / 2f
            }
    ) {
        TopographicBackground(
            modifier = Modifier.fillMaxSize(),
            lineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            lineCount = 7
        )

        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            AnimatedContent(
                targetState = isCollapsed,
                transitionSpec = {
                    val fadeTween = tween<Float>(OptionsConstants.ANIM_DUR_FADE)
                    val scaleIn = scaleIn(initialScale = OptionsConstants.SCALE_TRANSITION)
                    val scaleOut = scaleOut(targetScale = OptionsConstants.SCALE_TRANSITION)
                    
                    fadeIn(fadeTween) + scaleIn togetherWith fadeOut(fadeTween) + scaleOut
                },
                label = "layout_change"
            ) { collapsed ->
                if (collapsed) {
                    OptionCardCollapsed(option.title, alpha)
                } else {
                    OptionCardHero(option, checked, alpha, onCheckedChange = { checked = it })
                }
            }
        }
    }
}

@Composable
private fun OptionCardCollapsed(title: String, alpha: Float) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title.uppercase(),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .rotate(OptionsConstants.ROTATION_VERTICAL)
                .wrapContentSize(unbounded = true)
                .graphicsLayer { this.alpha = alpha }
        )
    }
}

@Composable
private fun OptionCardHero(
    option: CheatOption,
    checked: Boolean,
    alpha: Float,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .graphicsLayer { this.alpha = alpha }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = option.title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = option.description,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 12.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Tarjeta de opción ancha para el panel de control.
 *
 * @param title Título de la opción.
 * @param description Descripción detallada de la opción.
 */
@Composable
fun WideOptionCard(title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(WIDE_CARD_HEIGHT.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }
    }
}

/**
 * Fondo Topográfico Personalizado.
 * Dibuja líneas onduladas que imitan un mapa de relieve.
 */
@Composable
fun TopographicBackground(
    modifier: Modifier = Modifier,
    lineColor: Color = Color.White.copy(alpha = 0.15f),
    lineCount: Int = 9,
    spacing: Dp = 22.dp,
    strokeWidth: Dp = 1.2.dp,
) {
    Canvas(modifier = modifier) {
        rotate(OptionsConstants.TOPOGRAPHIC_ROT) {
            scale(OptionsConstants.TOPOGRAPHIC_SCALE) {
                val spacingPx = spacing.toPx()
                val strokePx = strokeWidth.toPx()

                repeat(lineCount) { index ->
                    val offset = index * spacingPx

                    val path = Path().apply {
                        moveTo(
                            size.width * OptionsConstants.TOPOGRAPHIC_P1 + offset,
                            OptionsConstants.TOPOGRAPHIC_OFF_MINUS
                        )

                        cubicTo(
                            size.width * 0f + offset,
                            size.height * OptionsConstants.TOPOGRAPHIC_P1,
                            size.width * 1f + offset,
                            size.height * OptionsConstants.TOPOGRAPHIC_P2,
                            size.width * OptionsConstants.TOPOGRAPHIC_P3 + offset,
                            size.height * 1f
                        )
                    }

                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(
                            width = strokePx,
                            cap = StrokeCap.Round,
                            pathEffect = null
                        )
                    )
                }
            }
        }
    }
}
