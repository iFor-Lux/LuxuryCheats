import androidx.compose.ui.res.stringResource
import com.luxury.cheats.R

private object ButtonConstants {
    val HEIGHT = 66.dp
    val HORIZONTAL_PADDING = 32.dp
    val PRESSED_RADIUS = 16.dp
    val DEFAULT_RADIUS = 30.dp
    val BORDER_WIDTH = 1.dp
    val FONT_SIZE = 24.sp
}

/**
 * Sección del botón principal de la primera página de bienvenida.
 *
 * @param onNavigateNext Callback para navegar a la siguiente página.
 * @param modifier Modificador de Compose.
 */
@Composable
fun WelcomePage1ButtonSection(
    onNavigateNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val radius by animateDpAsState(
        if (pressed) ButtonConstants.PRESSED_RADIUS else ButtonConstants.DEFAULT_RADIUS,
        label = "radius"
    )

    Button(
        onClick = onNavigateNext,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ButtonConstants.HORIZONTAL_PADDING)
            .height(ButtonConstants.HEIGHT),
        shape = RoundedCornerShape(radius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(ButtonConstants.BORDER_WIDTH, MaterialTheme.colorScheme.tertiary)
    ) {
        Text(
            text = stringResource(R.string.welcome_page1_start),
            fontSize = ButtonConstants.FONT_SIZE,
            fontWeight = FontWeight.SemiBold
        )
    }
}


