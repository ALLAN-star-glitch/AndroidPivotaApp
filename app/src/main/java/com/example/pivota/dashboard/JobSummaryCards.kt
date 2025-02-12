package com.example.pivota.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun JobSummaryCards() {
    Row(
        modifier = Modifier.fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryCard(icon = Icons.Filled.ThumbUp, value = "12", label = "Total Jobs")
        SummaryCard(icon = Icons.Filled.Person, value = "8", label = "Active Applications")
        SummaryCard(icon = Icons.Filled.Done, value = "KSH 50,000", label = "Payments Processed")
    }
}