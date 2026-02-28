package com.luxury.cheats.features.tools.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luxury.cheats.core.ui.SquarePatternBackground
import com.luxury.cheats.core.theme.newsreaderItalicFamily

/**
 * Pantalla de herramientas (Tools).
 * Por ahora es un placeholder para la nueva sección.
 */
@Composable
fun ToolsScreen(
    modifier: Modifier = Modifier,
    backdrop: com.kyant.backdrop.backdrops.LayerBackdrop? = null
) {
    Box(modifier = modifier.fillMaxSize()) {
        SquarePatternBackground()
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tools",
                fontFamily = newsreaderItalicFamily,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 100.dp)
            )
            
            Text(
                text = "Sección en desarrollo",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center).padding(top = 40.dp)
            )
        }
    }
}
