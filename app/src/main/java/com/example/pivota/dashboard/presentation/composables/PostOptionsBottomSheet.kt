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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostOptionsBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        tonalElevation = 8.dp,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color(0xFF008080).copy(alpha = 0.4f))
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
                    color = Color(0xFF008080) // Pivota Teal
                )
            )
            Text(
                text = "Select a category to connect with your audience",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    letterSpacing = 0.2.sp
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // MVP1 Pillar: Employment
            PostOptionItem(
                title = "Post a Job",
                subtitle = "Find talent, interns, or offer training",
                icon = Icons.Rounded.BusinessCenter,
                onClick = { onOptionSelected("jobs") }
            )

            // MVP1 Pillar: Housing
            PostOptionItem(
                title = "Post a House",
                subtitle = "List apartments, land plots, or rentals",
                icon = Icons.Rounded.HomeWork,
                onClick = { onOptionSelected("housing") }
            )

            // MVP1 Pillar: Help & Support
            PostOptionItem(
                title = "Post for Help",
                subtitle = "Social services, NGO programs, or aid",
                icon = Icons.Rounded.Handshake,
                onClick = { onOptionSelected("support") }
            )

            // New: Service Offering (For Service Providers)
            PostOptionItem(
                title = "Post a Service",
                subtitle = "Plumbing, moving, legal, or professional help",
                icon = Icons.Rounded.Plumbing,
                onClick = { onOptionSelected("service") }
            )
        }
    }
}

@Composable
private fun PostOptionItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        color = Color(0xFFF8FBFB), // Very light teal tint
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
                            colors = listOf(Color(0xFF008080), Color(0xFF005A5A))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF666666),
                        lineHeight = 16.sp
                    )
                )
            }

            // Subtle arrow to indicate action
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF008080).copy(alpha = 0.3f)
            )
        }
    }
}