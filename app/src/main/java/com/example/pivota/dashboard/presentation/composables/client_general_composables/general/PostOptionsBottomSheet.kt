package com.example.pivota.dashboard.presentation.composables.client_general_composables.general

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight() // Fill the sheet height
                .padding(horizontal = 20.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.Start
        ) {
            // Fixed Header (non-scrollable)
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

            // Scrollable Options
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    PostOptionItem(
                        title = "Post a Job",
                        subtitle = "Find talent, interns, or offer training",
                        icon = Icons.Rounded.BusinessCenter,
                        onClick = { onOptionSelected("jobs") },
                        colorScheme = colorScheme
                    )
                }

                item {
                    PostOptionItem(
                        title = "Post a House",
                        subtitle = "List apartments, land plots, or rentals",
                        icon = Icons.Rounded.HomeWork,
                        onClick = { onOptionSelected("housing") },
                        colorScheme = colorScheme
                    )
                }

                item {
                    PostOptionItem(
                        title = "Post for Help",
                        subtitle = "Social services, NGO programs, or aid",
                        icon = Icons.Rounded.Handshake,
                        onClick = { onOptionSelected("support") },
                        colorScheme = colorScheme
                    )
                }

                item {
                    PostOptionItem(
                        title = "Post a Service",
                        subtitle = "Plumbing, moving, legal, or professional help",
                        icon = Icons.Rounded.Plumbing,
                        onClick = { onOptionSelected("service") },
                        colorScheme = colorScheme
                    )
                }

                // Add bottom padding as the last item
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
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
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        color = colorScheme.primary.copy(alpha = 0.05f),
        border = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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