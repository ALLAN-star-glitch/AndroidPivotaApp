package com.example.pivota.core.presentations.composables.buttons

import android.widget.Button
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PivotaPrimaryButton(
    modifier: Modifier = Modifier, // Put this before optional ones with no default
    text: String = "",
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier, // <- Named usage
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
