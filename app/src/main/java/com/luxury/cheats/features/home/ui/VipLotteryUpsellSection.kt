package com.luxury.cheats.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.luxury.cheats.R

private const val MIN_RANDOM_ID = 100000
private const val MAX_RANDOM_ID = 999999

@Composable
fun VipLotteryUpsellSection(
    diamantesImageUrl: String?,
    onBuyVipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showInfoDialog by remember { mutableStateOf(false) }
    val randomId = remember { (MIN_RANDOM_ID..MAX_RANDOM_ID).random() }

    val newsreaderFont =
        FontFamily(
            Font(R.font.newsreader_italic),
        )

    val titleText =
        buildAnnotatedString {
            append("Luxury ")
            withStyle(
                style =
                    SpanStyle(
                        fontFamily = newsreaderFont,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    ),
            ) {
                append("Sorteo's")
            }
        }

    val descText =
        buildAnnotatedString {
            append("¡Participa diariamente por 999 diamantes en nuestros sorteos exclusivos para miembros ")
            withStyle(
                style =
                    SpanStyle(
                        fontFamily = newsreaderFont,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    ),
            ) {
                append("VIPs")
            }
            append("!")
        }

    Box(
        modifier =
            modifier
                .width(341.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(30.dp),
                )
                .clickable { showInfoDialog = true }
                .padding(vertical = 14.dp, horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        IconButton(
            onClick = { showInfoDialog = true },
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 4.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), CircleShape)
                    .size(24.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Detalles",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(14.dp),
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = titleText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = descText,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                modifier = Modifier.padding(horizontal = 12.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier =
                    Modifier
                        .width(259.dp)
                        .height(90.dp)
                        .clip(RoundedCornerShape(10.dp)),
            ) {
                if (diamantesImageUrl != null) {
                    AsyncImage(
                        model = diamantesImageUrl,
                        contentDescription = "Sorteo de Diamantes",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth,
                    )
                } else {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier =
                    Modifier
                        .width(259.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Ganador: ID: $randomId Ganó +999 diamantes",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }

    // DIÁLOGO SIMPLE DE INFORMACIÓN
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
        title = {
            Text(
                text = "SORTEOS LUXURY",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        text = {
            Text(
                text = "Solo los miembros VIP tienen acceso exclusivo a los sorteos de " +
                    "999 diamantes. ¡Mejora tu cuenta ahora para participar!",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            Button(
                onClick = {
                        showInfoDialog = false
                    onBuyVipClick()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
            ) {
                Text("COMPRAR VIP", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
    )
}
}
