package com.example.pivota.listings.presentation.composables.jobs

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.listings.domain.models.JobType
import com.example.pivota.listings.presentation.viewmodel.PostJobViewModel

@Composable
fun LivePreviewSurface(viewModel: PostJobViewModel) {
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
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // The Actual Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Top Row: Job Type & Brand Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PreviewBadge(type = uiState.jobType)

                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Org",
                        tint = Color(0xFF006565),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title - Dynamic Placeholder
                Text(
                    text = uiState.title.ifBlank { "Your Job Title Here" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.title.isBlank()) Color.LightGray else Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Location Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (uiState.locationCity.isBlank()) "Location" else "${uiState.locationCity}, ${uiState.locationNeighborhood}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

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
                            color = Color(0xFF006565)
                        )
                        Text(
                            text = "per ${uiState.payRate.name.lowercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }

                    if (uiState.isNegotiable) {
                        Surface(
                            color = Color(0xFFFFC107).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Negotiable",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF856404),
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
fun PreviewBadge(type: JobType) {
    val backgroundColor by animateColorAsState(
        if (type == JobType.INFORMAL) Color(0xFFF0F4F4) else Color(0xFF006565).copy(alpha = 0.1f)
    )
    val textColor = if (type == JobType.INFORMAL) Color.DarkGray else Color(0xFF006565)

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