package com.luxury.cheats.features.widgets

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.core.theme.LuxuryCheatsTheme

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
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFF44336).copy(alpha = 0.40f), // Red 400
                                Color(0xFFF44336).copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Layer 2: The Icon (Solid Close X)
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Error",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFFF44336)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title: INCORRECTO (24sp, Bold)
        Text(
            text = "INCORRECTO",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle: Tuvimos problema con la instalacion (12sp)
        Text(
            text = "Tuvimos problema con la instalacion",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Action Button: Ver problemas (Now secondary)
        Button(
            onClick = onViewProblemsClick,
            modifier = Modifier
                .width(70.dp)
                .height(30.dp),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(0.dp), // Important for very small buttons
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(
                text = "Ver problemas",
                fontSize = 8.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Button: SALIR (Now primary/red)
        Button(
            onClick = onExitClick,
            modifier = Modifier
                .width(250.dp)
                .height(50.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(
                text = "SALIR",
                fontSize = 16.sp,
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
