package com.luxury.cheats.features.welcome.page1.bienvenida.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Constantes de diseño para insignias
private val BADGE_WIDTH_EXPERIENCE = 144.dp
private val BADGE_WIDTH_ANTIBLACKLIST = 125.dp
private val BADGE_WIDTH_ANTIBAN = 99.dp
private val BADGE_WIDTH_ANDROID = 99.dp
private val BADGE_WIDTH_PRIVATE = 61.dp

private val COLOR_EXPERIENCE = Color(0xFFF7FF1C)
private val COLOR_ANTIBLACKLIST = Color(0xFF00D0FF)
private val COLOR_ANTIBAN = Color(0xFF1CFF4D)
private val COLOR_ANDROID = Color(0xFFFF8E1C)
private val COLOR_PRIVATE = Color(0xFFFF1CBB)



/**
 * Sección de Badges para Page 1
 * - 5 insignias con colores específicos
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WelcomePage1BadgesSection(
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 3
    ) {
        BadgeItem(text = "5 años de experiencia", color = COLOR_EXPERIENCE, width = BADGE_WIDTH_EXPERIENCE)
        BadgeItem(text = "100% Antiblacklist", color = COLOR_ANTIBLACKLIST, width = BADGE_WIDTH_ANTIBLACKLIST)
        BadgeItem(text = "100% Antiban", color = COLOR_ANTIBAN, width = BADGE_WIDTH_ANTIBAN)
        BadgeItem(text = "Android 9 -16", color = COLOR_ANDROID, width = BADGE_WIDTH_ANDROID)
        BadgeItem(text = "Privado", color = COLOR_PRIVATE, width = BADGE_WIDTH_PRIVATE)
    }
}

@Composable
private fun BadgeItem(
    text: String,
    color: Color,
    width: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .width(width)
            .height(22.dp)
            .background(color, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp, // Ajustado para que quepa bien en 22dp de altura
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}
