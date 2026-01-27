package com.luxury.cheats.features.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.luxury.cheats.R
import com.luxury.cheats.core.theme.LuxuryCheatsTheme

/**
 * Dialog wrapper for the InfoMessageContent for testing and easy usage.
 */
@Composable
fun InfoMessageDialog(
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        InfoMessageSection(onDismiss = onDismissRequest)
    }
}

/**
 * Main Info Message Section widget.
 * Designed as a Box of 350x472 (outer) and 350x439 (body).
 */
@Composable
fun InfoMessageSection(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Outer Container 350x472
    Box(
        modifier = modifier
            .size(width = 350.dp, height = 472.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.46f),
                shape = RoundedCornerShape(40.dp)
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Header Section: Icon + "Mensaje" (Left Aligned, 30% Alpha, Pushed down slightly)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp) // Reduced from 12.dp
                .padding(horizontal = 35.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Default.Message,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Mensaje",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }

        // Main Body Box 350x439 (Bottom Aligned)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(width = 350.dp, height = 439.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.20f),
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Content Row: Title, Subtitle and Tag (Recurrente aligned with Title)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = "Luxury Cheat´s",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Importante",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Recurrente Tag (118x31, radius 30) - Aligned with the title area
                    Box(
                        modifier = Modifier
                            .size(width = 118.dp, height = 31.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recurrente",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Inner Box 308x281
                Box(
                    modifier = Modifier
                        .size(width = 308.dp, height = 281.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Avatar",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                repeat(3) { index ->
                                    Text(
                                        text = getDummyParagraph(index),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 14.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }
                        }

                        // Content Image 288x123
                        Box(
                            modifier = Modifier
                                .size(width = 288.dp, height = 123.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.info_message_content),
                                contentDescription = "Message Content",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Action Button: W:100 H:37, Aligned to RIGHT, Reduced top margin
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp), // Reduced from 16.dp
                    contentAlignment = Alignment.BottomEnd // Aligned to the RIGHT
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(width = 100.dp, height = 37.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(
                            text = "Entendido",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private fun getDummyParagraph(index: Int): String {
    return when (index) {
        0 -> "Mantenemos nuestros servicios actualizados diariamente para garantizar la mejor seguridad y rendimiento en todos tus juegos favoritos."
        1 -> "Recuerda siempre verificar el estado del servidor antes de iniciar una sesión larga para evitar interrupciones o detecciones innecesarias."
        2 -> "Gracias por confiar en Luxury Cheats. Estamos comprometidos con brindarte la experiencia de juego más exclusiva y potente del mercado."
        else -> ""
    }
}

@Preview(showBackground = true)
@Composable
fun InfoMessageSectionPreview() {
    LuxuryCheatsTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            InfoMessageSection(onDismiss = {})
        }
    }
}
