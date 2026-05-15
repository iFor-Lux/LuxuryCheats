package com.luxury.cheats.services.floating.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryItem(
    text: String,
    isSelected: Boolean,
    normalColor: Color,
    selectedColor: Color,
    onClick: () -> Unit,
) {
    // Usamos un Box con altura fija para que el texto nunca se mueva
    Box(
        modifier =
            Modifier
                .height(54.dp) // Altura fija para estabilidad total
                .padding(horizontal = 4.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        // Texto: Siempre en el centro, nunca salta
        Text(
            text = text,
            color = if (isSelected) selectedColor else normalColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = (-0.2).sp,
            modifier = Modifier.padding(bottom = 4.dp), // Espacio para la línea
        )

        // Línea: Aparece abajo sin empujar al texto
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally(),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(bottom = 8.dp)
                        .width(16.dp)
                        .height(2.dp)
                        .background(selectedColor, RoundedCornerShape(1.dp)),
            )
        }
    }
}
