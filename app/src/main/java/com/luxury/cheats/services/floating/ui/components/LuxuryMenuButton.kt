package com.luxury.cheats.services.floating.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private object LuxuryMenuButtonConstants {
    val BG_COLOR = Color(0xFF003300)
    val ACCENT_COLOR = Color(0xFFBBFF00)
}

@Composable
fun LuxuryMenuButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onClick: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(LuxuryMenuButtonConstants.BG_COLOR) // Verde muy oscuro
                .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = LuxuryMenuButtonConstants.ACCENT_COLOR, // Verde neón
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                color = LuxuryMenuButtonConstants.ACCENT_COLOR, // Texto verde neón
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
