package com.example.pivota.dashboard.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.HomeWork
import androidx.compose.material.icons.rounded.Plumbing
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostOptionsBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colorScheme.surface,
        tonalElevation = 8.dp,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = colorScheme.primary.copy(alpha = 0.4f))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Header
            Text(
                text = "Post a New Listing",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    color = colorScheme.primary // Pivota Teal
                )
            )
            Text(
                text = "Select a category to connect with your audience",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = colorScheme.onSurfaceVariant,
                    letterSpacing = 0.2.sp
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // MVP1 Pillar: Employment
            PostOptionItem(
                title = "Post a Job",
                subtitle = "Find talent, interns, or offer training",
                icon = Icons.Rounded.BusinessCenter,
                onClick = { onOptionSelected("jobs") },
                colorScheme = colorScheme
            )

            // MVP1 Pillar: Housing
            PostOptionItem(
                title = "Post a House",
                subtitle = "List apartments, land plots, or rentals",
                icon = Icons.Rounded.HomeWork,
                onClick = { onOptionSelected("housing") },
                colorScheme = colorScheme
            )

            // MVP1 Pillar: Help & Support
            PostOptionItem(
                title = "Post for Help",
                subtitle = "Social services, NGO programs, or aid",
                icon = Icons.Rounded.Handshake,
                onClick = { onOptionSelected("support") },
                colorScheme = colorScheme
            )

            // New: Service Offering (For Service Providers)
            PostOptionItem(
                title = "Post a Service",
                subtitle = "Plumbing, moving, legal, or professional help",
                icon = Icons.Rounded.Plumbing,
                onClick = { onOptionSelected("service") },
                colorScheme = colorScheme
            )
        }
    }
}

@Composable
private fun PostOptionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    colorScheme: androidx.compose.material3.ColorScheme
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        color = colorScheme.primary.copy(alpha = 0.05f), // Very light teal tint
        border = null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Elegant Icon Container with Gradient
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                colorScheme.primary,
                                colorScheme.primary.copy(alpha = 0.8f) // Slightly darker version
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                )
            }

            // Subtle arrow to indicate action
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = colorScheme.primary.copy(alpha = 0.3f)
            )
        }
    }
}