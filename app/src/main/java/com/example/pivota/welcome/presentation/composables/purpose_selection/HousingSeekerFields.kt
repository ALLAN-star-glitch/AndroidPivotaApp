package com.example.pivota.welcome.presentation.composables.purpose_selection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pivota.welcome.presentation.state.HousingSeekerFormData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HousingSeekerFields(
    data: HousingSeekerFormData,
    onDataChange: (HousingSeekerFormData) -> Unit
) {
    var currentAreaInput by remember { mutableStateOf("") }
    var showDuplicateError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val areasList = remember(data.preferredAreas) {
        data.preferredAreas.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    fun addArea(
        currentInput: String,
        currentList: List<String>,
        onAdd: (String) -> Unit,
        onClearInput: () -> Unit
    ) {
        val trimmedItem = currentInput.trim()
        if (trimmedItem.isNotEmpty() && !currentList.contains(trimmedItem)) {
            val newItems = if (currentList.isEmpty()) {
                trimmedItem
            } else {
                "${currentList.joinToString(", ")}, $trimmedItem"
            }
            onAdd(newItems)
            onClearInput()
            showDuplicateError = false
        } else if (currentList.contains(trimmedItem)) {
            showDuplicateError = true
            coroutineScope.launch {
                delay(2000)
                showDuplicateError = false
            }
        }
    }

    fun removeArea(
        areaToRemove: String,
        currentList: List<String>,
        onRemove: (String) -> Unit
    ) {
        val newItems = currentList.filter { it != areaToRemove }.joinToString(", ")
        onRemove(newItems)
    }

    // Search Type Options
    val searchTypes = listOf("RENTAL", "SALE", "BOTH")

    // Property Types (multi-select)
    val propertyTypes = listOf(
        "APARTMENT", "HOUSE", "BEDSITTER", "ROOM",
        "STUDIO", "TOWNHOUSE", "LAND", "CONDO", "VILLA"
    )

    // Area Suggestions
    val areaSuggestions = listOf(
        "Kilimani", "Kileleshwa", "Westlands", "Lavington", "Karen",
        "Ruiru", "Thika", "Kiambu", "Embakasi", "Donholm",
        "Buruburu", "Lang'ata", "South B", "South C", "Parklands"
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

            // Preferred Areas
            AreaInputSection(
                title = "Preferred Areas",
                itemsList = areasList,
                currentInput = currentAreaInput,
                onCurrentInputChange = { currentAreaInput = it },
                onAddItem = { area ->
                    addArea(area, areasList,
                        { onDataChange(data.copy(preferredAreas = it)) }) {
                        currentAreaInput = ""
                    }
                },
                onRemoveItem = {
                    removeArea(it, areasList,
                        { onDataChange(data.copy(preferredAreas = it)) })
                },
                placeholder = "e.g., Kilimani, Westlands",
                suggestions = areaSuggestions,
                showDuplicateError = showDuplicateError
            )
        }
    }
}

@Composable
fun AreaInputSection(
    title: String,
    itemsList: List<String>,
    currentInput: String,
    onCurrentInputChange: (String) -> Unit,
    onAddItem: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    placeholder: String,
    suggestions: List<String>,
    showDuplicateError: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        )

        // Items Pills Row
        if (itemsList.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(itemsList) { item ->
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                item,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onRemoveItem(item) },
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Input Field
        OutlinedTextField(
            value = currentInput,
            onValueChange = {
                onCurrentInputChange(it)
            },
            label = {
                Text(
                    if (itemsList.isEmpty()) "Add $title" else "Add another $title",
                    color = if (showDuplicateError)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            placeholder = {
                Text(
                    placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (currentInput.isNotBlank()) {
                            onAddItem(currentInput)
                        }
                    },
                    enabled = currentInput.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        tint = if (currentInput.isNotBlank())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (currentInput.isNotBlank()) {
                        onAddItem(currentInput)
                    }
                }
            ),
            isError = showDuplicateError,
            supportingText = {
                if (showDuplicateError) {
                    Text(
                        "This area has already been added",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (itemsList.isNotEmpty()) {
                    Text(
                        "${itemsList.size} area${if (itemsList.size > 1) "s" else ""} added",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error
            )
        )

        // Suggestions chips
        if (suggestions.isNotEmpty()) {
            Text(
                text = "Suggestions:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        onClick = {
                            if (!itemsList.contains(suggestion)) {
                                onAddItem(suggestion)
                            }
                        },
                        label = {
                            Text(
                                suggestion,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        enabled = !itemsList.contains(suggestion),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }
    }
}