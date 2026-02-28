package com.luxury.cheats.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Sección de ID
 * - Texto "ID" seguido por un input estético interactivo
 * - W: 341, H: 35 (Input), BG: 404040
 */
@Composable
fun HomeIdSection(
    modifier: Modifier = Modifier,
    idValue: String = "",
    onIdValueChange: (String) -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier.width(341.dp),
    ) {
        Text(
            text = "ID",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HomeIdInputField(
                idValue = idValue,
                onIdValueChange = onIdValueChange,
                onSearchClick = onSearchClick,
                focusRequester = focusRequester,
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(8.dp))

            HomeIdSaveButton(
                onClick = {
                    onSaveClick()
                    onSearchClick()
                },
            )
        }
    }
}

@Composable
private fun HomeIdInputField(
    idValue: String,
    onIdValueChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    val textFieldValue = remember(idValue) {
        androidx.compose.ui.text.input.TextFieldValue(
            text = idValue,
            selection = androidx.compose.ui.text.TextRange(idValue.length)
        )
    }

    Box(
        modifier =
            modifier
                .height(35.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(10.dp))
                .clickable { focusRequester.requestFocus() }
                .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicTextField(
            modifier = Modifier.focusRequester(focusRequester),
            value = textFieldValue,
            onValueChange = { newValue ->
                if (newValue.text.all { it.isDigit() }) {
                    onIdValueChange(newValue.text)
                }
            },
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search,
                ),
            keyboardActions = KeyboardActions(onSearch = { onSearchClick() }),
            visualTransformation = VisualTransformation.None,
            singleLine = true,
            decorationBox = { innerTextField ->
                if (idValue.isEmpty()) {
                    Text(
                        text = "Ingrese su ID",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        fontSize = 14.sp,
                    )
                }
                innerTextField()
            },
        )
    }
}

@Composable
private fun HomeIdSaveButton(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .width(50.dp)
                .height(35.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Guardar ID",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
    }
}
