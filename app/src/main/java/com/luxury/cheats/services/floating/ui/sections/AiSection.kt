package com.luxury.cheats.services.floating.ui.sections

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.luxury.cheats.R

/**
 * Modelo de datos simple para representar cada mensaje de chat.
 */
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isTerminal: Boolean = false
)

/**
 * Paleta de colores Premium de la sección de IA, extraída del diseño de Figma.
 */
private val COLOR_AI_BUBBLE_BG = Color(0xFFEFEFEE)
private val COLOR_AI_TEXT = Color.Black
private val COLOR_USER_BUBBLE_BG = Color(0xFF358CFA)
private val COLOR_USER_TEXT = Color.White
private val COLOR_INPUT_BG = Color(0xFF1C1C1E)
private val COLOR_INPUT_BORDER = Color(0xFF2E2E30)
private val COLOR_PLACEHOLDER = Color(0xFF8E8E93)

// Textos pre-diseñados para la simulación interactiva
private const val USER_MESSAGE_1 = "Hola :D"
private const val USER_MESSAGE_2 = "Quiero que configures el Cheat para Free Fire"

/**
 * Composable que representa la sección de Chat con IA.
 * Utiliza una animación interactiva de tecleado guiado en lugar de teclado virtual.
 */
@Composable
fun AiSection() {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Inicializar y precargar el ayudante de sonido de forma anticipada
    DisposableEffect(Unit) {
        AiSoundHelper.init(context)
        onDispose {
            AiSoundHelper.release()
        }
    }

    // Lista mutable reactiva de mensajes de chat
    val messages = remember {
        mutableStateListOf(
            ChatMessage("Hola Que tal", isUser = false),
            ChatMessage("En que puedo ayudarte el dia de hoy :D", isUser = false)
        )
    }

    // Estados del motor de escritura
    var inputText by remember { mutableStateOf("") }
    var typingStep by remember { mutableIntStateOf(0) } // 0: Idle, 1: Escribiendo M1, 2: M1 Listo, 3: M1 Enviado, 4: Escribiendo M2, 5: M2 Listo, 6: Completado
    var isTypingAnimationActive by remember { mutableStateOf(false) }

    // Auto-scroll al final cuando la lista de mensajes cambie de tamaño
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    /**
     * Función que simula el efecto de tecleado letra por letra.
     */
    fun startTypingAnimation(targetText: String, onComplete: () -> Unit) {
        if (isTypingAnimationActive) return
        isTypingAnimationActive = true
        inputText = ""
        coroutineScope.launch {
            for (i in 1..targetText.length) {
                inputText = targetText.substring(0, i)
                
                // Determinar si el carácter actual es un espacio para usar el sonido de espacio (modifier)
                val isSpace = targetText[i - 1] == ' '
                
                // Reproducir el sonido de teclado por cada carácter tecleado (con soporte para espacio)
                AiSoundHelper.play(isSpace)

                // Retardo dinámico de tecleado humano (110 a 140 ms) para un sonido nítido y realista
                val humanDelay = (110..140).random().toLong()
                delay(humanDelay)
            }
            isTypingAnimationActive = false
            onComplete()
        }
    }

    /**
     * Ejecuta la acción cuando el usuario pulsa en el campo de texto o el botón enviar.
     */
    val handleInteraction: () -> Unit = {
        if (!isTypingAnimationActive && typingStep == 0) {
            coroutineScope.launch {
                // 1. Iniciar escritura del primer mensaje
                typingStep = 1
                startTypingAnimation(USER_MESSAGE_1) {
                    // Al finalizar la escritura del mensaje 1, se envía automáticamente
                    messages.add(ChatMessage(USER_MESSAGE_1, isUser = true))
                    AiSoundHelper.playSent()
                    inputText = ""
                    typingStep = 2
                }
                
                // Esperar a que la primera animación termine
                while (isTypingAnimationActive) {
                    delay(50)
                }
                
                // 2. Pausa realista de 1.2 segundos para simular al usuario pensando / leyendo
                delay(1200)
                
                // 3. Iniciar escritura del segundo mensaje
                typingStep = 3
                startTypingAnimation(USER_MESSAGE_2) {
                    // Al finalizar la escritura del mensaje 2, se envía automáticamente
                    messages.add(ChatMessage(USER_MESSAGE_2, isUser = true))
                    AiSoundHelper.playSent()
                    inputText = ""
                    typingStep = 4
                }
                
                // Esperar a que la segunda animación termine
                while (isTypingAnimationActive) {
                    delay(50)
                }
                
                // 4. Pausa de lectura de la IA de 1.5 segundos
                delay(1500)
                
                // 5. La IA responde confirmando el inicio de la configuración
                messages.add(ChatMessage("¡Entendido! 🚀 Iniciando configuracion de Luxury Cheats...", isUser = false))
                AiSoundHelper.playReceived()
                
                // Pausa de lectura antes del terminal
                delay(1200)

                // 6. ¡Aparición de la Consola de Hacker Interactiva!
                val terminalMessage = ChatMessage("", isUser = false, isTerminal = true)
                messages.add(terminalMessage)
                AiSoundHelper.playReceived()
                val terminalIndex = messages.size - 1

                val terminalLines = listOf(
                    "[+] Connecting to Free Fire server... SUCCESS",
                    "[+] Hooking library libil2cpp.so... DONE",
                    "[+] Bypassing Anti-Cheat engine... OK",
                    "[+] Patching memory offset 0x7F8E09B4...",
                    "[+] Injecting Premium Aimbot... ACTIVE",
                    "[+] Configuración completa! 100%"
                )

                var currentLogs = ""
                for (line in terminalLines) {
                    currentLogs += if (currentLogs.isEmpty()) line else "\n$line"
                    messages[terminalIndex] = ChatMessage(currentLogs, isUser = false, isTerminal = true)
                    
                    // Asegurar auto-scroll continuo al final conforme se imprimen las líneas de la consola
                    try {
                        listState.animateScrollToItem(messages.size - 1)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    
                    // Emitir sonido táctil rápido para simular la impresión de comandos hacker
                    AiSoundHelper.play(isSpace = false)
                    
                    // Retardo rápido y fluido por línea de consola
                    delay(550)
                }

                // Pausa para que el usuario admire la consola completada
                delay(1500)

                // 7. Mensaje final de éxito
                messages.add(ChatMessage("Se ha configurado correctamente el Aimbot táctico y el bypass de seguridad de forma exitosa. ¡A disfrutar Free Fire! 🔥🎮", isUser = false))
                AiSoundHelper.playReceived()
                typingStep = 5
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. LISTA DE MENSAJES CON SCROLL AUTO
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(messages) { index, message ->
                val nextMessage = messages.getOrNull(index + 1)
                val isLastInGroup = nextMessage == null || nextMessage.isUser != message.isUser
                BubbleMessage(message = message, isLastInGroup = isLastInGroup)
            }
        }

        // 2. CAJA DE ENTRADA INFERIOR (Tocar simula tecleado)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(COLOR_INPUT_BG)
                .border(1.dp, COLOR_INPUT_BORDER, RoundedCornerShape(24.dp))
                .clickable(
                    enabled = typingStep == 0 && !isTypingAnimationActive,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = handleInteraction
                )
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Placeholder / Texto Tecleado
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (inputText.isEmpty() && !isTypingAnimationActive) {
                    Text(
                        text = "Escribe tu mensaje ...",
                        color = COLOR_PLACEHOLDER,
                        fontSize = 14.sp
                    )
                } else {
                    val scrollState = rememberScrollState()
                    LaunchedEffect(inputText) {
                        scrollState.scrollTo(scrollState.maxValue)
                    }
                    Text(
                        text = inputText,
                        color = Color.White,
                        fontSize = 14.sp,
                        maxLines = 1,
                        modifier = Modifier.horizontalScroll(scrollState)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botón de Enviar Circular con Ícono
            val isButtonActive = typingStep == 0 && !isTypingAnimationActive
            val buttonColor = if (isButtonActive) COLOR_USER_BUBBLE_BG else COLOR_USER_BUBBLE_BG.copy(alpha = 0.5f)

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(buttonColor)
                    .clickable(
                        enabled = isButtonActive,
                        onClick = handleInteraction
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Enviar",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun BubbleMessage(message: ChatMessage, isLastInGroup: Boolean) {
    if (message.isTerminal) {
        // Renderizado personalizado de la terminal / consola interactiva al estilo Luxury Hack
        val density = LocalDensity.current
        val scale = remember { Animatable(0.6f) }
        val alpha = remember { Animatable(0f) }
        val offsetY = remember { Animatable(16f) }

        LaunchedEffect(Unit) {
            launch {
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 200)
                )
            }
            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
        }

        val scaleVal by scale.asState()
        val alphaVal by alpha.asState()
        val offsetYVal by offsetY.asState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .graphicsLayer(
                    scaleX = scaleVal,
                    scaleY = scaleVal,
                    alpha = alphaVal,
                    translationY = density.run { offsetYVal.dp.toPx() },
                    transformOrigin = TransformOrigin(0f, 0.5f)
                ),
            horizontalAlignment = Alignment.Start
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0D0E11))
                    .border(1.dp, Color(0xFF00FF66).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                // Cabecera superior estilo Terminal MacOS (Window Chrome)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "luxury_console.sh",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }

                // Cuerpo de salida de códigos de la Terminal
                Text(
                    text = message.text,
                    color = Color(0xFF00FF66), // Color verde matriz clásico
                    fontSize = 11.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    lineHeight = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        return
    }

    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val bubbleBg = if (message.isUser) COLOR_USER_BUBBLE_BG else COLOR_AI_BUBBLE_BG
    val textColor = if (message.isUser) COLOR_USER_TEXT else COLOR_AI_TEXT
    val density = LocalDensity.current

    // Animaciones para simular la entrada fluida y elástica estilo iMessage / Telegram
    val scale = remember { Animatable(0.6f) }
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(16f) } // Entrada desde abajo en dp

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy, // Rebote fluido y elástico
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200)
            )
        }
        launch {
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    val scaleVal by scale.asState()
    val alphaVal by alpha.asState()
    val offsetYVal by offsetY.asState()

    // El pivote se alinea perfectamente con la colita para que "explote" desde la colita del mensaje
    val transformOrigin = remember(message.isUser) {
        if (message.isUser) {
            TransformOrigin(1f, 1f) // Colita abajo-derecha
        } else {
            TransformOrigin(0f, 1f) // Colita abajo-izquierda
        }
    }

    // Utiliza nuestra forma personalizada de colita curva estilo iOS
    val shape = remember(message.isUser, isLastInGroup) {
        IosBubbleShape(isUser = message.isUser, hasTail = isLastInGroup)
    }

    // Margen interno asimétrico para compensar el inicio de la colita y centrar perfectamente el texto sin recortarlo
    val contentPadding = remember(message.isUser) {
        if (message.isUser) {
            androidx.compose.foundation.layout.PaddingValues(
                start = 12.dp,
                end = 16.dp, // Más espacio en la colita derecha
                top = 10.dp,
                bottom = 10.dp
            )
        } else {
            androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, // Más espacio en la colita izquierda
                end = 12.dp,
                top = 10.dp,
                bottom = 10.dp
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(
                scaleX = scaleVal,
                scaleY = scaleVal,
                alpha = alphaVal,
                translationY = density.run { offsetYVal.dp.toPx() },
                transformOrigin = transformOrigin
            ),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 210.dp) // Limitar ancho para que respire con el nuevo padding horizontal de la sección
                .clip(shape)
                .background(bubbleBg)
                .padding(contentPadding)
        ) {
            Text(
                text = message.text,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp
            )
        }
    }
}

