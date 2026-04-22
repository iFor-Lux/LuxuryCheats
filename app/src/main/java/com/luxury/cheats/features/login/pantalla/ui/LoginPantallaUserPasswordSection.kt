package com.luxury.cheats.features.login.pantalla.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.EditCommand
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.PlatformTextInputService
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TextInputService
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.core.theme.LuxuryCheatsTheme
import com.luxury.cheats.features.login.pantalla.logic.LoginCredentials
import com.luxury.cheats.features.login.pantalla.logic.LoginDisplayOptions
import com.luxury.cheats.features.login.pantalla.logic.LoginField
import com.luxury.cheats.features.login.pantalla.logic.LoginInteractionState
import com.luxury.cheats.features.login.teclado.logic.TecladoType



/**
 * Acciones para los campos de login.
 */

data class LoginFieldActions(
    val onInteractionStateChange: (LoginInteractionState) -> Unit,
    val onTecladoTypeChange: (TecladoType) -> Unit,
)

/**
 * Estado de visualización de los campos de login.
 * Agrupa el estado de interacción y el tipo de teclado.
 */
data class LoginDisplayState(
    val interactionState: LoginInteractionState,
    val tecladoType: TecladoType,
)

/**
 * Sección que contiene los campos de usuario, contraseña y la opción de guardar sesión.
 */
@Composable
fun LoginPantallaUserPasswordSection(
    credentials: LoginCredentials,
    options: LoginDisplayOptions,
    displayState: LoginDisplayState,
    actions: LoginFieldActions,
    modifier: Modifier = Modifier,
) {

    val isExpanded = displayState.interactionState == LoginInteractionState.EXPANDED
    val secondaryAlpha by animateFloatAsState(
        targetValue = if (isExpanded) 0f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "secondaryAlpha",
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CompositionLocalProvider(LocalTextInputService provides createDummyTextInputService()) {
            LoginInputFields(credentials, isExpanded, actions)
        }

        Column(
            modifier = Modifier.graphicsLayer { alpha = secondaryAlpha },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (credentials.isLicenseMode) "¿Usar Usuario y Contraseña?" else "¿Tienes una Licencia?",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (!isExpanded) credentials.onLicenseModeToggle(!credentials.isLicenseMode)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
            LoginRememberMeRow(
                saveUser = options.saveUser,
                onSaveUserChange = { if (!isExpanded) options.onSaveUserChange(it) },
                interactionSource = remember { MutableInteractionSource() },
            )

            if (credentials.isLicenseMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Generar licencia GRATIS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!isExpanded) credentials.onGetLicenseClick()
                    }
                )
            }


            
            Spacer(modifier = Modifier.height(8.dp))
            LoginDebugBox(credentials.debugMessage)
        }
    }
}


@Composable
private fun LoginInputFields(
    credentials: LoginCredentials,
    isExpanded: Boolean,
    actions: LoginFieldActions,
) {
    val focused = credentials.focusedField
    val isAnyFocused = focused != LoginField.NONE

    if (credentials.isLicenseMode) {
        val alpha by animateFloatAsState(
            targetValue = if (!isAnyFocused || focused == LoginField.LICENSE) 1f else 0f,
            label = "licenseAlpha"
        )
        LoginLicenseField(
            value = credentials.licenseKey,
            onValueChange = credentials.onLicenseChange,
            onFocusChanged = {
                if (it) {
                    actions.onInteractionStateChange(LoginInteractionState.EXPANDED)
                    actions.onTecladoTypeChange(TecladoType.ALPHABETIC)
                    credentials.onFocusFieldChange(LoginField.LICENSE)
                }
            },
            modifier = Modifier.alpha(alpha)
        )
    } else {
        val userAlpha by animateFloatAsState(
            targetValue = if (!isAnyFocused || focused == LoginField.USERNAME) 1f else 0f,
            label = "userAlpha"
        )
        val passAlpha by animateFloatAsState(
            targetValue = if (!isAnyFocused || focused == LoginField.PASSWORD) 1f else 0f,
            label = "passAlpha"
        )

        LoginUserField(
            value = credentials.username,
            onValueChange = credentials.onUsernameChange,
            onFocusChanged = {
                if (it) {
                    actions.onInteractionStateChange(LoginInteractionState.EXPANDED)
                    actions.onTecladoTypeChange(TecladoType.ALPHABETIC)
                    credentials.onFocusFieldChange(LoginField.USERNAME)
                }
            },
            modifier = Modifier.alpha(userAlpha)
        )

        if (!isAnyFocused) {
            Spacer(modifier = Modifier.height(8.dp))
        }

        LoginPasswordField(
            value = credentials.password,
            onValueChange = credentials.onPasswordChange,
            onFocusChanged = {
                if (it) {
                    actions.onInteractionStateChange(LoginInteractionState.EXPANDED)
                    actions.onTecladoTypeChange(TecladoType.NUMERIC)
                    credentials.onFocusFieldChange(LoginField.PASSWORD)
                }
            },
            modifier = Modifier.alpha(passAlpha)
        )
    }

}




