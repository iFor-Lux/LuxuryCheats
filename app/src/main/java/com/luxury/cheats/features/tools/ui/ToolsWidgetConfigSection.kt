package com.luxury.cheats.features.tools.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath
import com.luxury.cheats.features.tools.logic.ToolsAction
import com.luxury.cheats.features.tools.logic.ToolsState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private object ConfigConstants {
    const val MIN_WIDTH = 50f
    const val MAX_WIDTH = 400f
    const val MIN_HEIGHT = 5f
    const val MAX_HEIGHT = 50f
    const val PERCENT_MAX = 100f
    const val MIN_Y = -50f

    const val COLOR_BLACK = 0xFF000000L
    const val COLOR_BLUE = 0xFF2196F3L
    const val COLOR_YELLOW = 0xFFFFFF00L
    const val COLOR_GRAY = 0xFF757575L
    const val COLOR_WHITE = 0xFFFFFFFFL
    const val COLOR_CUSTOM = -1L
    const val COLOR_DYNAMIC = 0L

    const val MORPH_CORNER_ROUNDING_FACTOR = 0.3f
    const val MORPH_ROTATION_DEGREES = -90f

    const val HSV_ARRAY_SIZE = 3
    const val HEX_STRING_LENGTH = 6
    const val COLOR_PICKER_ASPECT_RATIO = 1.5f
    const val HUE_MAX = 360f
    const val HUE_LIST_SIZE = 361
}

/**
 * Sección de configuración para personalizar el Widget Flotante.
 */
@Composable
fun ToolsWidgetConfigSection(
    uiState: ToolsState,
    onAction: (ToolsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(true) }
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "ArrowRotation",
    )

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    MaterialTheme.shapes.extraLarge,
                )
                .padding(16.dp),
    ) {
        WidgetConfigHeader(
            isExpanded = isExpanded,
            rotation = rotation,
            onToggle = { isExpanded = !isExpanded },
        )

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                SizeControls(uiState = uiState, onAction = onAction)

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                )

                PositionControls(uiState = uiState, onAction = onAction)

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                )

                StrokeControls(uiState = uiState, onAction = onAction)
            }
        }
    }
}

@Composable
private fun WidgetConfigHeader(
    isExpanded: Boolean,
    rotation: Float,
    onToggle: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onToggle() }
                .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "PERSONALIZAR WIDGET",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.primary,
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Colapsar" else "Expandir",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.rotate(rotation),
        )
    }
}

@Composable
private fun SizeControls(
    uiState: ToolsState,
    onAction: (ToolsAction) -> Unit,
) {
    val widthPercent = ((uiState.floatingWidth - ConfigConstants.MIN_WIDTH) /
        (ConfigConstants.MAX_WIDTH - ConfigConstants.MIN_WIDTH) * ConfigConstants.PERCENT_MAX)
        .coerceIn(0f, ConfigConstants.PERCENT_MAX)
    ConfigSlider(
        label = "ANCHO",
        valueText = "${widthPercent.roundToInt()}",
        value = widthPercent,
        range = 0f..100f,
        onValueChange = { percent ->
            val diff = ConfigConstants.MAX_WIDTH - ConfigConstants.MIN_WIDTH
            val actualWidth = ConfigConstants.MIN_WIDTH + percent / ConfigConstants.PERCENT_MAX * diff
            onAction(ToolsAction.UpdateFloatingConfig(width = actualWidth.roundToInt()))
        },
    )

    val heightPercent = ((uiState.floatingHeight - ConfigConstants.MIN_HEIGHT) /
        (ConfigConstants.MAX_HEIGHT - ConfigConstants.MIN_HEIGHT) * ConfigConstants.PERCENT_MAX)
        .coerceIn(0f, ConfigConstants.PERCENT_MAX)
    ConfigSlider(
        label = "ALTO",
        valueText = "${heightPercent.roundToInt()}",
        value = heightPercent,
        range = 0f..100f,
        arrowStep = 3f,
        onValueChange = { percent ->
            val diff = ConfigConstants.MAX_HEIGHT - ConfigConstants.MIN_HEIGHT
            val actualHeight = ConfigConstants.MIN_HEIGHT + percent / ConfigConstants.PERCENT_MAX * diff
            onAction(ToolsAction.UpdateFloatingConfig(height = actualHeight.roundToInt()))
        },
    )
}

