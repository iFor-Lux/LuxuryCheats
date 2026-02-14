package com.luxury.cheats.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private object SaludoConstants {
    const val VALENTINE_MONTH = 2
    const val VALENTINE_DAY = 14

    const val HALLOWEEN_MONTH = 10
    const val HALLOWEEN_START = 25
    const val HALLOWEEN_END = 31

    const val XMAS_MONTH = 12
    const val XMAS_START = 20
    const val XMAS_END = 31

    const val NEW_YEAR_MONTH = 1
    const val NEW_YEAR_DAY = 1

    const val DAY_START_HOUR = 6
    const val DAY_END_HOUR = 18
}

/**
 * Sección de saludo en el Home
 * - W: 341, H: 110, Corner: 30
 * - Estética premium con bordes sutiles y badge superior
 */
@Composable
fun homeSaludoSection(
    modifier: Modifier = Modifier,
    userName: String = "",
    greeting: String = "",
    subtitle: String = "",
) {
    val icon = getGreetingIcon()

    Box(
        modifier =
            modifier
                .width(341.dp)
                .height(125.dp),
    ) {
        homeGreetingCard(
            userName = userName,
            greeting = greeting,
            subtitle = subtitle,
            icon = icon,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun getGreetingIcon(): androidx.compose.ui.graphics.vector.ImageVector {
    val currentHour = java.time.LocalTime.now().hour
    val now = java.time.LocalDate.now()
    val month = now.monthValue
    val day = now.dayOfMonth

    val isValentine =
        month == SaludoConstants.VALENTINE_MONTH &&
            day == SaludoConstants.VALENTINE_DAY

    val isHalloween =
        month == SaludoConstants.HALLOWEEN_MONTH &&
            day in SaludoConstants.HALLOWEEN_START..SaludoConstants.HALLOWEEN_END

    val isXmas =
        month == SaludoConstants.XMAS_MONTH &&
            day in SaludoConstants.XMAS_START..SaludoConstants.XMAS_END

    val isNewYear =
        month == SaludoConstants.NEW_YEAR_MONTH &&
            day == SaludoConstants.NEW_YEAR_DAY

    return when {
        isValentine -> Icons.Default.Favorite
        isHalloween || isXmas || isNewYear -> Icons.Default.Celebration
        currentHour in SaludoConstants.DAY_START_HOUR..SaludoConstants.DAY_END_HOUR ->
            Icons.Default.WbSunny
        else -> Icons.Default.NightsStay
    }
}

@Composable
private fun homeGreetingCard(
    userName: String,
    greeting: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(30.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(30.dp),
                )
                .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(55.dp),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "$greeting @$userName",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                )
            }
        }
    }
}