@Composable
private fun createDummyTextInputService(): TextInputService {
    return remember {
        object : TextInputService(
            object : PlatformTextInputService {
                override fun startInput(
                    v: TextFieldValue,
                    i: ImeOptions,
                    onE: (List<EditCommand>) -> Unit,
                    onI: (ImeAction) -> Unit,
                ) {
                    // No interactúa con el teclado real del sistema
                }

                override fun stopInput() {
                    // No interactúa con el teclado real del sistema
                }

                override fun showSoftwareKeyboard() {
                    // No interactúa con el teclado real del sistema
                }

                override fun hideSoftwareKeyboard() {
                    // No interactúa con el teclado real del sistema
                }

                override fun updateState(
                    o: TextFieldValue?,
                    n: TextFieldValue,
                ) {
                    // No interactúa con el teclado real del sistema
                }
            },
        ) {}
    }
}

@Composable
private fun BlinkingCursor() {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    Box(
        modifier = Modifier
            .size(2.dp, 20.dp)
            .graphicsLayer { this.alpha = alpha }
            .background(MaterialTheme.colorScheme.primary)
    )
}


@Composable
private fun LoginLicenseField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var isFocused by remember { mutableStateOf(false) }
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    Column(modifier = modifier) {
        Box(modifier = Modifier.width(210.dp)) {
            Text(
                text = "Clave de Licencia",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            onTextLayout = { textLayoutResult = it },
            modifier =
                Modifier
                    .width(210.dp)
                    .height(35.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        isFocused = it.isFocused
                        if (it.isFocused) onFocusChanged(true)
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            focusRequester.requestFocus()
                            textLayoutResult?.let { layout ->
                                val position = layout.getOffsetForPosition(offset)
                                onValueChange(value.copy(selection = androidx.compose.ui.text.TextRange(position)))
                            }
                        }
                    }
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp)),
            readOnly = true,
            textStyle =
                TextStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                ),
            cursorBrush = SolidColor(androidx.compose.ui.graphics.Color.Transparent),
            decorationBox = { innerTextField ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            innerTextField()

                            if (isFocused) {
                                textLayoutResult?.let { layout ->
                                    val offset = value.selection.max.coerceIn(0, layout.layoutInput.text.length)
                                    val cursorRect = layout.getCursorRect(offset)
                                    val density = LocalDensity.current
                                    val xOffset = with(density) { cursorRect.left.toDp() }

                                    Box(modifier = Modifier.offset(x = xOffset)) {
                                        BlinkingCursor()
                                    }
                                }
                            }
                        }

                        IconButton(
                            onClick = {
                                clipboardManager.getText()?.let { annotatedString ->
                                    onValueChange(TextFieldValue(text = annotatedString.text))
                                }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.ContentPaste,
                                contentDescription = "Pegar licencia",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            },
        )
    }
}


@Composable
private fun LoginUserField(

    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Box(modifier = Modifier.width(210.dp)) {
            Text(
                text = "Usuario",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            onTextLayout = { textLayoutResult = it },
            modifier =
                Modifier
                    .width(210.dp)
                    .height(35.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        isFocused = it.isFocused
                        if (it.isFocused) onFocusChanged(true)
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            focusRequester.requestFocus()
                            textLayoutResult?.let { layout ->
                                val position = layout.getOffsetForPosition(offset)
                                onValueChange(value.copy(selection = androidx.compose.ui.text.TextRange(position)))
                            }
                        }
                    }
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp)),
            readOnly = true,
            textStyle =
                TextStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                ),
            cursorBrush = SolidColor(androidx.compose.ui.graphics.Color.Transparent),
            decorationBox = { innerTextField ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    innerTextField()

                    if (isFocused) {
                        textLayoutResult?.let { layout ->
                            // Aseguramos que el offset esté dentro de los límites del layout actual
                            val offset = value.selection.max.coerceIn(0, layout.layoutInput.text.length)
                            val cursorRect = layout.getCursorRect(offset)
                            val density = LocalDensity.current
                            val xOffset = with(density) { cursorRect.left.toDp() }

                            Box(modifier = Modifier.offset(x = xOffset)) {
                                BlinkingCursor()
                            }
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun PasswordFieldLabel() {
    Box(modifier = Modifier.width(210.dp)) {
        Text(
            text = "Contraseña",
            fontSize = 12.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LoginPasswordField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    var passwordVisible by remember { mutableStateOf(false) }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        PasswordFieldLabel()
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier =
                Modifier
                    .width(210.dp)
                    .height(35.dp),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                onTextLayout = { textLayoutResult = it },
                modifier =
                    Modifier
                        .fillMaxSize()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            isFocused = it.isFocused
                            if (it.isFocused) onFocusChanged(true)
                        }
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                focusRequester.requestFocus()
                                textLayoutResult?.let { layout ->
                                    val position = layout.getOffsetForPosition(offset)
                                    onValueChange(value.copy(selection = androidx.compose.ui.text.TextRange(position)))
                                }
                            }
                        },
                readOnly = true,
                textStyle =
                    TextStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                    ),
                cursorBrush = SolidColor(androidx.compose.ui.graphics.Color.Transparent),
                visualTransformation =
                    if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                decorationBox = { innerTextField ->
                    PasswordTextFieldDecoration(
                        innerTextField = innerTextField,
                        passwordVisible = passwordVisible,
                        onPasswordVisibleChange = { passwordVisible = it },
                        isFocused = isFocused,
                        textLayoutResult = textLayoutResult,
                        selection = value.selection
                    )
                },
            )
        }
    }
}

