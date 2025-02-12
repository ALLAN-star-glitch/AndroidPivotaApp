package com.example.pivota.dashboard


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@Composable
fun DashboardHeader() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Employer Dashboard",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF008080) // Pivota Teal
        )
        Button(
            onClick = { /* Navigate to Post Job */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)) // Pivota Gold
        ) {
            Text("+ Post Job", color = Color.Black)
        }
    }
}