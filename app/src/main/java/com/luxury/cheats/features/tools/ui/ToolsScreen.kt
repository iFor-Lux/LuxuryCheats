package com.luxury.cheats.features.tools.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.IntOffset
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
import com.luxury.cheats.features.tools.logic.ToolsViewModel


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ToolsScreen(viewModel: ToolsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    MaterialExpressiveTheme(
        motionScheme = MotionScheme.expressive()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            FloatingConfigSection(
                uiState = uiState,
                onAction = { viewModel.onAction(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FloatingConfigSection(
    uiState: ToolsState,
    onAction: (ToolsAction) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "ArrowRotation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                MaterialTheme.shapes.extraLarge
            )
            .padding(16.dp)
    ) {
        // Cabecera Desplegable
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { isExpanded = !isExpanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "PERSONALIZAR WIDGET",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.rotate(rotation)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp.toFloat()
                val screenHeight = configuration.screenHeightDp.toFloat()

                // --- ANCHO (Mapeo 0-100 -> 50-400) ---
                val widthPercent = ((uiState.floatingWidth - 50f) / (400f - 50f) * 100f).coerceIn(0f, 100f)
                ConfigSlider(
                    label = "ANCHO",
                    valueText = "${widthPercent.toInt()}",
                    value = widthPercent,
                    range = 0f..100f,
                    onValueChange = { percent ->
                        val actualWidth = 50 + (percent / 100f * (400 - 50))
                        onAction(ToolsAction.UpdateFloatingConfig(width = actualWidth.toInt()))
                    }
                )

                // --- ALTO (Mapeo 0-100 -> 5-50) ---
                val heightPercent = ((uiState.floatingHeight - 5f) / (50f - 5f) * 100f).coerceIn(0f, 100f)
                ConfigSlider(
                    label = "ALTO",
                    valueText = "${heightPercent.toInt()}",
                    value = heightPercent,
                    range = 0f..100f,
                    onValueChange = { percent ->
                        val actualHeight = 5 + (percent / 100f * (50 - 5))
                        onAction(ToolsAction.UpdateFloatingConfig(height = actualHeight.toInt()))
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )

                // --- POSICIÓN X (Mapeo 0-100 -> 0-screenWidth) ---
                val xPercent = (uiState.floatingCenterX / screenWidth * 100f).coerceIn(0f, 100f)
                ConfigSlider(
                    label = "CENTRO X (HORIZONTAL)",
                    valueText = "${xPercent.toInt()}",
                    value = xPercent,
                    range = 0f..100f,
                    onValueChange = { percent ->
                        val actualX = (percent / 100f * screenWidth)
                        onAction(ToolsAction.UpdateFloatingConfig(centerX = actualX.toInt()))
                    }
                )

                // --- POSICIÓN Y (Mapeo 0-100 -> 0-screenHeight) ---
                val yPercent = (uiState.floatingCenterY / screenHeight * 100f).coerceIn(0f, 100f)
                ConfigSlider(
                    label = "CENTRO Y (VERTICAL)",
                    valueText = "${yPercent.toInt()}",
                    value = yPercent,
                    range = 0f..100f,
                    onValueChange = { percent ->
                        val actualY = (percent / 100f * screenHeight)
                        onAction(ToolsAction.UpdateFloatingConfig(centerY = actualY.toInt()))
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )

                // --- STROKE ENABLED ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "BORDE (STROKE)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Efecto exterior alrededor del widget",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = uiState.isFloatingStrokeEnabled,
                        onCheckedChange = { onAction(ToolsAction.UpdateFloatingConfig(isStrokeEnabled = it)) }
                    )
                }

                if (uiState.isFloatingStrokeEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    // --- GROSOR (Rango real 0-10 con puntos discretos) ---
                    ConfigSlider(
                        label = "GROSOR DEL BORDE",
                        valueText = "${uiState.floatingStrokeWidth.toInt()}",
                        value = uiState.floatingStrokeWidth,
                        range = 0f..8f,
                        steps = 7,
                        onValueChange = { onAction(ToolsAction.UpdateFloatingConfig(strokeWidth = it)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "COLOR DEL BORDE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    StrokeColorSelector(
                        selectedColor = uiState.floatingStrokeColor,
                        onColorSelected = { onAction(ToolsAction.UpdateFloatingConfig(strokeColor = it)) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StrokeColorSelector(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit
) {
    var showColorPicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val colors = listOf(
        Triple("Dinámico", 0L, MaterialTheme.colorScheme.primary),
        Triple("Negro", 0xFF000000, Color.Black),
        Triple("Azul", 0xFF2196F3, Color(0xFF2196F3)),
        Triple("Amarillo", 0xFFFFFF00, Color.Yellow),
        Triple("Gris", 0xFF757575, Color.Gray),
        Triple("Blanco", 0xFFFFFFFF, Color.White),
        Triple("Personalizar", -1L, Color.Transparent) // -1L para identificar el personalizado
    )

    if (showColorPicker) {
        ColorPickerDialog(
            initialColor = if (selectedColor == 0L) Color.White else Color(selectedColor),
            onColorSelected = {
                onColorSelected(it.toArgb().toLong())
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(colors) { (name, value, color) ->
            val isSelected = selectedColor == value

            // Animación del progreso de morphing: 0f = Círculo, 1f = Cookie9Sided
            val morphProgress by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0f,
                animationSpec = spring(
                    dampingRatio = 0.7f,
                    stiffness = 500f
                ),
                label = "ColorMorphProgress"
            )

            // Usamos MorphingShape para una transición fluida
            val shape = remember(morphProgress) {
                DynamicMorphingShape(morphProgress)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(72.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null // Elimina la sombra/ripple
                    ) {
                        if (value == -1L) {
                            showColorPicker = true
                        } else {
                            onColorSelected(value)
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (value == 0L) MaterialTheme.colorScheme.primary
                            else if (value == -1L) {
                                // Gradiente para el icono de personalizado
                                Color.Transparent
                            } else color,
                            shape
                        )
                        .then(
                            if (value == -1L) {
                                Modifier.border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                    shape = shape
                                )
                            } else Modifier
                        )
                        .then(
                            if (isSelected || (value == -1L && selectedColor != 0L && colors.none { it.second == selectedColor })) {
                                Modifier.border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                    shape = shape
                                )
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected || (value == -1L && selectedColor != 0L && colors.none { it.second == selectedColor })) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            tint = if (isSelected && (color == Color.White || value == 0xFFFFFFFF)) Color.Black else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (value == -1L) {
                        // Círculo pequeño para mostrar que es personalizado
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    if (selectedColor != 0L && colors.none { it.second == selectedColor })
                                        Color(selectedColor)
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                                    CircleShape
                                )
                                .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add, // Usar un "+" en lugar de emoji
                                contentDescription = null,
                                tint = if (selectedColor != 0L && colors.none { it.second == selectedColor })
                                    (if (Color(selectedColor).luminance() > 0.5f) Color.Black else Color.White)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = name,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Shape que hace morphing entre un Círculo y un Cookie de 9 lados.
 */
private class DynamicMorphingShape(private val progress: Float) : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radius = minOf(size.width, size.height) / 2f
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        // Forma inicial: Círculo
        val startPolygon = RoundedPolygon.circle(
            numVertices = 36, // Un círculo suave
            radius = radius,
            centerX = centerX,
            centerY = centerY
        )

        // Forma final: Cookie de 9 lados (usamos star con innerRadius alto)
        val endPolygon = RoundedPolygon.star(
            numVerticesPerRadius = 9,
            radius = radius,
            innerRadius = radius * 0.85f,
            rounding = CornerRounding(radius * 0.3f),
            centerX = centerX,
            centerY = centerY
        )

        // Realizamos el morphing
        val morph = Morph(startPolygon, endPolygon)
        val path = android.graphics.Path()
        morph.toPath(progress, path)

        // Rotar un poco para que se vea más dinámico
        val matrix = android.graphics.Matrix()
        matrix.postRotate(-90f * progress, centerX, centerY)
        path.transform(matrix)

        return Outline.Generic(path.asComposePath())
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ConfigSlider(
    label: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (Float) -> Unit
) {
    // Usamos un estado local para que el slider sea instantáneo
    var sliderValue by remember(value) { mutableStateOf(value) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valueText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Solo mostramos el punto magnético si NO hay pasos predeterminados
            if (steps == 0) {
                val isCentered = sliderValue in 48f..52f
                val dotSize by animateDpAsState(
                    targetValue = if (isCentered) 10.dp else 6.dp,
                    animationSpec = spring(
                        dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                        stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                    )
                )

                // Punto indicador del medio (Visual + Magnetismo dinámico)
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .background(
                            color = if (isCentered)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                )
            }

            Slider(
                value = sliderValue,
                onValueChange = { newValue ->
                    if (steps == 0) {
                        // Lógica del imán solo para desplazamiento libre
                        val snappedValue = if (newValue in 47f..53f) 50f else newValue
                        sliderValue = snappedValue
                        onValueChange(snappedValue)
                    } else {
                        sliderValue = newValue
                        onValueChange(newValue)
                    }
                },
                valueRange = range,
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    activeTickColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                    inactiveTickColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val hsv = remember {
        val hsvArray = FloatArray(3)
        android.graphics.Color.colorToHSV(initialColor.toArgb(), hsvArray)
        mutableStateOf(hsvArray)
    }

    var hue by remember { mutableFloatStateOf(hsv.value[0]) }
    var saturation by remember { mutableFloatStateOf(hsv.value[1]) }
    var value by remember { mutableFloatStateOf(hsv.value[2]) }

    val currentColor = remember(hue, saturation, value) {
        Color.hsv(hue, saturation, value)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SELECTOR DE COLOR",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Cuadro de Saturación y Valor (Visual)
                SaturationValueBox(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onSaturationValueChange = { s, v ->
                        saturation = s
                        value = v
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Barra de Tono (Hue Slider)
                HueSlider(hue = hue, onHueChange = { hue = it })

                Spacer(modifier = Modifier.height(24.dp))

                // Vista previa y HEX
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(currentColor, CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "HEX: #${Integer.toHexString(currentColor.toArgb()).uppercase().takeLast(6)}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCELAR")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onColorSelected(currentColor) }) {
                        Text("SELECCIONAR")
                    }
                }
            }
        }
    }
}

@Composable
fun SaturationValueBox(
    hue: Float,
    saturation: Float,
    value: Float,
    onSaturationValueChange: (Float, Float) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .clip(RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val s = (offset.x / size.width).coerceIn(0f, 1f)
                    val v = 1f - (offset.y / size.height).coerceIn(0f, 1f)
                    onSaturationValueChange(s, v)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val s = (change.position.x / size.width).coerceIn(0f, 1f)
                    val v = 1f - (change.position.y / size.height).coerceIn(0f, 1f)
                    onSaturationValueChange(s, v)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val hsvColor = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))

            // Gradiente horizontal (Saturación)
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.White, Color(hsvColor))
                )
            )

            // Gradiente vertical (Valor/Brillo)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black)
                )
            )

            // Selector (Círculo)
            val x = saturation * size.width
            val y = (1f - value) * size.height
            drawCircle(
                color = Color.White,
                radius = 8.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(x, y),
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = if (value > 0.5f) Color.Black else Color.White,
                radius = 2.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }
    }
}

@Composable
fun HueSlider(hue: Float, onHueChange: (Float) -> Unit) {
    val hueColors = remember {
        List(361) { Color.hsv(it.toFloat(), 1f, 1f) }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .clip(CircleShape)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onHueChange((offset.x / size.width * 360f).coerceIn(0f, 360f))
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    onHueChange((change.position.x / size.width * 360f).coerceIn(0f, 360f))
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.horizontalGradient(hueColors)
            )

            // Selector de tono
            val x = (hue / 360f) * size.width
            drawCircle(
                color = Color.White,
                radius = 10.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(x, size.height / 2),
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}