@Composable
private fun PositionControls(
    uiState: ToolsState,
    onAction: (ToolsAction) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val screenHeight = configuration.screenHeightDp.toFloat()

    val xPercent = (uiState.floatingCenterX / screenWidth * ConfigConstants.PERCENT_MAX)
        .coerceIn(0f, ConfigConstants.PERCENT_MAX)
    ConfigSlider(
        label = "CENTRO X (HORIZONTAL)",
        valueText = "${xPercent.roundToInt()}",
        value = xPercent,
        range = 0f..100f,
        onValueChange = { percent ->
            val actualX = percent / ConfigConstants.PERCENT_MAX * screenWidth
            onAction(ToolsAction.UpdateFloatingConfig(centerX = actualX.roundToInt()))
        },
    )

    val minY = ConfigConstants.MIN_Y
    val yPercent = ((uiState.floatingCenterY - minY) / (screenHeight - minY) * ConfigConstants.PERCENT_MAX)
        .coerceIn(0f, ConfigConstants.PERCENT_MAX)
    ConfigSlider(
        label = "CENTRO Y (VERTICAL)",
        valueText = "${yPercent.roundToInt()}",
        value = yPercent,
        range = 0f..100f,
        decreaseIcon = Icons.Default.KeyboardArrowUp,
        increaseIcon = Icons.Default.KeyboardArrowDown,
        onValueChange = { percent ->
            val actualY = minY + percent / ConfigConstants.PERCENT_MAX * (screenHeight - minY)
            onAction(ToolsAction.UpdateFloatingConfig(centerY = actualY.roundToInt()))
        },
    )
}

@Composable
private fun StrokeControls(
    uiState: ToolsState,
    onAction: (ToolsAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "BORDE (STROKE)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
            Text(
                text = "Efecto exterior alrededor del widget",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }
        Switch(
            checked = uiState.isFloatingStrokeEnabled,
            onCheckedChange = { onAction(ToolsAction.UpdateFloatingConfig(isStrokeEnabled = it)) },
        )
    }

    if (uiState.isFloatingStrokeEnabled) {
        Spacer(modifier = Modifier.height(12.dp))
        ConfigSlider(
            label = "GROSOR DEL BORDE",
            valueText = "${uiState.floatingStrokeWidth.roundToInt()}",
            value = uiState.floatingStrokeWidth,
            range = 0f..8f,
            steps = 7,
            onValueChange = { onAction(ToolsAction.UpdateFloatingConfig(strokeWidth = it)) },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "COLOR DEL BORDE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(12.dp))

        StrokeColorSelector(
            selectedColor = uiState.floatingStrokeColor,
            onColorSelected = { onAction(ToolsAction.UpdateFloatingConfig(strokeColor = it)) },
        )
    }
}

@Composable
private fun StrokeColorSelector(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit,
) {
    var showColorPicker by remember { mutableStateOf(false) }
    val colors =
        listOf(
            Triple("Dinámico", ConfigConstants.COLOR_DYNAMIC, MaterialTheme.colorScheme.primary),
            Triple("Negro", ConfigConstants.COLOR_BLACK, Color.Black),
            Triple("Azul", ConfigConstants.COLOR_BLUE, Color(ConfigConstants.COLOR_BLUE)),
            Triple("Amarillo", ConfigConstants.COLOR_YELLOW, Color.Yellow),
            Triple("Gris", ConfigConstants.COLOR_GRAY, Color.Gray),
            Triple("Blanco", ConfigConstants.COLOR_WHITE, Color.White),
            Triple("Personalizar", ConfigConstants.COLOR_CUSTOM, Color.Transparent),
        )

    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = if (selectedColor == 0L) Color.White else Color(selectedColor),
            onColorSelected = {
                onColorSelected(it.toArgb().toLong())
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false },
        )
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(colors) { (name, value, color) ->
            ColorOptionItem(
                name = name,
                value = value,
                color = color,
                selectedColor = selectedColor,
                allOptions = colors,
                onSelect = {
                    if (value == ConfigConstants.COLOR_CUSTOM) {
                        showColorPicker = true
                    } else {
                        onColorSelected(value)
                    }
                },
            )
        }
    }
}

