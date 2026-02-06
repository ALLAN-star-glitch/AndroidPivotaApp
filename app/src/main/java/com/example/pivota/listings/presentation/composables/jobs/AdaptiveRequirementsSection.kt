package com.example.pivota.listings.presentation.composables.jobs


import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pivota.listings.domain.models.DocumentType
import com.example.pivota.listings.domain.models.JobType
import com.example.pivota.listings.presentation.state.PostJobUiState
import com.example.pivota.listings.presentation.viewmodel.PostJobViewModel
import androidx.compose.ui.Alignment

@Composable
fun AdaptiveRequirementsSection(viewModel: PostJobViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Job Requirements",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Professional transition between Informal and Formal logic
        AnimatedContent(
            targetState = uiState.jobType,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "requirements_pivot"
        ) { targetType ->
            if (targetType == JobType.INFORMAL) {
                InformalRequirements(uiState, viewModel)
            } else {
                FormalRequirements(uiState, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InformalRequirements(uiState: PostJobUiState, viewModel: PostJobViewModel) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Handyman, contentDescription = null, tint = Color(0xFF006565), modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Equipment & Tools", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(12.dp))

        val equipmentOptions = listOf("Safety Gear", "Hand Tools", "Power Tools", "Smartphone", "Vehicle")

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            equipmentOptions.forEach { tool ->
                FilterChip(
                    selected = uiState.equipmentRequired.contains(tool),
                    onClick = { /* viewModel.toggleEquipment(tool) */ },
                    label = { Text(tool) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FormalRequirements(uiState: PostJobUiState, viewModel: PostJobViewModel) {
    Column {
        // Experience & Education
        OutlinedTextField(
            value = uiState.experienceLevel ?: "",
            onValueChange = { /* update */ },
            label = { Text("Experience Level") },
            placeholder = { Text("e.g. 2+ Years, Mid-level") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Document Multi-select
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF006565), modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Required Documents", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
        Text("Worker will be asked to provide these during application", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        Spacer(Modifier.height(12.dp))

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DocumentType.entries.forEach { docType ->
                val isSelected = uiState.documentsNeeded.contains(docType)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.toggleDocument(docType) },
                    label = {
                        val labelText = docType.name.lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            .replace("_", " ")

                        Text(text = labelText)
                    },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedLeadingIconColor = Color.White,
                        selectedContainerColor = Color(0xFF006565),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}