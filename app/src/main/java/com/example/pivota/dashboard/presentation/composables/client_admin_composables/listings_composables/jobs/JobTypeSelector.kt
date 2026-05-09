package com.example.pivota.dashboard.presentation.composables.client_admin_composables.listings_composables.jobs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pivota.R
import com.example.pivota.core.presentations.composables.UniversalSegmentedToggle
import com.example.pivota.dashboard.domain.model.listings_models.jobs.JobType

@Composable
fun JobTypeSelector(
    selectedType: JobType,
    onTypeSelected: (JobType) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "What kind of role are you hiring for?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary // Pivota Teal / African Sapphire
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Reusing the Core UniversalSegmentedToggle
        UniversalSegmentedToggle(
            options = listOf(JobType.INFORMAL, JobType.FORMAL),
            selected = selectedType,
            onSelect = onTypeSelected,
            labelProvider = { type ->
                if (type == JobType.INFORMAL) "Casual / Informal" else "Formal Job"
            },
            iconProvider = { type, tintColor ->
                Icon(
                    painter = painterResource(
                        if (type == JobType.INFORMAL) R.drawable.ic_casual else R.drawable.ic_formal
                    ),
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        )
    }
}