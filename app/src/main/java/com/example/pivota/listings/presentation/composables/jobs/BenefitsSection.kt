package com.example.pivota.listings.presentation.composables.jobs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pivota.listings.presentation.viewmodel.PostJobViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BenefitsSection(viewModel: PostJobViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Professional list of benefits for MVP1
    val commonBenefits = listOf(
        "Meals provided",
        "Accommodation",
        "Transport allowance",
        "Flexible hours",
        "Training provided",
        "Tools provided",
        "Health insurance",
        "Airtime allowance"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Benefits",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Benefits help workers decide and apply faster",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // FlowRow is perfect here—it wraps pills to the next line automatically
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            commonBenefits.forEach { benefit ->
                val isSelected = uiState.benefits.contains(benefit)

                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.toggleBenefit(benefit) },
                    label = {
                        Text(
                            text = benefit,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF006565),
                        selectedLabelColor = Color.White,
                        labelColor = Color.DarkGray
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color.LightGray,
                        selectedBorderColor = Color(0xFF006565)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Additional Notes for Benefits
        OutlinedTextField(
            value = uiState.additionalNotes ?: "",
            onValueChange = { /* viewModel.updateAdditionalNotes(it) */ },
            label = { Text("Other Benefits / Notes") },
            placeholder = { Text("e.g. Weekly bonuses for early completion") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF006565)
            ),
            minLines = 2
        )
    }
}