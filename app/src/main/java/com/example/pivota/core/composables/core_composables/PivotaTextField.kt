package com.example.pivota.core.composables.core_composables


import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun PivotaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    val tealColor = Color(0xFF008080)

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = tealColor,
        unfocusedBorderColor = tealColor,
        focusedLabelColor = tealColor,
        unfocusedLabelColor = tealColor,
        cursorColor = tealColor
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = textFieldColors
    )
}
