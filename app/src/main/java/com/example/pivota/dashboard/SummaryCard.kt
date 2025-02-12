package com.example.pivota.dashboard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun SummaryCard(icon: ImageVector, value: String, label: String) {
    Card(
        modifier = Modifier.padding(16.dp), // Use padding here instead of weight
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = label, tint = Color(0xFF008080), modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(label, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
