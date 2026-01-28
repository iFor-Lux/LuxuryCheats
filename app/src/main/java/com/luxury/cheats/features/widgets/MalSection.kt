package com.luxury.cheats.features.widgets

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.core.theme.LuxuryCheatsTheme

private const val AURA_INNER_ALPHA = 0.25f
private const val AURA_OUTER_ALPHA = 0.05f
private const val ICON_SIZE_LARGE = 60
private const val TITLE_FONT_SIZE = 28
private const val SUBTITLE_FONT_SIZE = 14
private const val SMALL_BUTTON_HEIGHT = 36
private const val BUTTON_WIDTH = 250
private const val BUTTON_HEIGHT = 50
private const val BUTTON_FONT_SIZE = 16
private const val ERROR_RED_COLOR = 0xFFFF4D4D

/**
 * Bottom sheet que muestra un mensaje de error tras una instalación fallida.
 * @param onDismissRequest Acción al cerrar el sheet.
 * @param onViewProblemsClick Acción para ver los detalles del error.
 * @param onExitClick Acción al pulsar salir.
 * @param sheetState Estado del bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MalBottomSheet(
    onDismissRequest: () -> Unit,
    onViewProblemsClick: () -> Unit,
    onExitClick: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null, // Remove official interactive handle
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 8.dp,
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
        MalContent(
            onViewProblemsClick = onViewProblemsClick,
            onExitClick = {
                onExitClick()
                onDismissRequest()
            }
        )
    }
}

/**
 * Contenido visual de la sección de error.
 * @param onViewProblemsClick Acción para ver problemas.
 * @param onExitClick Acción para salir.
 * @param modifier Modificador de layout.
 */
@Composable
fun MalContent(
    onViewProblemsClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Visual handle
        Box(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp)
                .size(width = 40.dp, height = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
        )

        ErrorAuraIcon()
        Spacer(modifier = Modifier.height(8.dp))
        ErrorTextSection()
        Spacer(modifier = Modifier.height(32.dp))
        ErrorActionButtons(onViewProblemsClick, onExitClick)
    }
}

@Composable
private fun ErrorAuraIcon() {
    Box(
        modifier = Modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(ERROR_RED_COLOR).copy(alpha = AURA_INNER_ALPHA),
                            Color(ERROR_RED_COLOR).copy(alpha = AURA_OUTER_ALPHA),
                            Color.Transparent
                        )
                    )
                )
        )

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.size(ICON_SIZE_LARGE.dp),
            tint = Color(ERROR_RED_COLOR)
        )
    }
}

@Composable
private fun ErrorTextSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "INCORRECTO",
            fontSize = TITLE_FONT_SIZE.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = (-0.5).sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Tuvimos un problema con la instalación",
            fontSize = SUBTITLE_FONT_SIZE.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ErrorActionButtons(
    onViewProblemsClick: () -> Unit,
    onExitClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onViewProblemsClick,
            modifier = Modifier.height(SMALL_BUTTON_HEIGHT.dp),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = null
        ) {
            Text(
                text = "Ver problemas",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onExitClick,
            modifier = Modifier
                .width(BUTTON_WIDTH.dp)
                .height(BUTTON_HEIGHT.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Salir",
                fontSize = BUTTON_FONT_SIZE.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Vista previa de la sección de error en modo claro y oscuro.
 */
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MalContentPreview() {
    LuxuryCheatsTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            MalContent(onViewProblemsClick = {}, onExitClick = {})
        }
    }
}
