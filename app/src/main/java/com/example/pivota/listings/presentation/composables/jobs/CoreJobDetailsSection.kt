package com.example.pivota.listings.presentation.composables.jobs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pivota.listings.domain.models.JobType
import com.example.pivota.listings.presentation.viewmodel.PostJobViewModel

@Composable
fun CoreJobDetailsSection(viewModel: PostJobViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF006565), // Pivota Teal
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedLabelColor = Color(0xFF006565),
        cursorColor = Color(0xFF006565)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Core Details",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📝 Job Title
        OutlinedTextField(
            value = uiState.title,
            onValueChange = { viewModel.updateTitle(it) },
            label = { Text("Job Title") },
            placeholder = {
                Text(if (uiState.jobType == JobType.INFORMAL) "e.g. Mason, House Helper" else "e.g. Office Manager")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📂 Category Picker (Simulated as a clickable field)
        OutlinedTextField(
            value = "Construction & Trades", // This would come from uiState.categoryId
            onValueChange = {},
            label = { Text("Category") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            trailingIcon = {
                Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = Color.Gray)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📍 Location: City & Neighborhood (Row for efficiency)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = uiState.locationCity,
                onValueChange = { /* viewModel update */ },
                label = { Text("City") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                singleLine = true
            )
            OutlinedTextField(
                value = uiState.locationNeighborhood,
                onValueChange = { /* viewModel update */ },
                label = { Text("Neighborhood") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🌐 Remote Toggle
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFF6FAF9),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Map, contentDescription = null, tint = Color(0xFF006565))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Remote Work", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Text("Worker can perform duties from home", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
                Switch(
                    checked = uiState.isRemote,
                    onCheckedChange = { /* viewModel toggle */ },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF006565))
                )
            }
        }
    }
}