/**
 * Forma personalizada que dibuja con absoluta fidelidad la colita de mensaje curvada de iOS (1:1).
 */
class IosBubbleShape(private val isUser: Boolean, private val hasTail: Boolean) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        if (!hasTail) {
            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(0f, 0f, size.width, size.height),
                        cornerRadius = CornerRadius(density.run { 18.dp.toPx() })
                    )
                )
            }
            return Outline.Generic(path)
        }

        val path = Path().apply {
            val radius = density.run { 18.dp.toPx() }
            val width = size.width
            val height = size.height

            if (!isUser) {
                // Mensaje de IA (Izquierda) con colita curvada clásica de iOS
                reset()
                val tailCurveWidth = density.run { 6.dp.toPx() } // Ancho reservado para la colita
                
                // Mover al inicio superior con holgura para la colita
                moveTo(radius + tailCurveWidth, 0f)
                lineTo(width - radius, 0f)
                
                // Esquina superior derecha
                quadraticTo(width, 0f, width, radius)
                lineTo(width, height - radius)
                
                // Esquina inferior derecha
                quadraticTo(width, height, width - radius, height)
                
                // Base del mensaje hasta el inicio de la colita
                lineTo(radius + tailCurveWidth, height)
                
                // Curva exterior de la colita que desciende a la punta (0f, height)
                // Usamos Bezier cúbico para emular el flujo natural y curvo de Apple
                cubicTo(
                    radius + tailCurveWidth - density.run { 4.dp.toPx() }, height,
                    tailCurveWidth, height + density.run { 0.5f.dp.toPx() },
                    0f, height
                )
                
                // Curva interior que asciende de regreso a la pared izquierda
                cubicTo(
                    tailCurveWidth - density.run { 1.dp.toPx() }, height - density.run { 2.dp.toPx() },
                    tailCurveWidth, height - density.run { 5.dp.toPx() },
                    tailCurveWidth, height - density.run { 10.dp.toPx() }
                )
                
                // Pared izquierda a partir de la colita
                lineTo(tailCurveWidth, radius)
                
                // Esquina superior izquierda
                quadraticTo(tailCurveWidth, 0f, radius + tailCurveWidth, 0f)
                close()
            } else {
                // Mensaje de Usuario (Derecha) con colita curvada clásica de iOS
                reset()
                val tailCurveWidth = density.run { 6.dp.toPx() } // Ancho reservado para la colita
                val bubbleRightWall = width - tailCurveWidth
                
                // Esquina superior izquierda
                moveTo(radius, 0f)
                lineTo(bubbleRightWall - radius, 0f)
                
                // Esquina superior derecha
                quadraticTo(bubbleRightWall, 0f, bubbleRightWall, radius)
                
                // Pared derecha hasta el inicio de la colita
                lineTo(bubbleRightWall, height - density.run { 10.dp.toPx() })
                
                // Curva interior que desciende de regreso a la punta derecha (width, height)
                cubicTo(
                    bubbleRightWall, height - density.run { 5.dp.toPx() },
                    bubbleRightWall + density.run { 1.dp.toPx() }, height - density.run { 2.dp.toPx() },
                    width, height
                )
                
                // Curva exterior de la colita que sube a la base
                cubicTo(
                    bubbleRightWall, height + density.run { 0.5f.dp.toPx() },
                    bubbleRightWall + density.run { 4.dp.toPx() }, height,
                    bubbleRightWall - radius, height
                )
                
                // Esquina inferior izquierda
                lineTo(radius, height)
                quadraticTo(0f, height, 0f, height - radius)
                
                // Pared izquierda
                lineTo(0f, radius)
                quadraticTo(0f, 0f, radius, 0f)
                close()
            }
        }
        return Outline.Generic(path)
    }
}

