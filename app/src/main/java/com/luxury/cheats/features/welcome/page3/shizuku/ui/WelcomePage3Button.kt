package com.luxury.cheats.features.welcome.page3.shizuku.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.R

private object ButtonConstants {
    val WIDTH = 160.dp
    val HEIGHT = 38.dp
    val PRESSED_RADIUS = 10.dp
    val DEFAULT_RADIUS = 19.dp
    val BORDER_WIDTH = 1.dp
    val FONT_SIZE = 12.sp
    const val PRESSED_SCALE = 0.96f
    const val DEFAULT_SCALE = 1f
}

/**
 * Nueva sección que contiene el botón premium para descargar Shizuku.
 * Cumple con el 10x Senior Code Mandate utilizando animaciones reactivas y física de resorte.
 */
@Composable
fun Buttonshizukusection(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    // Animación de morphing del radio con física de resorte (M3 Expressive)
    val radius by animateDpAsState(
        targetValue = if (pressed) ButtonConstants.PRESSED_RADIUS else ButtonConstants.DEFAULT_RADIUS,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "shizukuRadiusExpressive",
    )

    // Animación de escala sutil al presionar (M3 Expressive feel)
    val scale by animateFloatAsState(
        targetValue = if (pressed) ButtonConstants.PRESSED_SCALE else ButtonConstants.DEFAULT_SCALE,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
        label = "shizukuScaleExpressive",
    )

    Button(
        onClick = {
            try {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/iFor-Lux/DownloadLux/releases/download/v1.2.0/base.apk")
                )
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
        interactionSource = interactionSource,
        modifier =
            modifier
                .width(ButtonConstants.WIDTH)
                .height(ButtonConstants.HEIGHT)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
        shape = RoundedCornerShape(radius),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            ),
        border = BorderStroke(
            ButtonConstants.BORDER_WIDTH,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
        ),
    ) {
        Text(
            text = stringResource(R.string.welcome_page3_download_button),
            fontSize = ButtonConstants.FONT_SIZE,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
