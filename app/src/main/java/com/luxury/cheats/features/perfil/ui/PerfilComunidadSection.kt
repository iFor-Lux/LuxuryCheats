package com.luxury.cheats.features.perfil.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.core.theme.LuxuryCheatsTheme

private val BANNER_GREEN_HEX = Color(0xFFDBFFDD)
private const val BANNER_ALPHA = 0.16f

/**
 * Sección de comunidad de perfil
 * Contiene el título "Comunidad" y un Box contenedor con una descripción y banner de acción.
 */
@Composable
fun PerfilComunidadSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        ComunidadHeader()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(197.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ComunidadDescription()
                Spacer(modifier = Modifier.height(20.dp))
                ComunidadBanner()
            }
        }
    }
}

@Composable
private fun ComunidadHeader() {
    Text(
        text = "Comunidad",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
    )
}

@Composable
private fun ComunidadDescription() {
    Text(
        text = "Un agradecimiento especial a los increibles mienbros de nuestra comunidad " +
            "que han contribuido a mejorar Luxury Cheat’s a traves de pruebas, comentarios, " +
            "sugerencias y colaboraciones.",
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ComunidadBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(BANNER_GREEN_HEX.copy(alpha = BANNER_ALPHA)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "RECLAMAR DIAS GRATIS !!!",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Contribuye para poder recibir dias gratis en la app\n" +
                    "y poder probar lo maximo que podemos ofrecer",
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                fontSize = 9.sp,
                lineHeight = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Preview de la sección de comunidad en tema claro. */
@Preview(name = "Comunidad Light")
@Composable
fun PerfilComunidadPreviewLight() {
    LuxuryCheatsTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            PerfilComunidadSection()
        }
    }
}

/** Preview de la sección de comunidad en tema oscuro. */
@Preview(name = "Comunidad Dark")
@Composable
fun PerfilComunidadPreviewDark() {
    LuxuryCheatsTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            PerfilComunidadSection()
        }
    }
}

