package com.luxury.cheats.features.widgets

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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

private const val AURA_INNER_ALPHA = 0.45f
private const val AURA_OUTER_ALPHA = 0.15f
private const val ICON_SIZE_LARGE = 64
private const val TITLE_FONT_SIZE = 24
private const val SUBTITLE_FONT_SIZE = 12
private const val BUTTON_WIDTH = 250
private const val BUTTON_HEIGHT = 50
private const val BUTTON_FONT_SIZE = 16

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienBottomSheet(
    onDismissRequest: () -> Unit,
    onContinueClick: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = {
            // Drag Handle: Purely visual (clearing semantics removes interaction hints and text labels)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
                    .clearAndSetSemantics { }, // Removes accessibility text and interaction feedback
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
        BienContent(
            onContinueClick = {
                onContinueClick()
                onDismissRequest()
            }
        )
    }
}

@Composable
fun BienContent(
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Success Icon & Aura Section - Using RadialGradient for a perfect circular glow
        Box(
            modifier = Modifier
                .size(200.dp), // The requested 200x200 size
            contentAlignment = Alignment.Center
        ) {
            // Layer 1: The Aura (Radial Gradient)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF4CAF50).copy(alpha = AURA_INNER_ALPHA),
                                Color(0xFF4CAF50).copy(alpha = AURA_OUTER_ALPHA),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Layer 2: The Icon (Solid, on top of the blurred aura)
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                modifier = Modifier.size(ICON_SIZE_LARGE.dp),
                tint = Color(0xFF4CAF50)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title: Correcto
        Text(
            text = "Correcto",
            fontSize = TITLE_FONT_SIZE.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Felicidades la instalacion fue todo un exito",
            fontSize = SUBTITLE_FONT_SIZE.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Action Button: Continuar
        Button(
            onClick = onContinueClick,
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
                text = "Continuar",
                fontSize = BUTTON_FONT_SIZE.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BienContentPreview() {
    LuxuryCheatsTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            BienContent(onContinueClick = {})
        }
    }
}