@Composable
private fun ColorOptionItem(
    name: String,
    value: Long,
    color: Color,
    selectedColor: Long,
    allOptions: List<Triple<String, Long, Color>>,
    onSelect: () -> Unit,
) {
    val isSelected = isColorOptionSelected(value, selectedColor, allOptions)
    val morphProgress by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 500f),
        label = "ColorMorphProgress",
    )
    val shape = remember(morphProgress) { DynamicMorphingShape(morphProgress) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp).clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
        ) { onSelect() },
    ) {
        ColorOptionCircle(
            value = value,
            color = color,
            selectedColor = selectedColor,
            isSelected = isSelected,
            shape = shape,
            allOptions = allOptions,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = name,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

@Composable
private fun ColorOptionCircle(
    value: Long,
    color: Color,
    selectedColor: Long,
    isSelected: Boolean,
    shape: Shape,
    allOptions: List<Triple<String, Long, Color>>,
) {
    Box(
        modifier = Modifier.size(48.dp)
            .background(
                color = when (value) {
                    ConfigConstants.COLOR_DYNAMIC -> MaterialTheme.colorScheme.primary
                    ConfigConstants.COLOR_CUSTOM -> Color.Transparent
                    else -> color
                },
                shape = shape,
            )
            .then(
                if (value == ConfigConstants.COLOR_CUSTOM) {
                    Modifier.border(
                        1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), shape,
                    )
                } else Modifier,
            )
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), shape)
                } else Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            val checkColor = if (value == ConfigConstants.COLOR_WHITE || color == Color.White) Color.Black else Color.White
            Icon(Icons.Default.Check, "Seleccionado", tint = checkColor, modifier = Modifier.size(24.dp))
        } else if (value == ConfigConstants.COLOR_CUSTOM) {
            CustomColorAddIcon(selectedColor = selectedColor, allOptions = allOptions)
        }
    }
}

@Composable
private fun CustomColorAddIcon(
    selectedColor: Long,
    allOptions: List<Triple<String, Long, Color>>,
) {
    val isCustomActive = selectedColor != ConfigConstants.COLOR_DYNAMIC &&
        allOptions.none { it.second == selectedColor }

    Box(
        modifier = Modifier.size(32.dp)
            .background(
                if (isCustomActive) Color(selectedColor) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                CircleShape,
            )
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        val iconColor = if (isCustomActive) {
            if (Color(selectedColor).luminance() > 0.5f) Color.Black else Color.White
        } else MaterialTheme.colorScheme.onSurfaceVariant

        Icon(Icons.Default.Add, null, tint = iconColor, modifier = Modifier.size(16.dp))
    }
}

private fun isColorOptionSelected(
    value: Long,
    selectedColor: Long,
    options: List<Triple<String, Long, Color>>,
): Boolean {
    if (value == selectedColor) return true
    if (value == ConfigConstants.COLOR_CUSTOM) {
        return selectedColor != ConfigConstants.COLOR_DYNAMIC && options.none { it.second == selectedColor }
    }
    return false
}

private class DynamicMorphingShape(private val progress: Float) : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val radius = minOf(size.width, size.height) / 2f
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        val startPolygon =
            RoundedPolygon.circle(
                numVertices = 36,
                radius = radius,
                centerX = centerX,
                centerY = centerY,
            )

        val endPolygon =
            RoundedPolygon.star(
                numVerticesPerRadius = 9,
                radius = radius,
                innerRadius = radius * 0.85f,
                rounding = CornerRounding(radius * ConfigConstants.MORPH_CORNER_ROUNDING_FACTOR),
                centerX = centerX,
                centerY = centerY,
            )

        val morph = Morph(startPolygon, endPolygon)
        val path = android.graphics.Path()
        morph.toPath(progress, path)

        val matrix = android.graphics.Matrix()
        matrix.postRotate(ConfigConstants.MORPH_ROTATION_DEGREES * progress, centerX, centerY)
        path.transform(matrix)

        return Outline.Generic(path.asComposePath())
    }
}

