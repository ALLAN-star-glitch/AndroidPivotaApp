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
import com.example.pivota.welcome.presentation.state.HousingSeekerFormData

@Composable
fun HousingSeekerFields(
    data: HousingSeekerFormData,
    onDataChange: (HousingSeekerFormData) -> Unit
) {
    // Search Type Options
    val searchTypes = listOf("RENTAL", "SALE", "BOTH")

    // Property Types (multi-select)
    val propertyTypes = listOf(
        "APARTMENT", "HOUSE", "BEDSITTER", "ROOM",
        "STUDIO", "TOWNHOUSE", "LAND", "CONDO", "VILLA"
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
                text = "PURPOSE DETAILS: Housing Seeker",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Search Type (Rent / Sale / Both)
            Text(
                text = "What are you looking for?",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(searchTypes) { type ->
                    FilterChip(
                        selected = data.searchType == type,
                        onClick = {
                            val updatedType = if (data.searchType == type) "" else type
                            onDataChange(
                                data.copy(
                                    searchType = updatedType,
                                    isLookingForRental = updatedType == "RENTAL" || updatedType == "BOTH",
                                    isLookingToBuy = updatedType == "SALE" || updatedType == "BOTH"
                                )
                            )
                        },
                        label = {
                            Text(
                                when(type) {
                                    "RENTAL" -> "For Rent"
                                    "SALE" -> "For Sale"
                                    else -> "Both"
                                }
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Property Type (multi-select)
            Text(
                text = "Property Type",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(top = 8.dp)
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(propertyTypes) { type ->
                    FilterChip(
                        selected = data.propertyTypes.contains(type),
                        onClick = {
                            val updated = if (data.propertyTypes.contains(type)) {
                                data.propertyTypes.filter { it != type }
                            } else {
                                data.propertyTypes + type
                            }
                            onDataChange(data.copy(propertyTypes = updated))
                        },
                        label = { Text(type.replaceFirstChar { it.uppercase() }) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            if (data.propertyTypes.isNotEmpty()) {
                Text(
                    text = "${data.propertyTypes.size} type${if (data.propertyTypes.size > 1) "s" else ""} selected",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}