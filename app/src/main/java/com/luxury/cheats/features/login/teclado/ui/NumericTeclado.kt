package com.luxury.cheats.features.login.teclado.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val LAST_ROW_INDEX = 3
private const val KEY_WIDTH = 80

/**
 * Teclado numérico refinado.
 * Se eliminó la tecla de retorno por petición del usuario.
 */
@Composable
fun numericTeclado(
    onKeyPress: (String) -> Unit,
    onDelete: () -> Unit,
) {
    val numbers =
        listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf(".", "0"),
        )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        numbers.forEachIndexed { index, row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                row.forEach { num ->
                    tecladoKey(
                        text = num,
                        onClick = { onKeyPress(num) },
                        modifier = Modifier.width(KEY_WIDTH.dp),
                    )
                }
                if (index == LAST_ROW_INDEX) {
                    tecladoIconKey(
                        icon = Icons.AutoMirrored.Filled.Backspace,
                        onClick = onDelete,
                        modifier = Modifier.width(KEY_WIDTH.dp),
                        style =
                            TecladoKeyStyle(
                                itemColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                enableRepeat = true,
                            ),
                    )
                }
            }
        }
    }
}