/**
 * Ayudante de audio Singleton para precargar y reproducir el sonido del teclado en el flujo de Multimedia (Stream de Media).
 * Precarga un "pool" de reproductores en memoria durante la inicialización para garantizar una respuesta táctil
 * instantánea y de latencia cero, evitando instanciaciones tardías en tiempo de ejecución.
 */
object AiSoundHelper {
    private val playersNormal = java.util.concurrent.CopyOnWriteArrayList<android.media.MediaPlayer>()
    private val playersSpace = java.util.concurrent.CopyOnWriteArrayList<android.media.MediaPlayer>()
    private var playerSent: android.media.MediaPlayer? = null
    private var playerReceived: android.media.MediaPlayer? = null
    private var currentIndexNormal = 0
    private var currentIndexSpace = 0

    fun init(context: android.content.Context) {
        if (playersNormal.isNotEmpty() || playersSpace.isNotEmpty()) return // Ya precargado
        
        // Precargar 5 instancias normales para permitir superposición de clics de tecla súper fluida
        for (i in 0 until 5) {
            try {
                val mp = android.media.MediaPlayer.create(context, R.raw.keypressclick)
                if (mp != null) {
                    mp.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC)
                    playersNormal.add(mp)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Precargar 3 instancias especiales para la barra espaciadora (modifier)
        for (i in 0 until 3) {
            try {
                val mp = android.media.MediaPlayer.create(context, R.raw.keypressespacio)
                if (mp != null) {
                    mp.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC)
                    playersSpace.add(mp)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Precargar reproductor de mensaje enviado
        try {
            playerSent = android.media.MediaPlayer.create(context, R.raw.sentmessage).apply {
                setAudioStreamType(android.media.AudioManager.STREAM_MUSIC)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Precargar reproductor de mensaje recibido
        try {
            playerReceived = android.media.MediaPlayer.create(context, R.raw.receivedmessage).apply {
                setAudioStreamType(android.media.AudioManager.STREAM_MUSIC)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play(isSpace: Boolean = false) {
        val pool = if (isSpace) playersSpace else playersNormal
        if (pool.isEmpty()) return
        
        try {
            val index = if (isSpace) currentIndexSpace else currentIndexNormal
            val mp = pool[index]
            
            // Rotar el índice del pool correspondiente
            if (isSpace) {
                currentIndexSpace = (currentIndexSpace + 1) % pool.size
            } else {
                currentIndexNormal = (currentIndexNormal + 1) % pool.size
            }
            
            if (mp.isPlaying) {
                mp.pause()
            }
            mp.seekTo(0)
            mp.start()
        } catch (e: Exception) {
            // Intento de recuperación directa ante buffers inestables
            try {
                val index = if (isSpace) currentIndexSpace else currentIndexNormal
                val mp = pool[index]
                mp.seekTo(0)
                mp.start()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun playSent() {
        try {
            val mp = playerSent ?: return
            if (mp.isPlaying) {
                mp.pause()
            }
            mp.seekTo(0)
            mp.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playReceived() {
        try {
            val mp = playerReceived ?: return
            if (mp.isPlaying) {
                mp.pause()
            }
            mp.seekTo(0)
            mp.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        for (mp in playersNormal) {
            try {
                mp.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        for (mp in playersSpace) {
            try {
                mp.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try {
            playerSent?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            playerReceived?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        playersNormal.clear()
        playersSpace.clear()
        playerSent = null
        playerReceived = null
        currentIndexNormal = 0
        currentIndexSpace = 0
    }
}
