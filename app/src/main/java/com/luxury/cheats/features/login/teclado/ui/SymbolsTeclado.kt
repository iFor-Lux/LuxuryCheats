package com.luxury.cheats.features.login.teclado.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.luxury.cheats.features.login.teclado.logic.TecladoType

private const val SPECIAL_KEY_WEIGHT = 1.6f

/**
 * Teclado de símbolos y números.
 */
@Composable
fun SymbolsTeclado(
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit,
    onTecladoTypeChange: (TecladoType) -> Unit,
    onDone: () -> Unit,
) {
    val rows = listOf(


        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
        listOf("@", "#", "$", "_", "&", "-", "+", "(", ")", "/"),
        listOf("*", "\"", "'", ":", ";", "!", "?","\\")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Filas 1 y 2 (Números y Símbolos básicos)
        rows.take(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            ) {
                row.forEach { char ->
                    TecladoKey(
                        text = char,
                        onClick = { onKeyPress(char) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        // Fila 3: Símbolos + Borrar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        ) {
            val thirdRowSymbols = listOf("*", "\"", "'", ":", ";", "!", "?")
            thirdRowSymbols.forEach { char ->
                TecladoKey(
                    text = char,
                    onClick = { onKeyPress(char) },
                    modifier = Modifier.weight(1f),
                )
            }
            
            // Borrar movido aquí
            TecladoIconKey(
                icon = Icons.AutoMirrored.Filled.Backspace,
                onClick = onDelete,
                modifier = Modifier.weight(SPECIAL_KEY_WEIGHT),
                style = TecladoKeyStyle(
                    itemColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    enableRepeat = true
                )
            )
        }

        // Última fila: ABC, Espacio, Enter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        ) {
            // Volver a ABC
            TecladoKey(
                text = "ABC",
                onClick = { onTecladoTypeChange(TecladoType.ALPHABETIC) },
                modifier = Modifier.weight(SPECIAL_KEY_WEIGHT),
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )

            // Espacio
            TecladoKey(
                text = " ",
                onClick = { onKeyPress(" ") },
                modifier = Modifier.weight(4f)
            )

            // Enter
            TecladoIconKey(
                icon = Icons.Default.Check,
                onClick = onDone,
                modifier = Modifier.weight(SPECIAL_KEY_WEIGHT),
                style = TecladoKeyStyle(
                    itemColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}
