package com.example.pivota.core.composables.core_composables

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PivotaSecondaryButton(
    text: String = ""
){
    Button(
        onClick = { /* Login */ },
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color(0xFF3F51B5),
                shape = RoundedCornerShape(50.dp)
            ),
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium.copy(Color.Black))
    }
}