package com.example.pivota.dashboard.presentation.composables

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
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
        ListingStatus.PAUSED -> MaterialTheme.colorScheme.surfaceContainerHigh
        ListingStatus.REJECTED -> MaterialTheme.colorScheme.surfaceContainerHigh
        ListingStatus.EXPIRED -> MaterialTheme.colorScheme.surfaceContainerHigh
        ListingStatus.ARCHIVED -> MaterialTheme.colorScheme.surfaceContainerHigh
        ListingStatus.DRAFT -> MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val contentColor = when (status) {
        ListingStatus.ACTIVE -> MaterialTheme.colorScheme.onPrimaryContainer
        ListingStatus.PENDING -> MaterialTheme.colorScheme.onTertiaryContainer
        ListingStatus.CLOSED -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.AVAILABLE -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.RENTED -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.INACTIVE -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.SOLD -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.PAUSED -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.REJECTED -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.EXPIRED -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.ARCHIVED -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.DRAFT -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val label = when (status) {
        ListingStatus.ACTIVE -> "Active"
        ListingStatus.PENDING -> "Pending"
        ListingStatus.CLOSED -> "Closed"
        ListingStatus.AVAILABLE -> "Available"
        ListingStatus.RENTED -> "Rented"
        ListingStatus.INACTIVE -> "Inactive"
        ListingStatus.SOLD -> "Sold"
        ListingStatus.PAUSED -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.REJECTED -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.EXPIRED -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.ARCHIVED -> MaterialTheme.colorScheme.onSurfaceVariant
        ListingStatus.DRAFT -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    AssistChip(
        onClick = {},
        label = { Text(
            text = label as AnnotatedString,
            modifier = TODO(),
            color = TODO(),
            autoSize = TODO(),
            fontSize = TODO(),
            fontStyle = TODO(),
            fontWeight = TODO(),
            fontFamily = TODO(),
            letterSpacing = TODO(),
            textDecoration = TODO(),
            textAlign = TODO(),
            lineHeight = TODO(),
            overflow = TODO(),
            softWrap = TODO(),
            maxLines = TODO(),
            minLines = TODO(),
            inlineContent = TODO(),
            onTextLayout = TODO(),
            style = TODO()
        ) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = contentColor
        )
    )
}

