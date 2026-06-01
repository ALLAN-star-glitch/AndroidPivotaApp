package com.example.pivota.dashboard.presentation.composables.client_general_composables.general

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostOptionsBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colorScheme.surface,
        tonalElevation = 8.dp,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = colorScheme.primary.copy(alpha = 0.4f))
        },
        // Increase the sheet height by controlling the content window
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 500.dp, max = 700.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .navigationBarsPadding()
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Header with expand/collapse hint
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Post a New Listing",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp,
                            color = colorScheme.primary
                        )
                    )
                    Text(
                        text = "Select a category to connect with your audience",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = colorScheme.onSurfaceVariant,
                            letterSpacing = 0.2.sp
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Expand/Collapse button
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            when (sheetState.currentValue) {
                                SheetValue.Expanded -> sheetState.partialExpand()
                                else -> sheetState.expand()
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (sheetState.currentValue == SheetValue.Expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (sheetState.currentValue == SheetValue.Expanded) "Collapse" else "Expand",
                        tint = colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            // Hint text at bottom
            Text(
                text = "Pull up to expand • Pull down to close",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
    colorScheme: ColorScheme
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        color = colorScheme.primary.copy(alpha = 0.05f),
        border = null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                colorScheme.primary,
                                colorScheme.primary.copy(alpha = 0.8f)
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

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = colorScheme.primary.copy(alpha = 0.3f)
            )
        }
    }
}