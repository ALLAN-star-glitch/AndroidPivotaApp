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
        ListingStatus.AVAILABLE -> MaterialTheme.colorScheme.surfaceContainerHigh
        ListingStatus.RENTED -> MaterialTheme.colorScheme.surfaceContainerHigh
        ListingStatus.INACTIVE -> MaterialTheme.colorScheme.surfaceContainerHigh
        ListingStatus.SOLD -> MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val contentColor = when (status) {
        ListingStatus.ACTIVE -> MaterialTheme.colorScheme.onPrimaryContainer
        ListingStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer
        ListingStatus.CLOSED -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.AVAILABLE -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.RENTED -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.INACTIVE -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.SOLD -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val label = when (status) {
        ListingStatus.ACTIVE -> "Active"
        ListingStatus.PENDING -> "Pending"
        ListingStatus.CLOSED -> "Closed"
        ListingStatus.AVAILABLE -> "Available"
        ListingStatus.RENTED -> "Rented"
        ListingStatus.INACTIVE -> "Inactive"
        ListingStatus.SOLD -> "Sold"
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

