package com.luxury.cheats.features.login.teclado.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.luxury.cheats.features.login.teclado.logic.TecladoType

private const val SPECIAL_KEY_WEIGHT = 1.3f

/**
 * Teclado alfabético exclusivo.
 * Evolucionado para incluir espacio, enter y símbolos estilo Gboard.
 */
@Composable
fun AlphabeticTeclado(
    isUpperCase: Boolean,
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit,
    onToggleCase: () -> Unit,
    onTecladoTypeChange: (TecladoType) -> Unit,
    onDone: () -> Unit,
) {
    val rows =


        listOf(
            listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"),
            listOf("A", "S", "D", "F", "G", "H", "J", "K", "L", "Ñ"),
            listOf("Z", "X", "C", "V", "B", "N", "M"),
        )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        val actions = TecladoRowActions(onKeyPress, onDelete, onToggleCase)

        // Primeras 3 filas
        rows.forEachIndexed { index, row ->
            TecladoRow(
                index = index,
                row = row,
                isUpperCase = isUpperCase,
                actions = actions,
            )
        }

        // Cuarta fila: ?123, Espacio, Enter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        ) {
            // Tecla de Símbolos
            TecladoKey(
                text = "?123",
                onClick = { onTecladoTypeChange(TecladoType.SYMBOLS) },
                modifier = Modifier.weight(SPECIAL_KEY_WEIGHT),
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )

            // Barra Espaciadora
            TecladoKey(
                text = " ",
                onClick = { onKeyPress(" ") },
                modifier = Modifier.weight(3.5f),
            )

            // Tecla Enter
            TecladoIconKey(
                icon = Icons.Default.Check,
                onClick = onDone,
                modifier = Modifier.weight(SPECIAL_KEY_WEIGHT),
                style = TecladoKeyStyle(
                    itemColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        }
    }
}

@Composable
private fun TecladoRow(
    index: Int,
    row: List<String>,
    isUpperCase: Boolean,
    actions: TecladoRowActions,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    ) {
        if (index == 2) {
            ToggleCaseKey(isUpperCase, actions.onToggleCase)
        }

        row.forEach { char ->
            val displayText = if (isUpperCase) char.uppercase() else char.lowercase()
            TecladoKey(
                text = displayText,
                onClick = { actions.onKeyPress(displayText) },
                modifier = Modifier.weight(1f),
            )
        }

        if (index == 2) {
            DeleteKey(actions.onDelete)
        }
    }
}

private data class TecladoRowActions(
    val onKeyPress: (String) -> Unit,
    val onDelete: () -> Unit,
    val onToggleCase: () -> Unit,
)

@Composable
private fun RowScope.ToggleCaseKey(
    isUpperCase: Boolean,
    onToggleCase: () -> Unit,
) {
    TecladoIconKey(
        icon = if (isUpperCase) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
        onClick = onToggleCase,
        modifier = Modifier.weight(SPECIAL_KEY_WEIGHT),
        style =
            TecladoKeyStyle(
                itemColor =
                    if (isUpperCase) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    },
                contentColor =
                    if (isUpperCase) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    },
            ),
    )
}

@Composable
private fun RowScope.DeleteKey(onDelete: () -> Unit) {
    TecladoIconKey(
        icon = Icons.AutoMirrored.Filled.Backspace,
        onClick = onDelete,
        modifier = Modifier.weight(SPECIAL_KEY_WEIGHT),
        style =
            TecladoKeyStyle(
                itemColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                enableRepeat = true,
            ),
    )
}
