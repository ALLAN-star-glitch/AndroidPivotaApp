package com.example.pivota.dashboard.presentation.composables.client_admin_composables.listings_composables.jobs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobTopBar(
    onBack: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Create Job Post",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary // Pivota Teal / African Sapphire
                )
                Text(
                    text = "Step 1 of 1", // Simplified for MVP1
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colorScheme.primary
                )
            }
        },
        actions = {
            // Contextual help for managers
            IconButton(onClick = { /* Open Help BottomSheet */ }) {
                Icon(
                    imageVector = Icons.Outlined.HelpOutline,
                    contentDescription = "Help",
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colorScheme.surface,
            scrolledContainerColor = colorScheme.surface
        )
    )
}