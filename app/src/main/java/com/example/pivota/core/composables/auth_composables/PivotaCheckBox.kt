package com.example.pivota.core.composables.auth_composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PivotaCheckBox(){
    Checkbox(
        checked = false,
        onCheckedChange = {},
        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
        text = "I agree to the Terms and Conditions",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary
    )
}