@Composable
private fun PasswordTextFieldDecoration(
    innerTextField: @Composable () -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    isFocused: Boolean,
    textLayoutResult: TextLayoutResult?,
    selection: androidx.compose.ui.text.TextRange
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f)) {
                innerTextField()

                if (isFocused) {
                    textLayoutResult?.let { layout ->
                        // Aseguramos que el offset esté dentro de los límites del layout actual
                        val offset = selection.max.coerceIn(0, layout.layoutInput.text.length)
                        val cursorRect = layout.getCursorRect(offset)
                        val density = LocalDensity.current
                        val xOffset = with(density) { cursorRect.left.toDp() }

                        Box(modifier = Modifier.offset(x = xOffset)) {
                            BlinkingCursor()
                        }
                    }
                }
            }
            IconButton(
                onClick = { onPasswordVisibleChange(!passwordVisible) },
                modifier = Modifier.size(24.dp),
            ) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun LoginRememberMeRow(
    saveUser: Boolean,
    onSaveUserChange: (Boolean) -> Unit,
    interactionSource: MutableInteractionSource,
) {
    Row(
        modifier = Modifier.width(210.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(15.dp)
                    .background(
                        color =
                            if (saveUser) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.surfaceContainer
                            },
                        shape = RoundedCornerShape(5.dp),
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) { onSaveUserChange(!saveUser) },
            contentAlignment = Alignment.Center,
        ) {
            if (saveUser) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.size(10.dp),
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Guardar Usuario",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LoginDebugBox(debugMessage: String) {
    Box(
        modifier =
            Modifier
                .width(230.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
                    RoundedCornerShape(8.dp),
                )
                .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = "DEBUG: $debugMessage",
            fontSize = 9.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
            lineHeight = 12.sp,
            maxLines = 1,
        )
    }
}

/** Vista previa de la sección de usuario y contraseña en modo claro. */
@Preview(name = "User Password Light")
@Composable
fun LoginPantallaUserPasswordSectionPreviewLight() {
    LuxuryCheatsTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            LoginPantallaUserPasswordSection(
                credentials =
                    LoginCredentials(
                        username = TextFieldValue("UserDemo"),
                        password = TextFieldValue("Password123"),
                        licenseKey = TextFieldValue(""),
                        isLicenseMode = false,
                        focusedField = LoginField.NONE,
                        debugMessage = "Debug status OK",
                        onUsernameChange = {},
                        onPasswordChange = {},
                        onLicenseChange = {},
                        onLicenseModeToggle = {},
                        onGetLicenseClick = {},
                        onFocusFieldChange = {},
                    ),

                options =
                    LoginDisplayOptions(
                        saveUser = true,
                        onSaveUserChange = {},
                    ),
                displayState =
                    LoginDisplayState(
                        interactionState = LoginInteractionState.COMPACT,
                        tecladoType = TecladoType.NONE,
                    ),
                actions = LoginFieldActions({}, {}),
            )
        }
    }
}

/** Vista previa de la sección de usuario y contraseña en modo oscuro. */
@Preview(name = "User Password Dark")
@Composable
fun LoginPantallaUserPasswordSectionPreviewDark() {
    LuxuryCheatsTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            LoginPantallaUserPasswordSection(
                credentials =
                    LoginCredentials(
                        username = TextFieldValue("UserDemo"),
                        password = TextFieldValue("Password123"),
                        licenseKey = TextFieldValue(""),
                        isLicenseMode = false,
                        focusedField = LoginField.NONE,
                        debugMessage = "Debug status OK",
                        onUsernameChange = {},
                        onPasswordChange = {},
                        onLicenseChange = {},
                        onLicenseModeToggle = {},
                        onGetLicenseClick = {},
                        onFocusFieldChange = {},
                    ),

                options =
                    LoginDisplayOptions(
                        saveUser = true,
                        onSaveUserChange = {},
                    ),
                displayState =
                    LoginDisplayState(
                        interactionState = LoginInteractionState.COMPACT,
                        tecladoType = TecladoType.NONE,
                    ),
                actions = LoginFieldActions({}, {}),
            )
        }
    }
}


