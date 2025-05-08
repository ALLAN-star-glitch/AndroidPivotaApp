package com.example.pivota.core.presentations.composables.buttons

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun PivotaUpgradeButton(modifier: Modifier){
    Button(
        modifier = modifier,
        onClick = { /* Register */ },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
    ) {
        Text(
            "Upgrade Now",
            style = MaterialTheme.typography.bodyMedium.copy(Color.Black)
        )
    }
}