@Composable
private fun ColorPickerDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit,
) {
    val hsv = remember {
        val hsvArray = FloatArray(ConfigConstants.HSV_ARRAY_SIZE)
        android.graphics.Color.colorToHSV(initialColor.toArgb(), hsvArray)
        mutableStateOf(hsvArray)
    }

    var hue by remember { mutableFloatStateOf(hsv.value[0]) }
    var saturation by remember { mutableFloatStateOf(hsv.value[1]) }
    var value by remember { mutableFloatStateOf(hsv.value[2]) }
    val currentColor = remember(hue, saturation, value) { Color.hsv(hue, saturation, value) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "SELECTOR DE COLOR",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                )
                Spacer(modifier = Modifier.height(24.dp))
                SaturationValueBox(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onSaturationValueChange = { s, v ->
                        saturation = s
                        value = v
                    },
                )
                Spacer(modifier = Modifier.height(24.dp))
                HueSlider(hue = hue, onHueChange = { hue = it })
                Spacer(modifier = Modifier.height(24.dp))
                ColorPickerInfo(currentColor = currentColor)
                Spacer(modifier = Modifier.height(24.dp))
                ColorPickerActions(onDismiss = onDismiss, onConfirm = { onColorSelected(currentColor) })
            }
        }
    }
}

@Composable
private fun ColorPickerInfo(currentColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(40.dp)
                .background(currentColor, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "HEX: #${
                Integer.toHexString(currentColor.toArgb()).uppercase()
                    .takeLast(ConfigConstants.HEX_STRING_LENGTH)
            }",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ColorPickerActions(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        TextButton(onClick = onDismiss) { Text("CANCELAR") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onConfirm) { Text("SELECCIONAR") }
    }
}

@Composable
private fun SaturationValueBox(
    hue: Float,
    saturation: Float,
    value: Float,
    onSaturationValueChange: (Float, Float) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .aspectRatio(ConfigConstants.COLOR_PICKER_ASPECT_RATIO)
            .clip(RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectColorGestures { s, v -> onSaturationValueChange(s, v) }
            },
    ) {
        SaturationValueCanvas(hue = hue, saturation = saturation, value = value)
    }
}

private suspend fun androidx.compose.ui.input.pointer.PointerInputScope.detectColorGestures(
    onUpdate: (Float, Float) -> Unit,
) {
    coroutineScope {
        launch {
            this@detectColorGestures.detectTapGestures { offset ->
                onUpdate((offset.x / size.width).coerceIn(0f, 1f), 1f - (offset.y / size.height).coerceIn(0f, 1f))
            }
        }
        launch {
            this@detectColorGestures.detectDragGestures { change, _ ->
                onUpdate((change.position.x / size.width).coerceIn(0f, 1f), 1f - (change.position.y / size.height).coerceIn(0f, 1f))
            }
        }
    }
}

@Composable
private fun SaturationValueCanvas(hue: Float, saturation: Float, value: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val hsvColor = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
        drawRect(brush = Brush.horizontalGradient(colors = listOf(Color.White, Color(hsvColor))))
        drawRect(brush = Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black)))

        val x = saturation * size.width
        val y = (1f - value) * size.height
        drawCircle(
            color = Color.White,
            radius = 8.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(x, y),
            style = Stroke(width = 2.dp.toPx()),
        )
        drawCircle(
            color = if (value > 0.5f) Color.Black else Color.White,
            radius = 2.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(x, y),
        )
    }
}

@Composable
private fun HueSlider(
    hue: Float,
    onHueChange: (Float) -> Unit,
) {
    val hueColors =
        remember {
            List(ConfigConstants.HUE_LIST_SIZE) { Color.hsv(it.toFloat(), 1f, 1f) }
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val h = (offset.x / size.width * ConfigConstants.HUE_MAX)
                            .coerceIn(0f, ConfigConstants.HUE_MAX)
                        onHueChange(h)
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        val h = (change.position.x / size.width * ConfigConstants.HUE_MAX)
                            .coerceIn(0f, ConfigConstants.HUE_MAX)
                        onHueChange(h)
                    }
                },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.horizontalGradient(hueColors),
            )

            val x = hue / ConfigConstants.HUE_MAX * size.width
            drawCircle(
                color = Color.White,
                radius = 10.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(x, size.height / 2),
                style = Stroke(width = 2.dp.toPx()),
            )
        }
    }
}
