package com.luxury.cheats.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy

/**
 * Navigation Bar personalizado con efecto Glassmorphism usando Backdrop
 * Dimensiones: 190dp x 99dp
 */
@Composable
fun LuxuryNavigationBar(
    modifier: Modifier = Modifier,
    activeTab: String = "Inicio",
    backdrop: LayerBackdrop? = null,
    onTabSelected: (String) -> Unit = {}
) {
    val shape = RoundedCornerShape(30.dp)

    Box(
        modifier = modifier.size(width = 190.dp, height = 99.dp),
        contentAlignment = Alignment.Center
    ) {
        LuxuryNavBarBackground(backdrop, shape)
        LuxuryNavBarItems(activeTab, onTabSelected)
    }
}

@Composable
private fun LuxuryNavBarBackground(backdrop: LayerBackdrop?, shape: RoundedCornerShape) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape)
            .then(
                if (backdrop != null) {
                    Modifier.drawBackdrop(
                        backdrop = backdrop,
                        shape = { shape },
                        effects = {
                            lens(refractionHeight = 4f, refractionAmount = 0.5f)
                            blur(radius = 15f)
                            vibrancy()
                        }
                    )
                } else Modifier.background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f))
            )
    )
}

@Composable
private fun LuxuryNavBarItems(activeTab: String, onTabSelected: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                label = "Inicio",
                icon = Icons.Default.Home,
                isActive = activeTab == "Inicio",
                activeColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                onSelect = { onTabSelected("Inicio") }
            )

            NavItem(
                label = "Perfil",
                icon = Icons.Default.Person,
                isActive = activeTab == "Perfil",
                activeColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
                onSelect = { onTabSelected("Perfil") }
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    activeColor: Color,
    onSelect: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null // Sin ripple para mantener estética premium minimalista
        ) { onSelect() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 72.dp, height = 46.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isActive) activeColor else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
