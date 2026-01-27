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
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.core.theme.LuxuryCheatsTheme

private const val AURA_INNER_ALPHA = 0.40f
private const val AURA_OUTER_ALPHA = 0.10f
private const val ICON_SIZE_LARGE = 64
private const val TITLE_FONT_SIZE = 24
private const val SUBTITLE_FONT_SIZE = 12
private const val SMALL_BUTTON_WIDTH = 70
private const val SMALL_BUTTON_HEIGHT = 30
private const val SMALL_BUTTON_FONT_SIZE = 8
private const val LARGE_BUTTON_WIDTH = 250
private const val LARGE_BUTTON_HEIGHT = 50
private const val LARGE_BUTTON_FONT_SIZE = 16

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
        dragHandle = {
            // Drag Handle: Purely visual
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
                    .clearAndSetSemantics { },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
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

@Composable
fun MalContent(
    onViewProblemsClick: () -> Unit,
    onExitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Error Icon & Aura Section - Using Red RadialGradient
        Box(
            modifier = Modifier
                .size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Layer 1: The Aura (Red Radial Gradient)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFF44336).copy(alpha = AURA_INNER_ALPHA),
                                Color(0xFFF44336).copy(alpha = AURA_OUTER_ALPHA),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Layer 2: The Icon (Solid Close X)
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Error",
                modifier = Modifier.size(ICON_SIZE_LARGE.dp),
                tint = Color(0xFFF44336)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title: INCORRECTO
        Text(
            text = "INCORRECTO",
            fontSize = TITLE_FONT_SIZE.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Tuvimos problema con la instalacion",
            fontSize = SUBTITLE_FONT_SIZE.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Action Button: Ver problemas (Now secondary)
        Button(
            onClick = onViewProblemsClick,
            modifier = Modifier
                .width(SMALL_BUTTON_WIDTH.dp)
                .height(SMALL_BUTTON_HEIGHT.dp),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(0.dp), // Important for very small buttons
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(
                text = "Ver problemas",
                fontSize = SMALL_BUTTON_FONT_SIZE.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Button: SALIR (Now primary/red)
        Button(
            onClick = onExitClick,
            modifier = Modifier
                .width(LARGE_BUTTON_WIDTH.dp)
                .height(LARGE_BUTTON_HEIGHT.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(
                text = "SALIR",
                fontSize = LARGE_BUTTON_FONT_SIZE.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

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
