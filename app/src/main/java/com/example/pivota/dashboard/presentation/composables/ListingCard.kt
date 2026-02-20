package com.example.pivota.dashboard.presentation.composables

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.pivota.dashboard.domain.*
import com.example.pivota.dashboard.presentation.model.ListingUiModel
import com.example.pivota.dashboard.presentation.model.PerformanceHint

@Composable
fun ListingCard(
    listing: ListingUiModel,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(Modifier.padding(16.dp)) {

            // Title + Category + Status
            Row(verticalAlignment = Alignment.CenterVertically) {

                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = listing.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = listing.category.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StatusChip(status = listing.status)
            }

            Spacer(Modifier.height(8.dp))

            // Description Preview
            Text(
                text = listing.descriptionPreview,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(12.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(Modifier.height(12.dp))

            // Metrics + Chevron
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                BottomMetricsRow(listing = listing)

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Optional performance hint
            listing.performanceHint?.let {
                Spacer(Modifier.height(8.dp))
                PerformanceHintView(it)
            }
        }
    }
}

@Composable
private fun BottomMetricsRow(listing: ListingUiModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        MetricItem(label = "Views", value = listing.views)
        MetricItem(label = "Messages", value = listing.messages)
        MetricItem(label = "Requests", value = listing.requests)
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: Int
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PerformanceHintView(hint: PerformanceHint) {

    val text = when (hint) {
        PerformanceHint.HighInterest -> "High interest this week"
        PerformanceHint.NewResponses -> "New responses today"
        is PerformanceHint.Custom -> hint.message
    }

    AssistChip(
        onClick = {},
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

