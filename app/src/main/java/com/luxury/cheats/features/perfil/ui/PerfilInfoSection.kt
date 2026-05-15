package com.luxury.cheats.features.perfil.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.luxury.cheats.features.perfil.logic.PerfilAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val CAPTURE_DELAY_MS = 100L
private const val BLINK_ANIMATION_DURATION_MS = 1500

/**
 * Sección de información principal del perfil.
 *
 * Muestra el banner, avatar, estado VIP y detalles del usuario.
 *
 * @param modifier Modificador para el contenedor.
 * @param data Datos del perfil a renderizar.
 * @param onAction Callback para eventos de usuario.
 */
@Composable
fun PerfilInfoSection(
    modifier: Modifier = Modifier,
    data: ProfileInfoData = ProfileInfoData(),
    onAction: (PerfilAction) -> Unit = {},
) {
    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    var showOverlay by remember { mutableStateOf(true) }

    Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(49.dp))
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(20.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                InfoHeaderSection(
                    tier = data.tier,
                    profileImageUri = data.profileImageUri,
                    bannerImageUri = data.bannerImageUri,
                    onAction = onAction,
                    showOverlay = showOverlay,
                )
                Spacer(modifier = Modifier.height(12.dp))
                InfoUserDetails(data.userName, data.userId)
                Spacer(modifier = Modifier.height(20.dp))
                InfoStatusButtons(
                    remainingDays = data.remainingDays,
                    androidVersion = data.androidVersion,
                    appVersion = data.appVersion,
                )
            }
        }

        // Botón de descarga fuera del Box capturado por graphicsLayer
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(36.dp) // Ajustado para que no tape el banner demasiado
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.35f), CircleShape)
                    .clip(CircleShape)
                    .clickable {
                        scope.launch {
                            try {
                                showOverlay = false
                                delay(CAPTURE_DELAY_MS)
                                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                onAction(PerfilAction.SaveProfileClicked(bitmap))
                            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                                android.util.Log.e("PerfilInfoSection", "Error capturing bitmap", e)
                            } finally {
                                showOverlay = true
                            }
                        }
                    },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Compartir Perfil",
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

/**
 * Datos de información del perfil.
 */
data class ProfileInfoData(
    val userName: String = "",
    val userId: String = "",
    val tier: String = "free",
    val remainingDays: String = "",
    val androidVersion: String = "",
    val appVersion: String = "",
    val profileImageUri: String? = null,
    val bannerImageUri: String? = null,
)

@Composable
private fun InfoHeaderSection(
    tier: String,
    profileImageUri: String?,
    bannerImageUri: String?,
    onAction: (PerfilAction) -> Unit,
    showOverlay: Boolean = true,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(207.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        val backdrop = rememberLayerBackdrop()

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .layerBackdrop(backdrop),
        ) {
            InfoBanner(
                uri = bannerImageUri,
                modifier = Modifier.align(Alignment.TopCenter),
                onClick = { onAction(PerfilAction.BannerImageClicked) },
                showOverlay = showOverlay,
            )
            InfoAvatar(
                uri = profileImageUri,
                modifier = Modifier.align(Alignment.BottomStart),
                onClick = { onAction(PerfilAction.ProfileImageClicked) },
            )
        }

        InfoTierTag(
            tier = tier,
            backdrop = backdrop,
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

@Composable
private fun InfoBanner(
    uri: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    showOverlay: Boolean = true,
) {
    Box(
        modifier =
            modifier
                .size(width = 340.dp, height = 157.dp)
                .clip(RoundedCornerShape(29.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.40f),
                    shape = RoundedCornerShape(29.dp),
                )
                .clickable { onClick() },
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "Blink")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(BLINK_ANIMATION_DURATION_MS, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "Alpha",
        )

        AsyncImage(
            model = uri,
            contentDescription = "Cover Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        if (showOverlay) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "CLICK PARA CAMBIAR",
                    color = Color.White.copy(alpha = alpha),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.graphicsLayer(alpha = alpha),
                )
            }
        }
    }
}

@Composable
private fun InfoAvatar(
    uri: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .padding(start = 16.dp)
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = CircleShape,
                )
                .clickable { onClick() },
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Profile Avatar",
            modifier = Modifier.fillMaxSize().padding(1.dp).clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun InfoTierTag(
    tier: String,
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(30.dp)

    Box(
        modifier =
            modifier
                .padding(end = 16.dp, bottom = 28.dp)
                .size(width = 131.dp, height = 43.dp)
                .clip(shape)
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { shape },
                    effects = {
                        lens(refractionHeight = 4f, refractionAmount = 0.5f)
                        blur(radius = 16f)
                        vibrancy()
                    },
                )
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f), shape = shape)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.4f),
                    shape = shape,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            val (icon, label) = when (tier.lowercase()) {
                "plus" -> Pair(Icons.Filled.AutoAwesome, "Plus")
                "vip" -> Pair(Icons.Filled.Star, "Vip")
                else -> Pair(Icons.Filled.Person, "Free")
            }

            Icon(
                imageVector = icon,
                contentDescription = "$label Icon",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Composable
private fun InfoUserDetails(
    userName: String,
    userId: String,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = userName,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = userId,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun InfoStatusButtons(
    remainingDays: String,
    androidVersion: String,
    appVersion: String,
) {
    Row(
        modifier =
            Modifier
                .width(280.dp)
                .height(40.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusButton(
            text = remainingDays.uppercase(),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp, topEnd = 5.dp, bottomEnd = 5.dp),
        )
        StatusButton(
            text = "ANDROID $androidVersion",
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(5.dp),
        )
        StatusButton(
            text = appVersion,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp, topStart = 5.dp, bottomStart = 5.dp),
        )
    }
}

/**
 * Botón de estado estilizado para la sección de información.
 *
 * @param text Texto a mostrar en el botón.
 * @param modifier Modificador de Compose.
 * @param shape Forma geométrica del botón.
 */
@Composable
fun StatusButton(
    text: String,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape,
) {
    Box(
        modifier =
            modifier
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f), shape = shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
