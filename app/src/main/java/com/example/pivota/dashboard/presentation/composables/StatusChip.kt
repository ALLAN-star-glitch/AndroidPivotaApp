package com.example.pivota.dashboard.presentation.composables

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.pivota.dashboard.domain.ListingStatus

@Composable
fun StatusChip(status: ListingStatus) {

    val containerColor = when (status) {
        ListingStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
        ListingStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer
        ListingStatus.CLOSED -> MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val contentColor = when (status) {
        ListingStatus.ACTIVE -> MaterialTheme.colorScheme.onPrimaryContainer
        ListingStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer
        ListingStatus.CLOSED -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val label = when (status) {
        ListingStatus.ACTIVE -> "Active"
        ListingStatus.PENDING -> "Pending"
        ListingStatus.CLOSED -> "Closed"
    }

    AssistChip(
        onClick = {},
        label = { Text(text = label) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = contentColor
        )
    )
}

