package com.luxury.cheats.features.perfil.ui

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.core.theme.luxuryCheatsTheme

private val PINK_BANNER_HEX = Color(0xFFFFDBFE)
private const val BANNER_ALPHA = 0.16f

private object SkillsConstants {
    const val SKILLS_WEIGHT = 1.3f
}

/**
 * Sección de créditos del perfil
 * Contiene el título "Creditos" y una tarjeta con información del creador.
 */
@Composable
fun perfilCreditosSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
    ) {
        creditosHeader()
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(124.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            contentAlignment = Alignment.Center,
        ) {
            creditosCard()
        }
    }
}

@Composable
private fun creditosHeader() {
    Text(
        text = "Creditos",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 12.dp, start = 8.dp),
    )
}

@Composable
private fun creditosCard() {
    Box(
        modifier =
            Modifier
                .width(333.dp)
                .height(96.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PINK_BANNER_HEX.copy(alpha = BANNER_ALPHA)),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            creatorAvatar()

            // Columna 1: Identidad (más espacio para evitar cortes feos)
            creatorInfo(Modifier.weight(1f))

            // Columna 2: Especialidades (con espacio suficiente para las bullets)
            creatorSkills(Modifier.weight(SkillsConstants.SKILLS_WEIGHT))
        }
    }
}

@Composable
private fun creatorAvatar() {
    Box(
        modifier =
            Modifier
                .size(60.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), CircleShape),
    ) {
        // Placeholder Box (Removed local resource)
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        )
    }
}

@Composable
private fun creatorInfo(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "iFor1722",
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Desarrollador principal y arquitecto del proyecto",
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
            fontSize = 9.sp,
            lineHeight = 12.sp,
        )
        Text(
            text = "@Lux._ez",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun creatorSkills(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ) {
        bulletText(text = "Ingenieria de Software AI")
        bulletText(text = "Desarrolador FullStack")
    }
}

/**
 * Componente de texto con un punto (bullet) al inicio.
 *
 * @param text El texto a mostrar.
 */
@Composable
fun bulletText(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 1.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier =
                Modifier
                    .padding(top = 4.dp) // Alineado con la primera línea de texto
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
            fontSize = 9.sp,
            lineHeight = 11.sp,
        )
    }
}

/** Preview de la sección de créditos en tema claro. */
@Preview(name = "Creditos Light")
@Composable
fun perfilCreditosPreviewLight() {
    luxuryCheatsTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            perfilCreditosSection()
        }
    }
}

/** Preview de la sección de créditos en tema oscuro. */
@Preview(name = "Creditos Dark")
@Composable
fun perfilCreditosPreviewDark() {
    luxuryCheatsTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            perfilCreditosSection()
        }
    }
}
