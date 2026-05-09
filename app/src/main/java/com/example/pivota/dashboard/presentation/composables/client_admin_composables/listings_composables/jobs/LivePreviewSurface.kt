package com.example.pivota.dashboard.presentation.composables.client_admin_composables.listings_composables.jobs

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.dashboard.domain.model.listings_models.jobs.JobType
import com.example.pivota.dashboard.presentation.viewmodels.client_general_viewmodels.PostJobViewModel

@Composable
fun LivePreviewSurface(viewModel: PostJobViewModel) {
    val colorScheme = MaterialTheme.colorScheme
    val uiState by viewModel.uiState.collectAsState()

    // The Preview Container
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "PREVIEW",
            style = MaterialTheme.typography.labelLarge,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // The Actual Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = colorScheme.surface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Top Row: Job Type & Brand Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PreviewBadge(type = uiState.jobType, colorScheme = colorScheme)

                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Org",
                        tint = colorScheme.primary, // African Sapphire
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title - Dynamic Placeholder
                Text(
                    text = uiState.title.ifBlank { "Your Job Title Here" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.title.isBlank()) colorScheme.onSurfaceVariant.copy(alpha = 0.3f) else colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Location Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (uiState.locationCity.isBlank()) "Location" else "${uiState.locationCity}, ${uiState.locationNeighborhood}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 0.5.dp,
                    color = colorScheme.outlineVariant
                )

                // Compensation Highlight
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "KES ${uiState.payAmount}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = colorScheme.primary // African Sapphire
                        )
                        Text(
                            text = "per ${uiState.payRate.name.lowercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    if (uiState.isNegotiable) {
                        Surface(
                            color = colorScheme.tertiary.copy(alpha = 0.1f), // Baobab Gold with opacity
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Negotiable",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.tertiary, // Baobab Gold
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewBadge(
    type: JobType,
    colorScheme: ColorScheme
) {
    val backgroundColor by animateColorAsState(
        if (type == JobType.INFORMAL)
            colorScheme.primary.copy(alpha = 0.05f)
        else
            colorScheme.primary.copy(alpha = 0.1f)
    )
    val textColor = if (type == JobType.INFORMAL)
        colorScheme.onSurfaceVariant
    else
        colorScheme.primary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (type == JobType.INFORMAL) "CASUAL" else "FORMAL",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = textColor,
            letterSpacing = 1.sp
        )
    }
}