package com.example.pivota.welcome.presentation.composables.purpose_selection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.welcome.presentation.screens.EmployerData
import com.example.pivota.welcome.presentation.state.EmployerFormData

@Composable
fun EmployerFields(
    data: EmployerFormData,
    onDataChange: (EmployerFormData) -> Unit
) {

    val industries = listOf(
        "Construction", "Tech", "Healthcare",
        "Manufacturing", "Education",
        "Agriculture", "Hospitality", "Retail"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Quick Setup",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Business Name
            OutlinedTextField(
                value = data.businessName,
                onValueChange = {
                    onDataChange(data.copy(businessName = it))
                },
                label = { Text("Business Name") },
                placeholder = { Text("e.g., Wanjiku Hardware") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Industry
            Text(
                text = "Industry",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(industries) { industry ->
                    FilterChip(
                        selected = data.industrySector == industry,
                        onClick = {
                            onDataChange(data.copy(industrySector = industry))
                        },
                        label = { Text(industry) }
                    )
                }

                item {
                    FilterChip(
                        selected = data.industrySector == "Other",
                        onClick = {
                            onDataChange(data.copy(industrySector = "Other"))
                        },
                        label = { Text("Other") }
                    )
                }
            }

            // Optional: Other Industry input
            if (data.industrySector == "Other") {
                OutlinedTextField(
                    value = data.otherIndustry,
                    onValueChange = {
                        onDataChange(data.copy(otherIndustry = it))
                    },
                    label = { Text("Specify Industry") },
                    placeholder = { Text("Enter your industry") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}